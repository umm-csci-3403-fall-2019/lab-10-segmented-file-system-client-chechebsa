package segmentedfilesystem;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.DatagramPacket;

/**
 * This is just a stub test file. You should rename it to something meaningful
 * in your context and populate it with useful tests.
 */
public class Tests {

    @Test
    public void testCreateHeader() {
        var sendBuf = new byte[10];
        sendBuf[0] = 0;
        DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length);
        FileReceiver file = new FileReceiver(packet);
        assertNotNull(file.packets.get(-1));
    }

    @Test
    public void testDone() {
        byte[] sendBuf = new byte[10];
        sendBuf[0] = 0;
        DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length);
        FileReceiver file = new FileReceiver(packet);
        sendBuf[0] = 3;
        sendBuf[2] = 0;
        sendBuf[3] = 0;
        packet = new DatagramPacket(sendBuf, sendBuf.length);
        file.addPacket(packet);
        assertTrue(file.isDone());

    }

}