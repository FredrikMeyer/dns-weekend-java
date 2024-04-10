package net.fredrikmeyer;

import net.fredrikmeyer.dnsweekend.Query;
import net.fredrikmeyer.dnsweekend.ResourceType;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Random;

import static net.fredrikmeyer.dnsweekend.Query.doQuery;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueryTest {

    @Test
    void testBuildQuery() {
        Random mock = mock(Random.class);
        when(mock.nextInt(anyInt(),
                anyInt())).thenReturn(0x1314);

        byte[] answer = new Query(mock).buildQuery("example.com",
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
    void testDoQuery() {
        Random mock = mock(Random.class);
        when(mock.nextInt(anyInt(),
                anyInt())).thenReturn(0x1314);
        byte[] query = new Query(mock).buildQuery("example.com",
                ResourceType.TYPE_A);
        byte[] result = doQuery(query,
                "8.8.8.8",
                DatagramSocket::new);

        System.out.println(Arrays.toString(result));

        byte[] truncated = Arrays.copyOf(result,
                36);

        assertArrayEquals(new byte[]{19, 20, -127, -128, 0, 1, 0, 1, 0, 0, 0, 0, 7, 101, 120, 97, 109, 112, 108, 101, 3,
                        99, 111, 109, 0, 0, 1, 0, 1, -64, 12, 0, 1, 0, 1, 0,},
                truncated);
    }
}