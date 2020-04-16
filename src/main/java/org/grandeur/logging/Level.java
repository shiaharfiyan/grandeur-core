package io.github.shiaharfiyan.logging;

import io.github.shiaharfiyan.utils.helpers.ObjectHelper;

public enum Level {
    OFF(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    TRACE(5),
    ALL(6);

    private int value;

    Level(int value) {
        this.value = value;
    }

    public static Level Find(String name) {
        for (Level l : Level.values()) {
            if (name.toUpperCase().equals(l.name())) {
                return l;
            }
        }

        return null;
    }

    public static Level FindWithDefault(String name, Level default_value) {
        Level l = Find(name);
        return l == null ? default_value : l;
    }

    public static Level Lookup(String name) {
        return ObjectHelper.Lookup(Level.class, name);
    }

    public int GetValue() {
        return value;
    }
}
