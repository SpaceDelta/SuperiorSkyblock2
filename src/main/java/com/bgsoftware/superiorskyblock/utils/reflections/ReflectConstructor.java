package com.bgsoftware.superiorskyblock.utils.reflections;

import java.lang.reflect.Constructor;

public final class ReflectConstructor<T> {

    private final Constructor<?> constructor;

    public ReflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        this.constructor = getConstructor(clazz, parameterTypes);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        Constructor<?> constructor = null;

        if (clazz != null) {
            try {
                constructor = clazz.getDeclaredConstructor(parameterTypes);
                constructor.setAccessible(true);
            } catch (Exception ignored) {
            }
        }

        return constructor;
    }

    public T newInstance(Object... args) {
        Object result = null;

        try {
            if (constructor != null)
                result = constructor.newInstance(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //noinspection unchecked
        return result == null ? null : (T) result;
    }

    public boolean isValid() {
        return constructor != null;
    }

}
