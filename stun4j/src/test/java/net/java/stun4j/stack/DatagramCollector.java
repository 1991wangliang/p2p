/*
 * Stun4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.stun4j.stack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramCollector
    implements Runnable
{
    DatagramPacket receivedPacket = null;
    DatagramSocket sock           = null;

    public DatagramCollector()
    {
    }

    public void run()
    {
        try
        {
            sock.receive(receivedPacket);
        }
        catch (IOException ex)
        {
            receivedPacket = null;
        }

    }

    public void startListening(DatagramSocket sock)
    {
        this.sock = sock;
        receivedPacket = new DatagramPacket(new byte[4096], 4096);

        new Thread(this).start();

        //give the guy a chance to start
        try
        {
            Thread.sleep(200);
        }
        catch (InterruptedException ex)
        {
        }
    }

    public DatagramPacket collectPacket()
    {
        //recycle
        DatagramPacket returnValue = receivedPacket;
        receivedPacket = null;
        sock           = null;

        //return
        return returnValue;
    }
}
