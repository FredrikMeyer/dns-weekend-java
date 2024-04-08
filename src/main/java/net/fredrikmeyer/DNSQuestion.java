package net.fredrikmeyer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public record DNSQuestion(byte[] name,
                          ResourceType type,
                          ResourceClass clazz) {

    /**
     * Encodes a domain name into a byte array according to the DNS encoding rules.
     * Example: www.example.com -> 3 w w w 7 e x a m p l e 3 c o m,
     * Then replace characters with their ASCII values.
     * Return byte array.
     *
     * @param name The domain name to encode.
     * @return The encoded byte array representing the domain name.
     */
    public static byte[] encodeName(String name) {
        var bytePairs = Arrays
                .stream(name.split("\\."))
                .map(t -> {
                    var byteArray = t.getBytes(StandardCharsets.US_ASCII);
                    byte partLength = (byte) byteArray.length;
                    return ByteBuffer.allocate(1 + partLength).put(partLength).put(byteArray).array();
                })
                .collect(() -> new ArrayList<Byte>(),
                        (acc, curr) -> {
                            for (byte b : curr) {
                                acc.add(b);
                            }
                        },
                        ArrayList::addAll);
        bytePairs.add((byte) 0x00);

        Object[] array = bytePairs.toArray();

        byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = (byte) array[i];
        }
        return result;
    }


    public byte[] toBytes() {
        return ByteBuffer
                .allocate(name.length + 4)
                .put(name)
                .putShort((short) type.value)
                .putShort((short) clazz.value)
                .array();
    }


    public static DNSQuestion parseBytes(ByteBuffer byteBuffer) throws IOException {
        byte[] nameBytes = DNSRecord.decodeName(byteBuffer);

        ResourceType type = ResourceType.fromValue(byteBuffer.getChar());

        ResourceClass clazz = ResourceClass.fromValue(byteBuffer.getChar());
        return new DNSQuestion(nameBytes,
                type,
                clazz);
    }

    @Override
    public String toString() {
        String format = "%32s%32s%32s";
        return "\n" + "DNSQuestion: \n" + String.format(format,
                "Name",
                "Type",
                "Class") + "\n" + String.format(format,
                new String(name),
                type,
                clazz) + "\n";
    }
}
