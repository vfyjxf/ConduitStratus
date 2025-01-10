package dev.vfyjxf.conduitstratus.api.graph;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

public class CodeGraph<N extends CodeNode> {

    private final MutableList<N> nodes = Lists.mutable.empty();

    @Unmodifiable
    public MutableList<N> nodes() {
        return nodes.asUnmodifiable();
    }

    @Contract("_ -> this")
    public CodeGraph<N> addNode(N node) {
        nodes.add(node);
        return this;
    }

    @Contract("_ -> this")
    public CodeGraph<N> removeNode(N node) {
        nodes.remove(node);
        return this;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }


}
