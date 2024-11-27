package dev.vfyjxf.conduitstratus.api.graph;

import dev.vfyjxf.cloudlib.api.math.Pos;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.Unmodifiable;

public interface CodeNode {

    CodeGraph<?> graph();

    Pos pos();

    String name();

    boolean expanded();

    void setExpanded(boolean expanded);

    @Unmodifiable
    MutableList<NodePort> ports();

}
