package net.fredrikmeyer;

import net.fredrikmeyer.dnsweekend.Query;
import net.fredrikmeyer.dnsweekend.RealSocket;
import net.fredrikmeyer.dnsweekend.ResourceType;
import net.fredrikmeyer.dnsweekend.SocketLike;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueryTest {
    static class FakeSocket implements SocketLike {

        @Override
        public void connect(SocketAddress address) {

        }

        @Override
        public void send(DatagramPacket packet) {

        }

        @Override
        public void receive(DatagramPacket packet) {
            var bytes = new byte[]{114, 73, -123, 0, 0, 1, 0, 4, 0, 0, 0, 0, 12, 102, 114, 101, 100, 114, 105, 107, 109,
                    101, 121, 101, 114, 3, 110, 101, 116, 0, 0, 1, 0, 1, -64, 12, 0, 1, 0, 1, 0, 0, 14, 16, 0, 4, -71,
                    -57, 108, -103, -64, 12, 0, 1, 0, 1, 0, 0, 14, 16, 0, 4, -71, -57, 111, -103, -64, 12, 0, 1, 0, 1,
                    0, 0, 14, 16, 0, 4, -71, -57, 109, -103, -64, 12, 0, 1, 0, 1, 0, 0, 14, 16, 0, 4, -71, -57, 110,
                    -103};

            byte[] data = packet.getData();
            System.arraycopy(bytes,
                    0,
                    data,
                    0,
                    bytes.length);
        }

        @Override
        public void close() {

        }
    }

    @Test
    void testBuildQuery() {
        Random mock = mock(Random.class);
        when(mock.nextInt(anyInt(),
                anyInt())).thenReturn(0x1314);

        byte[] answer = new Query(mock,
                FakeSocket::new).buildQuery("example.com",
                ResourceType.TYPE_A);

        // box array
        Byte[] boxedArray = new Byte[answer.length];
        for (int i = 0; i < answer.length; i++) {
            boxedArray[i] = answer[i];
        }

        assertArrayEquals(new Byte[]{0x13, 0x14, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 7, 101, 120, 97, 109, 112, 108, 101, 3,
                        99, 111, 109, 0, 0, 1, 0, 1},
                boxedArray);
    }

    @Test
    @EnabledOnOs({OS.MAC})
        // Don't do real network calls on CI. Probably a better way to do this?
    void testDoQuery() throws SocketException {
        Random mock = mock(Random.class);
        when(mock.nextInt(anyInt(),
                anyInt())).thenReturn(0x1314);
        var queryInstance = new Query(mock,
                RealSocket::new);
        byte[] query = queryInstance.buildQuery("example.com",
                ResourceType.TYPE_A);
        byte[] result = queryInstance.doQuery(query,
                "8.8.8.8");

        byte[] truncated = Arrays.copyOf(result,
                36);

        assertArrayEquals(new byte[]{19, 20, -127, -128, 0, 1, 0, 1, 0, 0, 0, 0, 7, 101, 120, 97, 109, 112, 108, 101, 3,
                        99, 111, 109, 0, 0, 1, 0, 1, -64, 12, 0, 1, 0, 1, 0,},
                truncated);
    }

    @Test
    public void testResolve() throws Exception {
        Random mock = mock(Random.class);
        when(mock.nextInt(anyInt(),
                anyInt())).thenReturn(0x1314);

        Query query = new Query(mock,
                FakeSocket::new);

        String res = query.resolve("www.facebook.com",
                ResourceType.TYPE_A);
        assertEquals("185.199.108.153",
                res);

    }
}