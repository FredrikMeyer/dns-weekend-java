package net.fredrikmeyer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DNSPacketTest {

    @Test
    void testParse() throws Exception {
        var bytes = new byte[]{(byte) 96, 86, (byte) 129, (byte) 128, 0, 1, 0, 1, 0, 0, 0, 0, 3, 119, 119, 119, 7, 101,
                120, 97, 109, 112, 108, 101, 3, 99, 111, 109, 0, 0, 1, 0, 1, (byte) 192, 12, 0, 1, 0, 1, 0, 0, 82,
                (byte) 155, 0, 4,
                93, (byte) 184, (byte) 216, 34};

        DNSPacket result = DNSPacket.parse(bytes);

        System.out.println(Util.ipToString(result.answers().getFirst().data()));
        System.out.println(Arrays.toString(result.answers().getFirst().data()));
    }
}