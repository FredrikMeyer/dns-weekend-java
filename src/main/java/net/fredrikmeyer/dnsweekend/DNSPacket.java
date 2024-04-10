package net.fredrikmeyer.dnsweekend;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public record DNSPacket(DNSHeader header,
                        List<DNSQuestion> questions,
                        List<DNSRecord> answers,
                        List<DNSRecord> authorities,
                        List<DNSRecord> additionals) {

    public static DNSPacket parse(byte[] data) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(data);
        DNSHeader header = DNSHeader.parseHeader(bb);

        List<DNSQuestion> questions = fillAndCreateList(() -> DNSQuestion.parseBytes(bb),
                header.numQuestions());

        List<DNSRecord> answers = fillAndCreateList(() -> DNSRecord.parseBytes(bb),
                header.numAnswers());

        List<DNSRecord> authorities = fillAndCreateList(() -> DNSRecord.parseBytes(bb),
                header.numAuthorities());

        List<DNSRecord> additionals = fillAndCreateList(() -> DNSRecord.parseBytes(bb),
                header.numAdditionals());

        return new DNSPacket(header,
                questions,
                answers,
                authorities,
                additionals);
    }

    private static <E> List<E> fillAndCreateList(Callable<E> creator, int times) throws Exception {
        List<E> list = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            list.add(creator.call());
        }
        return list;
    }

}
