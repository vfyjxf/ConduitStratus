package dev.vfyjxf.conduitstratus.ui.trait;

import dev.vfyjxf.cloudlib.api.ui.widgets.Widget;
import dev.vfyjxf.cloudlib.test.ui.TestScreen;
import dev.vfyjxf.cloudlib.api.ui.BasicScreen;


@TestScreen
public class TraitConfigScreen extends BasicScreen {

    private TraitConfigScreen() {
        var widget = mainGroup.addWidget(
                Widget.create()
        );
        widget.onRender(((graphics, mouseX, mouseY, partialTicks, context) -> {
        }));
    }

}
