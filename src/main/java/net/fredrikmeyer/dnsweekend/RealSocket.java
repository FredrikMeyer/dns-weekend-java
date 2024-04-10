package net.fredrikmeyer.dnsweekend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class RealSocket implements SocketLike {
    private final DatagramSocket socket;

    public RealSocket() throws SocketException {
        this.socket = new DatagramSocket();
    }

    @Override
    public void connect(SocketAddress address) throws SocketException {
        socket.connect(address);
    }

    @Override
    public void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    @Override
    public void receive(DatagramPacket packet) throws IOException {
        socket.receive(packet);
    }

    @Override
    public void close() {
        this.socket.close();
    }
}
