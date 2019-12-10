
package segmentedfilesystem;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;

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
            System.err.println("Failed to open socket\n" + e);
        }

        try {
            address = InetAddress.getByName(args[0]);
            packet = new DatagramPacket(sendBuf, sendBuf.length, address, port);
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Failed to send header packet to server\n" + e);
            return;
        }
        ArrayList<FileReceiver> doneFiles = new ArrayList<>();
        HashMap<Byte, FileReceiver> files = new HashMap<>();

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
