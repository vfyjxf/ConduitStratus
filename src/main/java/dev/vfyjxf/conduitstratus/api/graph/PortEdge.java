package dev.vfyjxf.conduitstratus.api.graph;

import org.jetbrains.annotations.Nullable;

public class PortEdge {

    private final int id;
    private CodeNode owner;
    private NodePort from;
    private NodePort to;
    private Object toPass;

    public PortEdge(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public CodeNode owner() {
        return owner;
    }

    public NodePort from() {
        return from;
    }

    public CodeNode fromNode() {
        return from.owner();
    }

    public NodePort to() {
        return to;
    }

    public CodeNode toNode() {
        return to.owner();
    }

    @Nullable
    public Object toPass() {
        return toPass;
    }
}
