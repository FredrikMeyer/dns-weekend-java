package net.fredrikmeyer.dnsweekend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

public class Query {
    private final Random randomGenerator;
    private final SocketProvider socketProvider;
    final int RECURSION_DESIRED = 1 << 8;

    public Query(Random randomGenerator, SocketProvider socketProvider) {
        this.randomGenerator = randomGenerator;
        this.socketProvider = socketProvider;
    }

    public Query() {
        this.randomGenerator = new Random();
        this.socketProvider = RealSocket::new;
    }

    private DNSPacket sendQuery(String ipAddress, String domainName, ResourceType recordType) throws Exception {
        byte[] queryBytes = buildQuery(domainName,
                recordType);

        byte[] response = doQuery(queryBytes,
                ipAddress);

        return DNSPacket.parse(response);
    }

    /**
     * Do a DNS resolve. Given a domain name and a record type (A, AAAA, CNAME, etc), connect to a root
     * DNS server and recursively resolve the IP of the given domain name.
     * Example: resolve("wwww.facebook.com", ResourceType.TYPE_A) -> 12.3.4.5 (ish)
     *
     * @return The resolved domain name.
     * @throws Exception If the response contains some resource type that is not implemented.
     */
    public String resolve(String domainName, ResourceType recordType) throws Exception {
        // One of the root name server IPs
        // This one is in Sweden
        // See: https://en.wikipedia.org/wiki/Root_name_server#Root_server_addresses
        var nameServer = "192.36.148.17";

        while (true) {
            System.out.println("Querying " + nameServer + " for " + domainName + ".");

            var response = sendQuery(nameServer,
                    domainName,
                    recordType);

            System.out.println("RESPONSE: " + response);

            if (getAnswer(response) instanceof String answ) {
                return answ;
            } else if (getNameServerIP(response) instanceof String nameServerIP) {
                System.out.println("Name server IP: " + nameServerIP);
                nameServer = nameServerIP;
            } else if (getNameServer(response) instanceof String nsDomain) {
                System.out.printf("Doing recursive resolve: " + nsDomain);
                nameServer = resolve(nsDomain,
                        ResourceType.TYPE_A);
            } else if (getCName(response) instanceof String cName) {
                System.out.println("Got CNAME: " + cName + ". Redirecting.");
                return resolve(cName,
                        recordType);
            } else {
                System.out.println(response);
                throw new Exception("Should not get here: " + nameServer + ". Domain: " + domainName + ". Type: " + recordType);
            }
        }
    }

    private String getCName(DNSPacket response) {
        return response
                .answers()
                .stream()
                .filter(auth -> auth.type() == ResourceType.TYPE_CNAME)
                .findFirst()
                .map(DNSRecord::data)
                .map(String::new)
                .orElse(null);
    }

    private String getAnswer(DNSPacket response) {
        return response
                .answers()
                .stream()
                .filter(DNSRecord::isARecord)
                .findFirst()
                .map(DNSRecord::data)
                .map(Util::ipToString)
                .orElse(null);
    }

    private String getNameServerIP(DNSPacket response) {
        return response
                .additionals()
                .stream()
                .filter(DNSRecord::isARecord)
                .findFirst()
                .map(DNSRecord::data)
                .map(Util::ipToString)
                .orElse(null);
    }

    private String getNameServer(DNSPacket response) {
        return response
                .authorities()
                .stream()
                .filter(auth -> auth.type() == ResourceType.TYPE_NS)
                .findFirst()
                .map(dnsRecord -> new String(dnsRecord.data()))
                .orElse(null);
    }

    /**
     * A DNS query consists of a header,
     * Builds a DNS query for a given domain name and ResourceType.
     *
     * @param domainName The domain name.
     * @param recordType The record type. Not all are supported.
     * @return The DNS query as an array of bytes.
     */
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

        var headerBytes = header.toBytes();
        var questionBytes = question.toBytes();
        var totalLength = headerBytes.length + questionBytes.length;
        var result = new byte[totalLength];
        System.arraycopy(headerBytes,
                0,
                result,
                0,
                headerBytes.length);
        System.arraycopy(questionBytes,
                0,
                result,
                headerBytes.length,
                questionBytes.length);

        return result;
    }

    public byte[] doQuery(byte[] query, String ipAddress) {
        try (SocketLike socket = this.socketProvider.provideSocket()) {
            // Connect to the DNS server. DNS queries are done with port 53.
            socket.connect(new InetSocketAddress(ipAddress,
                    53));

            // Send a packet over UDP
            socket.send(new DatagramPacket(query,
                    query.length));

            var recBytes = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(recBytes,
                    recBytes.length);
            socket.receive(receivePacket);

            System.out.println(Arrays.toString(recBytes));

            return Arrays.copyOf(recBytes,
                    receivePacket.getLength());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
