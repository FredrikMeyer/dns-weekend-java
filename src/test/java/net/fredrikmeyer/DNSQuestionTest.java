package net.fredrikmeyer;

import net.fredrikmeyer.dnsweekend.DNSQuestion;
import net.fredrikmeyer.dnsweekend.ResourceClass;
import net.fredrikmeyer.dnsweekend.ResourceType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DNSQuestionTest {

    @Test
    void toBytes() {
        DNSQuestion dnsQuestion = new DNSQuestion(new byte[]{(byte) 111},
                ResourceType.TYPE_A,
                ResourceClass.IN);

        byte[] bytes = dnsQuestion.toBytes();

        assertArrayEquals(new byte[]{(byte) 111, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01},
                bytes);
    }

    @Test
    void encodeName() {
        String domainName = "google.com";
        byte[] encodedName = DNSQuestion.encodeName(domainName);

        assertArrayEquals(new byte[]{(byte) 0x06, (byte) 103, (byte) 111, (byte) 111, (byte) 103, (byte) 108,
                        (byte) 101, (byte) 3, (byte) 99, (byte) 111, (byte) 109, (byte) 0x00},
                encodedName);
    }

    @Test
    void testParseBytes() throws IOException {
        byte[] bytes = new byte[]{(byte) 0x03, (byte) 119, (byte) 119, (byte) 119, (byte) 7, 101, 120, 97, 109, 112,
                108, 101, 3, 99, 111, 109, 0, 0, 1, 0, 1};

        var byteBuffer = ByteBuffer.wrap(bytes);
        DNSQuestion dnsQuestion = DNSQuestion.parseBytes(byteBuffer);

        assertEquals("www.example.com",
                new String(dnsQuestion.name()));
        assertEquals(ResourceType.TYPE_A,
                dnsQuestion.type());
        assertEquals(ResourceClass.IN,
                dnsQuestion.clazz());

    }
}