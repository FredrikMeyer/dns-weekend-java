package net.fredrikmeyer;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class DNSHeaderTest {

    @Test
    public void testToBytes() {
        int flags = 1 << 8;
        DNSHeader dnsHeader = new DNSHeader(0x1314,
                flags,
                1,
                0,
                0,
                0);

        byte[] bytes = dnsHeader.toBytes();

        assertEquals(12,
                bytes.length);
        assertArrayEquals(new byte[]{(byte) 0x13, (byte) 0x14, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                bytes);
    }

    @Test
    void testParseHeader() throws IOException {
        var bytes = new byte[]{(byte) 0x13, (byte) 0x14, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        ByteBuffer bs = ByteBuffer.wrap(bytes);
        DNSHeader dnsHeader = DNSHeader.parseHeader(bs);
        assertEquals(0x1314,
                dnsHeader.id());
        assertEquals(1 << 8,
                dnsHeader.flags());
        assertEquals(1,
                dnsHeader.numQuestions());
        assertEquals(0,
                dnsHeader.numAnswers());
        assertEquals(0,
                dnsHeader.numAuthorities());
        assertEquals(0,
                dnsHeader.numAdditionals());
    }

    @Test
    void testParseHeaderAgain() throws IOException {
        var bytes = new byte[]{96, 86, (byte) 0x81, (byte) 0x80, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, 0, 0};

        ByteBuffer bs = ByteBuffer.wrap(bytes);
        assertEquals(DNSHeader.parseHeader(bs),
                new DNSHeader(24662,
                        33152,
                        1,
                        1,
                        0,
                        0));
    }
}