package dev.vfyjxf.conduitstratus.api.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public sealed interface IEventContext permits IEventContext.Common, IEventContext.Cancelable, IEventContext.Interruptible {

    @ApiStatus.Internal
    IEventChannel<?> getChannel();

    @SuppressWarnings("unchecked")
    default <T> T poster() {
        return (T) getChannel().handler();
    }

    @Nullable
    default <T> T poster(Class<T> type) {
        IEventHandler<?> handler = getChannel().handler();
        if (type.isInstance(handler)) {
            return type.cast(handler);
        }
        return null;
    }

    boolean cancelled();

    boolean interrupted();

    /**
     * Cancellable/Interruptible
     */
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
        public boolean cancelled() {
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
        public boolean interrupted() {
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
        public boolean cancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
        }

        @Override
        public boolean interrupted() {
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
        public boolean cancelled() {
            return false;
        }

        public void interrupt() {
            interrupted = true;
        }

        @Override
        public boolean interrupted() {
            return interrupted;
        }
    }
}
