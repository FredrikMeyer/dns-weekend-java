package net.fredrikmeyer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public record DNSRecord(byte[] name,
                        ResourceType type,
                        ResourceClass clazz,
                        int ttl,
                        byte[] data) {

    public static DNSRecord parseBytes(ByteBuffer bs) throws IOException {
        byte[] name = DNSQuestion.decodeName(bs);

        ResourceType type = ResourceType.fromValue(bs.getChar());
        ResourceClass clazz = ResourceClass.fromValue(bs.getChar());
        int ttl = bs.getInt();
        int dataLength = bs.getChar();

        byte[] data = new byte[dataLength];
        bs.get(data);
        return new DNSRecord(name,
                type,
                clazz,
                ttl,
                data);
    }

    @Override
    public String toString() {
        return "DNSRecord{" + "name=" + new String(name) + "\n " + ", type=" + type + "\n " + ", clazz=" + clazz + "\n" + ", ttl=" + ttl + "\n" + ", data=\"" + (formatData()) + "\"\n" + '}';
    }

    private String formatData() {
        return switch (type) {
            case TYPE_A -> Util.ipToString(data);
            case TYPE_CNAME -> new String(data);
            default -> Arrays.toString(data);
        };
    }
}
