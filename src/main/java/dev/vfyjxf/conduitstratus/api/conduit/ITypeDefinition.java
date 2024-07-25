package dev.vfyjxf.conduitstratus.api.conduit;

public interface ITypeDefinition<T> {

    /**
     * @return the typeDef token of the typeDef.
     */
    Class<T> typeToken();

    /**
     * @return whether the typeDef is a final class.
     */
    default boolean classless() {
        return true;
    }

    default boolean support(Class<?> type) {
        if (classless()) {
            return typeToken() == type;
        } else {
            return typeToken().isAssignableFrom(type);
        }
    }

}
