/*
 * Stun4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.stun4j.client;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.Response;
import net.java.stun4j.stack.RequestListener;
import net.java.stun4j.stack.StunProvider;
import net.java.stun4j.stack.StunStack;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a programmable STUN server that sends predefined
 * sequences of responses. It may be used to test whether a STUN client
 * behaves correctly in different use cases.
 *
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
public class ResponseSequenceServer
    implements RequestListener
{
    private static final Logger logger =
        Logger.getLogger(ResponseSequenceServer.class.getName());
    /**
     * The sequence of responses to send.
     */
    private Vector messageSequence = new Vector();

    private StunStack    stunStack    = null;
    private StunProvider stunProvider = null;

    private StunAddress              serverAddress       = null;
    private NetAccessPointDescriptor apDescriptor        = null;

    public ResponseSequenceServer(StunAddress bindAddress)
    {
        this.serverAddress = bindAddress;
    }

    /**
     * Initializes the underlying stack
     * @throws StunException if something fails
     */
    public void start()
        throws StunException
    {
        stunStack    = StunStack.getInstance();
        stunProvider = stunStack.getProvider();

        apDescriptor = new NetAccessPointDescriptor(serverAddress);

        stunStack.installNetAccessPoint(apDescriptor);
        stunProvider.addRequestListener(apDescriptor, this);
        stunStack.start();

    }

    /**
     * Resets the server (deletes the sequence and stops the stack)
     */
    public void shutDown()
    {
        messageSequence.removeAllElements();

        stunStack.shutDown();

        stunStack    = null;
        stunProvider = null;
    }

    /**
     * Adds the specified response to this sequence or marks a pause (i.e. do
     * not respond) if response is null.
     * @param response the response to add or null to mark a pause
     */
    public void addMessage(Response response)
    {
        if (response == null)
        {
            //leave a mark to skip a message
            messageSequence.add(new Boolean(false));
        }
        else
            messageSequence.add(response);
    }

    /**
     * Completely ignores the event that is passed and just sends the next
     * message from the sequence - or does nothing if there's something
     * different from a Response on the current position.
     * @param evt the event being dispatched
     */
    public void requestReceived(StunMessageEvent evt)
    {
        if(messageSequence.isEmpty())
            return;
        Object obj = messageSequence.remove(0);

        if( !(obj instanceof Response) )
            return;

        Response res = (Response)obj;

        try
        {
            stunProvider.sendResponse(evt.getMessage().getTransactionID(),
                                      res,
                                      apDescriptor,
                                      evt.getRemoteAddress());
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "failed to send a response", ex);
        }

    }

    /**
     * Returns a string representation of this Server.
     * @return the ip address and port where this server is bound
     */
    public String toString()
    {
        return serverAddress == null?"null":serverAddress.toString();
    }

}
