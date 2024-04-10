package net.fredrikmeyer.dnsweekend;

import java.io.IOException;

public interface SocketProvider {
    SocketLike provideSocket() throws IOException;
}
