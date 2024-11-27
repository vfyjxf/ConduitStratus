package dev.vfyjxf.conduitstratus.api.graph;

import net.minecraft.resources.ResourceLocation;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.Unmodifiable;

public class NodePort {


    private final CodeNode owner;
    private final ResourceLocation identifier;
    private final Class<?> type;
    private final boolean input;

    private final MutableList<PortEdge> edges = Lists.mutable.empty();

    private String name;

    public NodePort(CodeNode owner, ResourceLocation identifier, Class<?> type, boolean input) {
        this(owner, identifier, type, input, "port");
    }

    public NodePort(CodeNode owner, ResourceLocation identifier, Class<?> type, boolean input, String name) {
        this.owner = owner;
        this.identifier = identifier;
        this.type = type;
        this.input = input;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CodeNode owner() {
        return owner;
    }

    public ResourceLocation identifier() {
        return identifier;
    }

    public Class<?> type() {
        return type;
    }

    @Unmodifiable
    public MutableList<PortEdge> edges() {
        return edges.asUnmodifiable();
    }

    public void addEdge(PortEdge edge) {
        edges.add(edge);
    }

    public void removeEdge(PortEdge edge) {
        edges.remove(edge);
    }
}


