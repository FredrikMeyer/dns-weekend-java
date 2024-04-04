package net.fredrikmeyer;

import java.nio.ByteBuffer;

public record DNSHeader(int id,
                        int flags,
                        int numQuestions,
                        int numAnswers,
                        int numAuthorities,
                        int numAdditionals) {

    public byte[] toBytes() {
        return ByteBuffer
                .allocate(12)
                .putShort((short) id)
                .putShort((short) flags)
                .putShort((short) numQuestions)
                .putShort((short) numAnswers)
                .putShort((short) numAuthorities)
                .putShort((short) numAdditionals)
                .array();
    }

    public static DNSHeader parseHeader(ByteBuffer bs) {
        return new DNSHeader(bs.getChar(),
                bs.getChar(),
                bs.getChar(),
                bs.getChar(),
                bs.getChar(),
                bs.getChar());
    }
}
