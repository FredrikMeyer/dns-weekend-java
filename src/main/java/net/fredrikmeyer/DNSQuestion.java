package net.fredrikmeyer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.toObject;

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

    public static byte[] decodeName(final ByteBuffer bais) throws IOException {
        // (64 & 0b11000000) > 0
        ArrayList<Byte> bytes = new ArrayList<>();

        int length = bais.get();
        while (length != 0) {
            if ((length & 0b1100_0000) > 0) {
                // decode compressed name
                byte[] compressedName = decodeCompressedName(bais,
                        length);
                bytes.addAll(Arrays.asList(toObject(compressedName)));
                break;
            } else {
                byte[] nameBytes = new byte[length];
                bais.get(nameBytes);
                List<Byte> part = Arrays.stream(toObject(nameBytes)).toList();
                bytes.addAll(part);
                length = bais.get();
            }

            if (length != 0) {
                bytes.add((byte) 46); // Add punctuation mark
            }
        }

        return Util.convertToPrimitive(bytes);
    }

    private static byte[] decodeCompressedName(final ByteBuffer bb, final int length) throws IOException {
        var pointerBytes = new byte[]{(byte) (length & 0b0011_1111), bb.get()};
        int pointer = ByteBuffer.wrap(pointerBytes).getChar();
        int currentPos = bb.position();

        bb.position(pointer);

        var result = decodeName(bb);

        bb.position(currentPos);

        return result;
    }

    public static DNSQuestion parseBytes(final ByteBuffer byteBuffer) throws IOException {

        byte[] nameBytes = decodeName(byteBuffer);

        ResourceType type = ResourceType.fromValue(byteBuffer.getChar());

        ResourceClass clazz = ResourceClass.fromValue(byteBuffer.getChar());
        return new DNSQuestion(nameBytes,
                type,
                clazz);
    }

    @Override
    public String toString() {
        return "DNSQuestion{" +
                "name=" + new String(name) +
                ", type=" + type +
                ", clazz=" + clazz +
                '}';
    }
}
