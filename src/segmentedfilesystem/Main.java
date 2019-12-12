
package segmentedfilesystem;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        int port = 6014;
        InetAddress address;
        DatagramSocket socket = null;
        DatagramPacket packet;
        byte[] sendBuf = new byte[1028];

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

        HashMap<Byte, FileReceiver> files = new HashMap<>();

        ArrayList<FileReceiver> doneFiles = new ArrayList<>();

        while (true) {
            packet = new DatagramPacket(sendBuf, sendBuf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.err.println("Error when receiving packets\n" + e);
                return;
            }
            byte fileId = sendBuf[1];
            if (files.get(fileId) == null) {
                files.put(fileId, new FileReceiver(packet));
            } else {
                files.get(fileId).addPacket(packet);
            }
            if (files.get(fileId).isDone()) {
                doneFiles.add(files.remove(fileId));
                if (files.isEmpty()) {
                    break;
                }
            }

        }
        for (FileReceiver file : doneFiles) {
            try {
                file.createFile();
            } catch (IOException e) {
                System.err.println("Error when creating file\n" + e);
            }

        }

    }

}
