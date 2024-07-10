package dev.vfyjxf.conduitstratus.api.event;

public sealed interface IEventContext permits IEventContext.Common, IEventContext.Cancelable, IEventContext.Interruptible {

    IEventChannel<?> getChannel();

    @SuppressWarnings("unchecked")
    default <T> T holder() {
        return (T) getChannel().handler();
    }

    boolean isCancelled();

    boolean isInterrupted();

    final class Common implements IEventContext {

        private final IEventChannel<?> channel;
        private boolean cancelled = false;
        private boolean interrupted = false;

        public Common(IEventChannel<?> channel) {
            this.channel = channel;
        }

        @Override
        public IEventChannel<?> getChannel() {
            return channel;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
            interrupted = true;
        }

        public void interrupt() {
            interrupted = true;
        }

        @Override
        public boolean isInterrupted() {
            return interrupted;
        }
    }

    final class Cancelable implements IEventContext {

        private final IEventChannel<?> channel;
        private boolean cancelled = false;

        public Cancelable(IEventChannel<?> channel) {
            this.channel = channel;
        }

        @Override
        public IEventChannel<?> getChannel() {
            return channel;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public boolean isInterrupted() {
            return cancelled;
        }

    }

    final class Interruptible implements IEventContext {

        private final IEventChannel<?> channel;
        private boolean interrupted = false;

        public Interruptible(IEventChannel<?> channel) {
            this.channel = channel;
        }

        @Override
        public IEventChannel<?> getChannel() {
            return channel;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public void interrupt() {
            interrupted = true;
        }

        @Override
        public boolean isInterrupted() {
            return interrupted;
        }
    }
}