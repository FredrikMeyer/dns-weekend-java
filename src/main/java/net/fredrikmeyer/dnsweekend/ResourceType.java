package net.fredrikmeyer.dnsweekend;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {
    TYPE_A(1),
    // 128-bit IPv6
    TYPE_AAAA(28),
    TYPE_NS(2),
    TYPE_CNAME(5),
    TYPE_AUTHORITY(6),
    TYPE_TXT(16);

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
            System.out.println("Exception. Tried to create resource, but not implemented. Code: " + i);
            throw new NullPointerException();
        }
        return res;
    }

    public static ResourceType fromCode(String code) {
        if (code == null) return null;
        return switch (code) {
            case "TXT" -> ResourceType.TYPE_TXT;
            case "A" -> ResourceType.TYPE_A;
            case "NS" -> ResourceType.TYPE_NS;
            case "CNAMe" -> ResourceType.TYPE_CNAME;
            default -> null;
        };
    }
}
