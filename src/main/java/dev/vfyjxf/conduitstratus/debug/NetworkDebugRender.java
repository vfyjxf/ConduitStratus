package dev.vfyjxf.conduitstratus.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

@EventBusSubscriber
public class NetworkDebugRender {
    public static List<DebugPackage.Entry> entries;

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        if (entries == null) {
            return;
        }

        // Render the debug entries

        MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        if (Minecraft.getInstance().level == null) {
            return;
        }
        ResourceKey<Level> level = Minecraft.getInstance().level.dimension();

        PoseStack poseStack = event.getPoseStack();

        for (DebugPackage.Entry entry : entries) {
            if (!level.equals(entry.dimension())) {
                continue;
            }
            // draw each distance number at the position
            BlockPos pos = entry.pos();
            int distance = entry.distance();

            String text = String.valueOf(distance);

            DebugRenderer.renderFloatingText(poseStack, bufferSource, text, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 0xFFFFFFFF);
        }

    }
}
