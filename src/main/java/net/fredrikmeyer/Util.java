package net.fredrikmeyer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ArrayUtils.toObject;
import static org.apache.commons.lang3.ArrayUtils.toPrimitive;

public class Util {
    private static byte[] convertToPrimitive(Object[] array) {
        Byte[] containers = Arrays.stream(array).map(obj -> (Byte) obj).toArray(Byte[]::new);

        return toPrimitive(containers);
    }

    public static byte[] convertToPrimitive(List<Byte> array) {
        return convertToPrimitive(array.toArray());
    }

    public static String ipToString(byte[] data) {
        return Arrays.stream(toObject(data)).map(i -> String.valueOf(i & 0xFF)) // Trick to get unsigned int
                .collect(Collectors.joining("."));
    }
}
