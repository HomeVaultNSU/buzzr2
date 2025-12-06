package ru.nsu.ccfit.buzzr.discovery.util;

import lombok.experimental.UtilityClass;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@UtilityClass
public class NetworkUtils {

    public InetSocketAddress buildAddress(SocketAddress address, int port) {
        if (address instanceof InetSocketAddress inetSocketAddress) {
            return new InetSocketAddress(inetSocketAddress.getAddress(), port);
        }

        throw new IllegalArgumentException("address must be an instance of InetSocketAddress");
    }
}