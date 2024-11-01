package dev.vfyjxf.conduitstratus.api.conduit.data;

public interface DataAttachable {

    <T> void attach(DataKey<T> key, T value);
    
    <T> T get(DataKey<T> key);
    
    <T> T getOr(DataKey<T> key, T defaultValue);
    
    <T> T detach(DataKey<T> key);
    
    void clear();
    
    boolean isEmpty();
    
    boolean has(DataKey<?> key);

}
