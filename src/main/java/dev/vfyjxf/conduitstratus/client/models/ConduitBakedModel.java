package dev.vfyjxf.conduitstratus.client.models;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.math.Transformation;
import dev.vfyjxf.conduitstratus.conduit.ConnectionState;
import dev.vfyjxf.conduitstratus.utils.BiDirection;
import dev.vfyjxf.conduitstratus.utils.EnumConstant;
import dev.vfyjxf.conduitstratus.utils.Locations;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ConduitBakedModel implements IDynamicBakedModel {

    private static final ResourceLocation CENTER = Locations.of("block/conduit_center");
    private static final ResourceLocation CONNECTION = Locations.of("block/conduit_connection");
    private static final ResourceLocation STRAIGHT = Locations.of("block/conduit_straight");
    private static final ResourceLocation TRAIT_INTERFACE = Locations.of("block/trait_interface");

    private final LoadingCache<ConnectionState, MutableList<BakedQuad>> modelCache;
    private final ModelBaker baker;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final TextureAtlasSprite particleSprite;
    private final List<BakedQuad> centerQuads;
    private final EnumMap<Direction, List<BakedQuad>> connectionQuads;
    private final EnumMap<Direction, List<BakedQuad>> traitQuads;


    public ConduitBakedModel(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, TextureAtlasSprite particleSprite) {
        this.baker = baker;
        this.spriteGetter = spriteGetter;
        this.particleSprite = particleSprite;
        this.centerQuads = Objects.requireNonNull(baker.bake(CENTER, BlockModelRotation.X0_Y0, spriteGetter))
                .getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null);
        this.modelCache = CacheBuilder.newBuilder()
                .build(CacheLoader.from(this::getQuads));
        this.connectionQuads = new EnumMap<>(Direction.class);
        this.traitQuads = new EnumMap<>(Direction.class);
        final RandomSource rand = RandomSource.create();
        for (Direction direction : EnumConstant.directions) {
            connectionQuads.put(direction, getQuads(CONNECTION, direction, baker, rand, spriteGetter));
            traitQuads.put(direction, getQuads(TRAIT_INTERFACE, direction, baker, rand, spriteGetter));
        }
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            RandomSource rand,
            ModelData extraData,
            @Nullable RenderType renderType
    ) {
        ConnectionState connections = extraData.get(ModelProperties.CONDUIT_CONNECTION);
        if (connections == null || side != null) return Collections.emptyList();
        return modelCache.getUnchecked(connections);
    }

    private MutableList<BakedQuad> getQuads(ConnectionState connections) {
        final RandomSource rand = RandomSource.create();
        MutableList<BakedQuad> quads = Lists.mutable.empty();
        if (connections.isStraight()) {
            Direction straightDirection = connections.getStraightDirection();
            Transformation transformation = TransformationBuilder.create().rotate(BiDirection.forDirection(straightDirection))
                    .build();
            ModelState wrappedState = new SimpleModelState(transformation);
            final BakedModel bakedModel = baker.bake(STRAIGHT, wrappedState, spriteGetter);
            if (bakedModel == null) {
                return quads;
            }
            quads.addAll(bakedModel.getQuads(null, null, rand, ModelData.EMPTY, null));
            return quads;
        } else {
            quads.addAll(centerQuads);
            for (Direction direction : connections.connectionSides()) {
                quads.addAll(connectionQuads.get(direction));
            }
            for (Direction traitConnection : connections.traitSides()) {
                quads.addAll(traitQuads.get(traitConnection));
            }
        }
        return quads;
    }

    private static List<BakedQuad> getQuads(ResourceLocation resourceLocation, Direction rotation, ModelBaker modelBaker, RandomSource rand, Function<Material, TextureAtlasSprite> spriteGetter) {
        Transformation transformation = TransformationBuilder.create()
                .rotate(BiDirection.forDirection(rotation))
                .build();
        ModelState wrappedState = new SimpleModelState(transformation);
        final BakedModel bakedModel = modelBaker.bake(resourceLocation, wrappedState, spriteGetter);
        if (bakedModel == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(bakedModel.getQuads(null, null, rand, ModelData.EMPTY, null));
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleSprite;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
