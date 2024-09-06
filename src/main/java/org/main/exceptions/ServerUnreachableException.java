package org.main.exceptions;

import java.net.InetSocketAddress;

public class ServerUnreachableException extends Exception {

    public ServerUnreachableException(InetSocketAddress address) {
        super("Server " + address + " unreachable !");
    }
}
