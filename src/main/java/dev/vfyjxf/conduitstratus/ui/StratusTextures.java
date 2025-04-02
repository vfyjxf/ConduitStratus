package dev.vfyjxf.conduitstratus.ui;

import dev.vfyjxf.cloudlib.ui.textures.NineSliceTexture;
import dev.vfyjxf.cloudlib.ui.textures.RenderableSprite;
import dev.vfyjxf.cloudlib.ui.textures.SpriteUploader;
import dev.vfyjxf.conduitstratus.StratusConstants;
import dev.vfyjxf.conduitstratus.utils.StratusLocations;
import mezz.jei.common.gui.textures.Textures;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

/**
 * Based on {@link  Textures}
 * <p>
 */
public final class StratusTextures {

    private final static SpriteUploader uploader = new SpriteUploader(
            Minecraft.getInstance().getTextureManager(),
            StratusConstants.MOD_ID,
            StratusLocations.of("textures/atlas/gui.png")
    );

    @ApiStatus.Internal
    public static SpriteUploader getUploader() {
        return uploader;
    }


    //region trait config icons
    public static final RenderableSprite settings =
            createSprite(
                    "gui/picture.png",
                    256,
                    256
            );

    //endregion

    private static RenderableSprite createSprite(
            String path,
            int width,
            int height
    ) {
        return new RenderableSprite(
                uploader,
                StratusLocations.of(path),
                width, height
        );
    }

    private static NineSliceTexture createNineSlice(
            String path,
            int width,
            int height,
            int left,
            int right,
            int top,
            int bottom
    ) {
        return new NineSliceTexture(
                uploader,
                StratusLocations.of(path),
                width, height,
                left, right, top, bottom
        );
    }


    private StratusTextures() {
    }
}
