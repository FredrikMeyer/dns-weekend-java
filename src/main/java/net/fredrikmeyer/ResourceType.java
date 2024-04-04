package net.fredrikmeyer;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {
    TYPE_A(1),
    TYPE_CNAME(5),
    TYPE_AUTHORITY(6);

    public final int value;

    private static final Map<Integer, ResourceType> lookup = new HashMap<>();

    static {
        for (ResourceType type : ResourceType.values()) {
            lookup.put(type.value,
                    type);
        }
    }

    ResourceType(int value) {
        this.value = value;
    }

    public static ResourceType fromValue(int i) {
        var res = lookup.get(i);
        if (res == null) {
            throw new NullPointerException();
        }
        return res;
    }
}
