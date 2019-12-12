
package segmentedfilesystem;

import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        int port = 6014;
        InetAddress address;
        DatagramSocket socket = null;
        DatagramPacket packet;
        byte[] sendBuf = new byte[256];

        if (args.length != 1) {
            System.out.println("Usage: java Main <hostname>");
            return;
        }

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.err.println("Socket failed to open\n" + e);
        }

        try {
            address = InetAddress.getByName(args[0]);
            packet = new DatagramPacket(sendBuf, sendBuf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Header packet is not sent to the server\n" + e);
            return;
        }

    }

}
