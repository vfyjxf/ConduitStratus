package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import java.util.EnumSet;

@ApiStatus.NonExtendable
public interface NetworkNode {

    String identifier();

    Network getNetwork();

    BlockEntity getHolder();

    default BlockPos getPos() {
        return getHolder().getBlockPos();
    }

    default ServerLevel getLevel() {
        return (ServerLevel) getHolder().getLevel();
    }

    /**
     * if a node is positive, it has connected to a positive node or has a trait.
     *
     * @return whether the node is positive
     */
    boolean positive();

    void addTrait(Direction direction, Trait trait);

    boolean hasTrait(TraitType type);

    /**
     * @param type the trait type
     * @return the list create the attached traits, if the type is not attached, an empty list will be returned.
     */
    @Unmodifiable
    MutableMap<Direction, ? extends Trait> getTraits(TraitType type);

    @Unmodifiable
    MutableList<? extends Trait> getTraits(Direction direction);

    @Unmodifiable
    MutableMap<Direction, MutableList<Trait>> allTraits();

    @Nullable
    <T, C> T poxyCapability(BlockCapability<T, C> capability, @Nullable C context);


    void destroy();

//    void visit(NetworkNodeVisitor visitor);

    /**
     * @param direction the direction to check
     * @return Whether the node can connect to the given direction with a {@link TraitConnection}
     */
    boolean canWorkWith(Direction direction);

    void tick();

    void onNetworkDestroy();

    void resetNetwork();
}
