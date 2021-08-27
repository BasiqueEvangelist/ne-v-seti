package me.basiqueevangelist.nevseti.util;

import net.fabricmc.fabric.api.event.Event;

public class SignallingEvent<T> extends Event<T> {
    private final Event<T> wrapped;
    private boolean signalled = false;

    public SignallingEvent(Event<T> wrapped) {
        this.wrapped = wrapped;
        invoker = wrapped.invoker();
    }

    @Override
    public void register(T listener) {
        signalled = true;

        wrapped.register(listener);

        invoker = wrapped.invoker();
    }

    public boolean hasSignalled() {
        return signalled;
    }
}
