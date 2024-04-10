package net.fredrikmeyer.dnsweekend;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.toObject;

public record DNSRecord(byte[] name,
                        ResourceType type,
                        ResourceClass clazz,
                        int ttl,
                        byte[] data) {

    public static DNSRecord parseBytes(ByteBuffer bs) throws IOException {
        byte[] name = decodeName(bs);

        ResourceType type = ResourceType.fromValue(bs.getChar());
        ResourceClass clazz = ResourceClass.fromValue(bs.getChar());
        int ttl = bs.getInt();
        int dataLength = bs.getChar();

        byte[] data;
        // Different types have different kind of data
        if (type == ResourceType.TYPE_NS || type == ResourceType.TYPE_CNAME) {
            data = decodeName(bs);
        } else if (type == ResourceType.TYPE_AUTHORITY) {
            // There's a lot more data here. But need to find better way.
            // Probably best to decode completely to a Record type.
            data = decodeName(bs);
            decodeName(bs);
            bs.getInt();
            bs.getInt();
            bs.getInt();
            bs.getInt();
        } else {
            data = new byte[dataLength];
            bs.get(data);
        }
        return new DNSRecord(name,
                type,
                clazz,
                ttl,
                data);
    }

    public static byte[] decodeName(ByteBuffer bb) throws IOException {
        // (64 & 0b11000000) > 0
        List<Byte> bytes = new ArrayList<>();

        int length = bb.get();
        while (length != 0) {
            if ((length & 0b1100_0000) > 0) {
                // decode compressed name
                byte[] compressedName = decodeCompressedName(bb,
                        length);
                bytes.addAll(Arrays.asList(toObject(compressedName)));
                break;
            } else {
                byte[] nameBytes = new byte[length];
                bb.get(nameBytes);
                List<Byte> part = Arrays.stream(toObject(nameBytes)).toList();
                bytes.addAll(part);
                length = bb.get();
            }

            if (length != 0) {
                bytes.add((byte) 46); // Add punctuation mark
            }
        }

        return Util.convertToPrimitive(bytes);
    }

    private static byte[] decodeCompressedName(ByteBuffer bb, final int length) throws IOException {
        var pointerBytes = new byte[]{(byte) (length & 0b0011_1111), bb.get()};
        int pointer = ByteBuffer.wrap(pointerBytes).getChar();
        int currentPos = bb.position();

        bb.position(pointer);

        var result = decodeName(bb);

        bb.position(currentPos);

        return result;
    }


    @Override
    public String toString() {
        String format = "%-25s%-10s%-7s%-10s%-5s";
        return "\n" + "DNSRecord: \n" + String.format(format,
                "Name",
                "Type",
                "Class",
                "TTL",
                "Data") + "\n" + String.format(format,
                new String(name),
                type,
                clazz,
                ttl,
                formatData()) + "\n";
    }

    private String formatData() {
        return switch (type) {
            case TYPE_A -> Util.ipToString(data);
            case TYPE_AUTHORITY, TYPE_CNAME, TYPE_TXT, TYPE_NS -> new String(data);
            case TYPE_AAAA -> {
                try {
                    InetAddress byAddress = Inet6Address.getByAddress(data);
                    yield byAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public boolean isARecord() {
        return type == ResourceType.TYPE_A;
    }
}
