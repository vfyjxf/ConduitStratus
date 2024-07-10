package dev.vfyjxf.conduitstratus.api.conduit;

public interface IConduitCapability<T> {

    Class<? extends T> getHandledType();

}
