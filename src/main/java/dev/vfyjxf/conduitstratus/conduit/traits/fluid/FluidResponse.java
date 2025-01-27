package dev.vfyjxf.conduitstratus.conduit.traits.fluid;

public class FluidResponse {

    public static FluidResponse pass() {
        return FluidResponse.Pass.INSTANCE;
    }

    public static FluidResponse fail(String reason) {
        return new FluidResponse.Fail(reason);
    }

    public static FluidResponse fail() {
        return FluidResponse.Fail.UNKNOWN;
    }

    public static FluidResponse illegal(String reason) {
        throw new IllegalStateException(reason);
    }

    public static class Pass extends FluidResponse {

        public static final Pass INSTANCE = new Pass();

        private Pass() {
        }

    }

    public static class Fail extends FluidResponse {

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
