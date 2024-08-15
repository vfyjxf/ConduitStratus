package dev.vfyjxf.conduitstratus.utils;

public class Checks {

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkNotNull(Object object, String parameterName) {
        if (object == null) {
            throw new NullPointerException(parameterName + " cannot be null");
        }
    }

    private Checks() {
    }
}
