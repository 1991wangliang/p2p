package net.java.stun4j;

import junit.framework.TestCase;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Request;
import net.java.stun4j.message.Response;
import net.java.stun4j.stack.RequestListener;
import net.java.stun4j.stack.StunStack;

import java.util.Arrays;
import java.util.Vector;

/**
 * Test how client and server behave, how they recognize/adopt messages and
 * how they both handle retransmissions (i.e. client transactions should make
 * them and server transactions should hide them)
 * <p>Company: Net Research Team, Louis Pasteur University</p>
 * <p>@author Emil Ivov</p>
 */
public class TransactionSupportTests extends TestCase
{
    StunStack stunStack = null;

    StunAddress clientAddress = new StunAddress("127.0.0.1", 5216);
    StunAddress serverAddress = new StunAddress("127.0.0.2", 5255);

    NetAccessPointDescriptor  clientAccessPoint = null;
    NetAccessPointDescriptor  serverAccessPoint = null;

    Request  bindingRequest = null;
    Response bindingResponse = null;

    PlainRequestCollector requestCollector = null;
    PlainResponseCollector responseCollector = null;

    protected void setUp() throws Exception
    {
        super.setUp();

        stunStack = StunStack.getInstance();
        stunStack.start();

        clientAccessPoint = new NetAccessPointDescriptor(clientAddress);
        serverAccessPoint = new NetAccessPointDescriptor(serverAddress);

        stunStack.installNetAccessPoint(clientAccessPoint);
        stunStack.installNetAccessPoint(serverAccessPoint);

        bindingRequest = MessageFactory.createBindingRequest();
        bindingResponse = MessageFactory.createBindingResponse(
            clientAddress, clientAddress, serverAddress);

        requestCollector = new PlainRequestCollector();
        responseCollector = new PlainResponseCollector();

        System.setProperty("net.java.stun4j.PROPAGATE_RECEIVED_RETRANSMISSIONS",
                           "false");
        System.setProperty("net.java.stun4j.KEEP_CLIENT_TRANS_AFTER_A_RESPONSE",
                           "false");
        System.setProperty("net.java.stun4j.MAX_RETRANSMISSIONS",
                           "");
        System.setProperty("net.java.stun4j.MAX_WAIT_INTERVAL",
                           "");
        System.setProperty("net.java.stun4j.ORIGINAL_WAIT_INTERVAL",
                           "");


    }

    protected void tearDown() throws Exception
    {
        clientAccessPoint = null;
        serverAccessPoint = null;
        requestCollector = null;
        responseCollector = null;

        System.setProperty("net.java.stun4j.PROPAGATE_RECEIVED_RETRANSMISSIONS",
                           "false");
        System.setProperty("net.java.stun4j.KEEP_CLIENT_TRANS_AFTER_A_RESPONSE",
                           "false");
        System.setProperty("net.java.stun4j.MAX_RETRANSMISSIONS",
                           "");
        System.setProperty("net.java.stun4j.MAX_WAIT_INTERVAL",
                           "");
        System.setProperty("net.java.stun4j.ORIGINAL_WAIT_INTERVAL",
                           "");

        stunStack.shutDown();

        super.tearDown();
    }

    /**
     * Test that requests are retransmitted if no response is received
     *
     * @throws Exception upon any failure
     */
    public void testClientRetransmissions() throws Exception
    {
        //prepare to listen
        System.setProperty("net.java.stun4j.PROPAGATE_RECEIVED_RETRANSMISSIONS",
                           "true");
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(12000);

        //verify
        assertTrue("No retransmissions of the request have been received",
            requestCollector.receivedRequests.size() > 1);
        assertTrue("The binding request has not been retransmitted enough!",
            requestCollector.receivedRequests.size() >= 9);

    }

    /**
     * Make sure that retransmissions are not seen by the server user and that
     * it only gets a single request.
     * @throws Exception if anything goes wrong.
     */
    public void testServerRetransmissionHiding() throws Exception
    {
        //prepare to listen
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(12000);

        //verify
        assertTrue(
            "Retransmissions of a binding request were propagated to the server",
            requestCollector.receivedRequests.size() <= 1 );
    }

    /**
     * Makes sure that once a request has been answered by the server,
     * retransmissions of this request are not propagated to the UA and are
     * automatically handled with a retransmission of the last seen response
     * @throws Exception if we screw up.
     */
    public void testServerResponseRetransmissions() throws Exception
    {
        //prepare to listen
        System.setProperty("net.java.stun4j.KEEP_CLIENT_TRANS_AFTER_A_RESPONSE",
                           "true");
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);

        //wait for the message to arrive
        Thread.currentThread().sleep(500);

        StunMessageEvent evt =
            ((StunMessageEvent)requestCollector.receivedRequests.get(0));
        byte[] tid = evt.getMessage().getTransactionID();
        stunStack.getProvider().sendResponse(tid,
                                             bindingResponse,
                                             serverAccessPoint,
                                             clientAddress);

        //wait for retransmissions
        Thread.currentThread().sleep(12000);

        //verify that at least half of the request received a retransmitted resp.
        assertTrue(
            "There were no retransmissions of a binding response",
            responseCollector.receivedResponses.size() < 5 );
    }

    /**
     * A (very) weak test, verifying that transaction IDs are unique.
     * @throws Exception in case we feel like it.
     */
    public void testUniqueIDs() throws Exception
    {
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send req 1
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(500);

        StunMessageEvent evt1 =
            ((StunMessageEvent)requestCollector.receivedRequests.get(0));

        //send a response to make the other guy shut up
        byte[] tid = evt1.getMessage().getTransactionID();
        stunStack.getProvider().sendResponse(tid,
                                             bindingResponse,
                                             serverAccessPoint,
                                             clientAddress);

        //send req 2
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(12000);

        StunMessageEvent evt2 =
            ((StunMessageEvent)requestCollector.receivedRequests.get(0));

        assertTrue("Consecutive requests were assigned the same transaction id",
            Arrays.equals( evt1.getMessage().getTransactionID(),
                           evt2.getMessage().getTransactionID()));
    }

    public void testClientTransactionMaxRetransmisssionsConfigurationParameter()
        throws Exception
    {
        //MAX_RETRANSMISSIONS

        System.setProperty("net.java.stun4j.MAX_RETRANSMISSIONS",
                           "2");
        //make sure we see retransmissions so that we may count them
        System.setProperty("net.java.stun4j.PROPAGATE_RECEIVED_RETRANSMISSIONS",
                           "true");
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(1600);

        //verify
        assertTrue("No retransmissions of the request have been received",
            requestCollector.receivedRequests.size() > 1);
        assertTrue("The MAX_RETRANSMISSIONS param was not taken into account!",
            requestCollector.receivedRequests.size() == 3);

    }

    public void testMinWaitIntervalConfigurationParameter()
        throws Exception
    {
        //MAX_RETRANSMISSIONS
        System.setProperty("net.java.stun4j.ORIGINAL_WAIT_INTERVAL",
                           "1000");
        //make sure we see retransmissions so that we may count them
        System.setProperty("net.java.stun4j.PROPAGATE_RECEIVED_RETRANSMISSIONS",
                           "true");
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);

        //wait a while
        Thread.currentThread().sleep(500);

        //verify
        assertTrue("A retransmissions of the request was sent too early",
            requestCollector.receivedRequests.size() < 2);

        //wait for a send
        Thread.currentThread().sleep(600);

        //verify
        assertTrue("A retransmissions of the request was not sent",
            requestCollector.receivedRequests.size() == 2);
    }

    public void testMaxWaitIntervalConfigurationParameter()
        throws Exception
    {
        //MAX_RETRANSMISSIONS
        System.setProperty("net.java.stun4j.MAX_WAIT_INTERVAL",
                           "100");
        //make sure we see retransmissions so that we may count them
        System.setProperty("net.java.stun4j.PROPAGATE_RECEIVED_RETRANSMISSIONS",
                           "true");
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait a while
        Thread.currentThread().sleep(1100);

        //verify
        assertTrue("Not all retransmissions were made for the expected period "
                   +"of time",
            requestCollector.receivedRequests.size() == 9);

        //wait for a send
        Thread.currentThread().sleep(1600);

        //verify
        assertTrue("A retransmissions of the request was sent, while not "
                   +"supposed to",
            requestCollector.receivedRequests.size() == 9);
    }

    private class PlainRequestCollector implements RequestListener{
        public Vector receivedRequests = new Vector();

        public void requestReceived(StunMessageEvent evt){
            receivedRequests.add(evt);
        }
    }

    private class PlainResponseCollector implements ResponseCollector{

        public Vector receivedResponses = new Vector();

        public void processResponse(StunMessageEvent responseEvt)
        {
            receivedResponses.add(responseEvt);
        }

        public void processTimeout()
        {
            receivedResponses.add(new String("timeout"));
        }

    }
}
