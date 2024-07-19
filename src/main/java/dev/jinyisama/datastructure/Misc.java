package dev.jinyisama.datastructure;

public class Misc {
    public class worldCoordinate {
        private int x;
        private int y;
        private int z;
        public worldCoordinate(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public int getX() {return this.x;}
        public int getY() {return this.y;}
        public int getZ() {return this.z;}
        public void setX(int x) {this.x = x;}
        public void setY(int y) {this.y = y;}
        public void setZ(int z) {this.z = z;}
    }
    public enum NodeColor {
        NODE_COLOR_UNCOLORED,
        NODE_COLOR_WHITE,
        NODE_COLOR_GRAY,
        NODE_COLOR_BLACK,
        NODE_COLOR_RED,
        NODE_COLOR_GREEN,
        NODE_COLOR_BLUE,
        NODE_COLOR_YELLOW,
        NODE_COLOR_PURPLE,
        NODE_COLOR_ORANGE,
        NODE_COLOR_BROWN,
        NODE_COLOR_PINK,
        NODE_COLOR_CYAN,
        NODE_COLOR_LIGHT_GRAY,
        NODE_COLOR_MAGENTA,
        NODE_COLOR_LIGHT_BLUE,
        NODE_COLOR_LIGHT_GREEN,
        NODE_COLOR_LIGHT_YELLOW,
        NODE_COLOR_LIGHT_PURPLE,
        NODE_COLOR_LIGHT_ORANGE,
        NODE_COLOR_LIGHT_BROWN,
        NODE_COLOR_LIGHT_PINK,
        NODE_COLOR_LIGHT_CYAN,

        NODE_COLOR_DARK_GRAY,
        NODE_COLOR_DARK_RED,
        NODE_COLOR_DARK_GREEN,
        NODE_COLOR_DARK_BLUE,
        NODE_COLOR_DARK_PURPLE,
        NODE_COLOR_DARK_ORANGE,
        NODE_COLOR_DARK_BROWN,
        NODE_COLOR_DARK_PINK,
        NODE_COLOR_DARK_CYAN
    }
}
