package net.fredrikmeyer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class Query {
    private final Random randomGenerator;
    final int RECURSION_DESIRED = 1 << 8;

    public Query(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public Query() {
        this.randomGenerator = new Random();
    }

    public String lookupDomain(String domain) throws Exception {
        var query = buildQuery(domain,
                ResourceType.TYPE_A);
        byte[] response = doQuery(query,
                DatagramSocket::new);

        var result = DNSPacket.parse(response);
        System.out.println(result);
        return Util.ipToString(result.answers().getFirst().data());
    }

    public byte[] buildQuery(String domainName, ResourceType recordType) {
        byte[] encodedName = DNSQuestion.encodeName(domainName);
        int id = this.randomGenerator.nextInt(0,
                65535);

        var header = new DNSHeader(id,
                RECURSION_DESIRED,
                1,
                0,
                0,
                0);

        var question = new DNSQuestion(encodedName,
                recordType,
                ResourceClass.IN);

        Object[] array = Stream
                .of(header.toBytes(),
                        question.toBytes())
                .collect(() -> new ArrayList<Byte>(),
                        (acc, curr) -> {
                            for (byte b : curr) {
                                acc.add(b);
                            }
                        },
                        ArrayList::addAll)
                .toArray();

        byte[] result = Util.convertToPrimitive(array);
        return result;
    }

    public static byte[] doQuery(byte[] query, SocketProvider provider) {
        try (DatagramSocket socket = provider.provideSocket()) {
            // Connect to the Google DNS server
            socket.connect(new InetSocketAddress("1.1.1.1",
                    53));

            // Send a packet over UDP
            socket.send(new DatagramPacket(query,
                    query.length));

            var recBytes = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recBytes,
                    recBytes.length);
            socket.receive(receivePacket);

            byte[] shortBytes = Arrays.copyOf(recBytes,
                    receivePacket.getLength());

            return shortBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
