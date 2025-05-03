package dev.vfyjxf.conduitstratus.ui.graph;

import dev.vfyjxf.cloudlib.api.ui.BasicScreen;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

public class TestCodeGraphScreen extends BasicScreen {

    private static final MutableList<String> testLabels = Lists.mutable.of(
            "add", "sub", "mul", "div", "mod", "and", "or",
            "xor", "shl", "shr", "ushr", "neg", "not", "cmp",
            "cmpl", "cmpg", "cmpl", "cmpg", "cmp", "cmpl"
    );

    private TestCodeGraphScreen() {
    }

}
