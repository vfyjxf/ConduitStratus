package dev.vfyjxf.conduitstratus.conduit.traits.item;

public class ItemResponse {

    public static ItemResponse pass() {
        return Pass.INSTANCE;
    }

    public static ItemResponse fail(String reason) {
        return new Fail(reason);
    }

    public static ItemResponse fail() {
        return Fail.UNKNOWN;
    }

    public static ItemResponse illegal(String reason) {
        throw new IllegalStateException(reason);
    }

    public static class Pass extends ItemResponse {

        public static final Pass INSTANCE = new Pass();

        private Pass() {
        }

    }

    public static class Fail extends ItemResponse {

        public static final Fail UNKNOWN = new Fail("Unknown");

        private final String reason;

        public Fail(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }

    }

}
