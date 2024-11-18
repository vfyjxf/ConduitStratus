package dev.vfyjxf.conduitstratus.client.models;

import com.mojang.math.Transformation;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * From RefinedStorage2
 */
public class TransformationBuilder {
    private final List<Transformation> transforms = new ArrayList<>();

    private TransformationBuilder() {
    }

    public static TransformationBuilder create() {
        return new TransformationBuilder();
    }

    public TransformationBuilder translate(final Vector3f translation) {
        transforms.add(new Transformation(translation, null, null, null));
        return this;
    }

    public TransformationBuilder rotate(final dev.vfyjxf.conduitstratus.utils.BiDirection direction) {
        transforms.add(new Transformation(null, direction.getQuaternion(), null, null));
        return this;
    }

    public Transformation build() {
        Transformation result = Transformation.identity();
        for (final Transformation child : transforms) {
            result = result.compose(child);
        }
        return result;
    }
}
