package net.fredrikmeyer.dnsweekend;

import java.io.IOException;
import java.net.DatagramSocket;

public interface SocketProvider {
    DatagramSocket provideSocket() throws IOException;
}
