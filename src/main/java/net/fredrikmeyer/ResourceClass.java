package net.fredrikmeyer;

import java.util.HashMap;
import java.util.Map;

public enum ResourceClass {
    IN(1);

    public final int value;

    private static final Map<Integer, ResourceClass> lookup = new HashMap<>();

    static {
        for (ResourceClass type : ResourceClass.values()) {
            lookup.put(type.value,
                    type);
        }
    }

    ResourceClass(int value) {
        this.value = value;
    }

    public static ResourceClass fromValue(int i) {
        return lookup.get(i);
    }
}
