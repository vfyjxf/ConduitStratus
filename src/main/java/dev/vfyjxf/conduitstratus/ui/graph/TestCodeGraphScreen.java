package dev.vfyjxf.conduitstratus.ui.graph;

import dev.vfyjxf.cloudlib.api.ui.InputContext;
import dev.vfyjxf.cloudlib.api.ui.widgets.Widget;
import dev.vfyjxf.cloudlib.api.ui.widgets.WidgetGroup;
import dev.vfyjxf.cloudlib.helper.RenderHelper;
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

    private TestCodeGraphScreen() {
        mainGroup.onRender((graphics, mouseX, mouseY, partialTicks, context) -> {
            RenderHelper.drawSolidRect(graphics, 0, 0, mainGroup.getWidth(), mainGroup.getHeight(), 0xff282c34);
        });

        var methodTab = new WidgetGroup<>();
        {
            methodTab.onInit((self) -> {
                methodTab.setId("methodTab");
                methodTab.setPos(0, 0);
                methodTab.setSize(mainGroup.getWidth() * 0.15, mainGroup.getHeight());
            });
            methodTab.onRender(((graphics, mouseX, mouseY, partialTicks, context) -> {
                RenderHelper.drawSolidRect(graphics, 0, 0, methodTab.getWidth(), methodTab.getHeight(), 0xff1a1a1a);
            }));
            for (int i = 0; i < testLabels.size(); i++) {
                var labelWidget = Widget.create();
                var text = testLabels.get(i);
                labelWidget.onRender((graphics, mouseX, mouseY, partialTicks, context) -> {
                            int posX = methodTab.getWidth() / 2 - font.width(text) / 2;
                            graphics.drawString(font, text, posX, 0, 0xffffff);
                        })
                        .setPos(0, i * 20)
                        .setSize(methodTab.getWidth(), 20)
                        .asChild(methodTab);
            }
        }
        methodTab.asChild(mainGroup);

        var codeGraph = new WidgetGroup<>();
        codeGraph.asChild(mainGroup);
    }

    @SubscribeEvent
    private static void onInputKey(InputEvent.Key event) {
        InputContext inputContext = InputContext.fromEvent(event);
        if (inputContext.released(openTestScreen)) {
            Minecraft.getInstance().setScreen(new TestCodeGraphScreen());
        }
    }

}
