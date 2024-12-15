package dev.vfyjxf.conduitstratus.ui.graph;

import dev.vfyjxf.cloudlib.api.ui.layout.ColumnResizer;
import dev.vfyjxf.cloudlib.api.ui.widgets.Widget;
import dev.vfyjxf.cloudlib.api.ui.widgets.WidgetGroup;
import dev.vfyjxf.cloudlib.helper.RenderHelper;
import dev.vfyjxf.cloudlib.ui.BaseScreen;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

public class TestCodeGraphScreen extends BaseScreen {

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
        methodTab.setId("methodTab");
        methodTab.withModifier(
                Modifier().fillMaxSize(0.15, 1)
                        .layoutWith(ColumnResizer::new, layout -> layout.width(200).height(100))
        );
        methodTab.asChild(mainGroup);
        {
            methodTab.onRender(((graphics, mouseX, mouseY, partialTicks, context) -> {
                RenderHelper.drawSolidRect(graphics, 0, 0, methodTab.getWidth(), methodTab.getHeight(), 0xff1a1a1a);
            }));

            var baseModifier = Modifier().fillMaxWidth(1);
            for (int i = 0; i < testLabels.size(); i++) {
                var labelWidget = Widget.create()
                        .withModifier(baseModifier.pos(0, i * 20).heightFixed(20));
                var text = testLabels.get(i);
                labelWidget.onRender((graphics, mouseX, mouseY, partialTicks, context) -> {
                            int posX = methodTab.getWidth() / 2 - font.width(text) / 2;
                            graphics.drawString(font, text, posX, 0, 0xffffff);
                        })
                        .asChild(methodTab);
            }
        }

        var codeGraph = new WidgetGroup<>();
        codeGraph.withModifier(
                Modifier().posRelX(0.15)
                        .fillMaxWidth(0.85)
                        .fillMaxHeight(1)
        );
        codeGraph.asChild(mainGroup);
    }

}
