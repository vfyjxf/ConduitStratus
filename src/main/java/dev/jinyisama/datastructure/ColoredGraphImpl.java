package dev.jinyisama.datastructure;

import java.util.List;

public class ColoredGraphImpl{
    public class ImplOnCPU implements ColoredGraphInterface {
        public List<Misc.worldCoordinate> usedColors() {return null;}
        public boolean deleteNode(Misc.worldCoordinate coordinate) {return false;}
        public boolean isConnectable(Misc.worldCoordinate coordinate) {return false;}
        public boolean insertNode(Misc.worldCoordinate coordinate, Misc.NodeColor color) {return false;}
        public boolean isConnected(Misc.worldCoordinate coordinate1, Misc.worldCoordinate coordinate2) {return false;}
    }
}
