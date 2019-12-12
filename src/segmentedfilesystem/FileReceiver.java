package segmentedfilesystem;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

class FileReceiver extends Main {
    public HashMap<Integer, byte[]> packets;
    public int numPackets;
    private int foundPackets;
    private boolean done;

    public FileReceiver(DatagramPacket packet) {
        numPackets = Integer.MAX_VALUE;
        foundPackets = 0;
        done = false;
        packets = new HashMap<>();

        this.addPacket(packet);
    }

    public void addPacket(DatagramPacket packet) {
        foundPackets++;
        switch (packet.getData()[0] % 4) {
        case 3: {
            numPackets = ((packet.getData()[2] & 0xff) << 8) | (packet.getData()[3] & 0xff) + 2;
        }
        case 1: {
            int packetNumber = ((packet.getData()[2] & 0xff) << 8) | (packet.getData()[3] & 0xff);
            packets.put(packetNumber, Arrays.copyOf(packet.getData(), packet.getLength()));
            break;
        }
        default: {
            packets.put(-1, Arrays.copyOf(packet.getData(), packet.getLength()));
        }
        }
        if (numPackets == foundPackets) {
            done = true;
        }

    }

    public boolean isDone() {
        return done;
    }

    public void createFile() throws IOException {
        String fileName = getFileName();

        File newFile = new File(fileName);
        OutputStream writer = new FileOutputStream(newFile);
        System.out.println(fileName);

        if (newFile.createNewFile()) {
            System.out.println("File already exists.");
        }

        for (int j = 0; j < numPackets - 1; j++) {
            byte[] packet = packets.get(j);
            writer.write(packet, 4, packet.length - 4);
        }

        writer.close();
    }

    public String getFileName() {
        byte[] header = packets.get(-1);
        return new String(header, 2, header.length - 2);
    }
}