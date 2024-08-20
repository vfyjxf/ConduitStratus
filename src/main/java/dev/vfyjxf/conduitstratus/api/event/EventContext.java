package dev.vfyjxf.conduitstratus.api.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public sealed interface EventContext permits EventContext.Common, EventContext.Cancelable, EventContext.Interruptible {

    @ApiStatus.Internal
    EventChannel<?> getChannel();

    @SuppressWarnings("unchecked")
    default <T> T poster() {
        return (T) getChannel().handler();
    }

    @Nullable
    default <T> T poster(Class<T> type) {
        EventHandler<?> handler = getChannel().handler();
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
    final class Common implements EventContext {

        private final EventChannel<?> channel;
        private boolean cancelled = false;
        private boolean interrupted = false;

        public Common(EventChannel<?> channel) {
            this.channel = channel;
        }

        @Override
        public EventChannel<?> getChannel() {
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

    final class Cancelable implements EventContext {

        private final EventChannel<?> channel;
        private boolean cancelled = false;

        public Cancelable(EventChannel<?> channel) {
            this.channel = channel;
        }

        @Override
        public EventChannel<?> getChannel() {
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

    final class Interruptible implements EventContext {

        private final EventChannel<?> channel;
        private boolean interrupted = false;

        public Interruptible(EventChannel<?> channel) {
            this.channel = channel;
        }

        @Override
        public EventChannel<?> getChannel() {
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
