package net.fredrikmeyer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.toObject;

public class Util {
    public static byte[] convertToPrimitive(Byte[] containers) {
        byte[] result = new byte[containers.length];
        for (int i = 0; i < containers.length; i++) {
            result[i] = containers[i];
        }
        return result;
    }

    public static byte[] convertToPrimitive(Object[] array) {
        Byte[] containers = Arrays.stream(array).map(obj -> (Byte) obj).toArray(Byte[]::new);

        return convertToPrimitive(containers);
    }

    public static byte[] convertToPrimitive(List<Byte> array) {
        return convertToPrimitive(array.toArray());
    }

    public static String ipToString(byte[] data) {
        return Arrays
                .stream(toObject(data))
                .map(i -> String.valueOf(i & 0xFF)) // Trick to get unsigned int
                .collect(Collectors.joining("."));
    }
}
