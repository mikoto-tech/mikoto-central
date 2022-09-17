package net.mikoto.central.util;

import net.mikoto.oauth2.model.annotation.ForbiddenField;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mikoto
 */
public final class ReflectionUtil {
    /**
     * Get the forbidden field list in the class.
     *
     * @param clazz The class you need to check.
     * @return The forbidden field list.
     */
    public static @NotNull List<String> getForbiddenField(@NotNull Class<?> clazz) {
        List<String> resultString = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field :
                fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation :
                    annotations) {
                if (annotation instanceof ForbiddenField) {
                    resultString.add(field.getName());
                    break;
                }
            }
        }
        return resultString;
    }
}
