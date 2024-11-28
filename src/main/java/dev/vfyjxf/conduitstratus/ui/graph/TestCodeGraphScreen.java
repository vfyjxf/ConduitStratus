package dev.vfyjxf.conduitstratus.ui.graph;

import dev.vfyjxf.cloudlib.api.ui.event.WidgetEvent;
import dev.vfyjxf.cloudlib.api.ui.widgets.Widget;
import dev.vfyjxf.cloudlib.api.ui.widgets.WidgetGroup;
import dev.vfyjxf.cloudlib.ui.ModularScreen;
import dev.vfyjxf.conduitstratus.Constants;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

import static dev.vfyjxf.conduitstratus.ConduitStratus.openTestScreen;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class TestCodeGraphScreen extends ModularScreen {

    private static final MutableList<String> testLabels = Lists.mutable.of(
            "add", "sub", "mul", "div", "mod", "and", "or",
            "xor", "shl", "shr", "ushr", "neg", "not", "cmp",
            "cmpl", "cmpg", "cmpl", "cmpg", "cmp", "cmpl"
    );

    @SubscribeEvent
    private static void onInputKey(InputEvent.Key event) {
        if (openTestScreen.matches(event.getKey(), event.getScanCode())) {
            Minecraft.getInstance().setScreen(new TestCodeGraphScreen());
        }
    }

    @Override
    protected void init() {
        super.init();
        var methodTab = new WidgetGroup<>();
        methodTab.setPos(0, 0);
        methodTab.setSize((int) (width * 0.2), height);
        for (int i = 0; i < testLabels.size(); i++) {
            var labelWidget = Widget.create();
            var text = testLabels.get(i);
            labelWidget.onEvent(WidgetEvent.onRender, (graphics, mouseX, mouseY, partialTicks, context) -> {
                graphics.drawString(font, text, 0, 0, 0xffffff);
            });
            labelWidget.setPos(0, i * 20);
            labelWidget.setSize(methodTab.getWidth(), 20);
            labelWidget.asChild(methodTab);
        }
        methodTab.asChild(mainGroup);
        var codeGraph = new WidgetGroup<>();
        codeGraph.onEvent(WidgetEvent.onRender, (graphics, mouseX, mouseY, partialTicks, context) -> {
            graphics.fill(0, 0, codeGraph.getWidth(), codeGraph.getHeight(), 0x282c34);
        });
        codeGraph.asChild(mainGroup);
    }
}
