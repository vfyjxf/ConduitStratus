package dev.vfyjxf.conduitstratus.api.annotations;


import org.jetbrains.annotations.NotNull;

import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@NotNull
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierDefault({ElementType.FIELD})
public @interface FieldNotNullByDefault {
}
