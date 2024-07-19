package dev.jinyisama.datastructure;

import java.util.List;

public interface ColoredGraphInterface {
    List<Misc.worldCoordinate> usedColors();
    boolean deleteNode(Misc.worldCoordinate coordinate);
    boolean isConnectable(Misc.worldCoordinate coordinate);
    boolean insertNode(Misc.worldCoordinate coordinate, Misc.NodeColor color);
    boolean isConnected(Misc.worldCoordinate coordinate1, Misc.worldCoordinate coordinate2);
}
