package net.fredrikmeyer.dnsweekend;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;

public interface SocketLike extends Closeable {
    void connect(SocketAddress address) throws SocketException;

    void send(DatagramPacket packet) throws IOException;

    void receive(DatagramPacket packet) throws IOException;
}
