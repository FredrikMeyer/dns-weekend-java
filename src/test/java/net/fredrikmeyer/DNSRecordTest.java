package net.fredrikmeyer;

import net.fredrikmeyer.dnsweekend.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DNSRecordTest {

    @Test
    void testParseBytes() throws IOException {
        var bytes = new byte[]{(byte) 96, 86, (byte) 129, (byte) 128, 0, 1, 0, 1, 0, 0, 0, 0, 3, 119, 119, 119, 7, 101,
                120, 97, 109, 112, 108, 101, 3, 99, 111, 109, 0, 0, 1, 0, 1, (byte) 192, 12, 0, 1, 0, 1, 0, 0, 82,
                (byte) 155, 0, 4,
                93, (byte) 184, (byte) 216, 34};

        ByteBuffer bs = ByteBuffer.wrap(bytes);
        DNSHeader.parseHeader(bs);
        DNSQuestion.parseBytes(bs);
        DNSRecord result = DNSRecord.parseBytes(bs);

        assertEquals("www.example.com",
                new String(result.name()));

        assertEquals(ResourceType.TYPE_A,
                result.type());
        assertEquals(ResourceClass.IN,
                result.clazz());

        assertEquals(21147,
                result.ttl());

        assertArrayEquals(new byte[]{93, (byte) 184, (byte) 216, (byte) 34},
                result.data());
    }
}