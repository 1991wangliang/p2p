package net.java.stun4j;

import junit.framework.TestCase;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Request;
import net.java.stun4j.message.Response;
import net.java.stun4j.stack.RequestListener;
import net.java.stun4j.stack.StunStack;

import java.util.Vector;

/**
 * Test event dispatching for both client and server.
 * <p>Company: Net Research Team, Louis Pasteur University</p>
 * <p>@author Emil Ivov</p>
 */
public class MessageEventDispatchingTest extends TestCase
{
    StunStack stunStack = null;

    StunAddress clientAddress = new StunAddress("127.0.0.1", 5216);
    StunAddress serverAddress = new StunAddress("127.0.0.2", 5255);
    StunAddress serverAddress2 = new StunAddress("127.0.0.2", 5259);

    NetAccessPointDescriptor  clientAccessPoint = null;
    NetAccessPointDescriptor  serverAccessPoint = null;
    NetAccessPointDescriptor  serverAccessPoint2 = null;

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
        serverAccessPoint2 = new NetAccessPointDescriptor(serverAddress2);

        stunStack.installNetAccessPoint(clientAccessPoint);
        stunStack.installNetAccessPoint(serverAccessPoint);
        stunStack.installNetAccessPoint(serverAccessPoint2);

        bindingRequest = MessageFactory.createBindingRequest();
        bindingResponse = MessageFactory.createBindingResponse(
            clientAddress, clientAddress, serverAddress);

        requestCollector = new PlainRequestCollector();
        responseCollector = new PlainResponseCollector();

    }

    protected void tearDown() throws Exception
    {
        clientAccessPoint = null;
        serverAccessPoint = null;
        requestCollector = null;
        responseCollector = null;

        stunStack.shutDown();

        super.tearDown();
    }

    /**
     * Test timeout events.
     *
     * @throws Exception upon a stun failure
     */
    public void testClientTransactionTimeouts() throws Exception
    {

        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        Thread.currentThread().sleep(12000);

        assertEquals(
            "No timeout was produced upon expiration of a client transaction",
            responseCollector.receivedResponses.size(), 1);

        assertEquals(
            "No timeout was produced upon expiration of a client transaction",
            responseCollector.receivedResponses.get(0), "timeout");
    }

    /**
     * Test reception of Message events.
     *
     * @throws Exception upon any failure
     */
    public void testEventDispatchingUponIncomingRequests() throws Exception
    {
        //prepare to listen
        stunStack.getProvider().addRequestListener(requestCollector);
        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(500);

        //verify
        assertTrue("No MessageEvents have been dispatched",
            requestCollector.receivedRequests.size() == 1);
    }

    /**
     * Test that reception of Message events is only received for accesspoints
     * that we have been registered for.
     *
     * @throws Exception upon any failure
     */
    public void testSelectiveEventDispatchingUponIncomingRequests()
        throws Exception
    {
        //prepare to listen
        stunStack.getProvider().addRequestListener(serverAccessPoint,
                                                   requestCollector);

        PlainRequestCollector requestCollector2 = new PlainRequestCollector();
        stunStack.getProvider().addRequestListener(serverAccessPoint2,
                                                   requestCollector2);

        //send
        stunStack.getProvider().sendRequest(bindingRequest,
                                            serverAddress2,
                                            clientAccessPoint,
                                            responseCollector);
        //wait for retransmissions
        Thread.currentThread().sleep(500);

        //verify
        assertTrue(
            "A MessageEvent was received by a non-interested selective listener",
            requestCollector.receivedRequests.size() == 0);
        assertTrue(
            "No MessageEvents have been dispatched for a selective listener",
            requestCollector2.receivedRequests.size() == 1);
    }


    /**
     * Makes sure that we receive response events.
     * @throws Exception if we screw up.
     */
    public void testServerResponseRetransmissions() throws Exception
    {
        //prepare to listen
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
        Thread.currentThread().sleep(500);

        //verify that we got the response.
        assertTrue(
            "There were no retransmissions of a binding response",
            responseCollector.receivedResponses.size() == 1 );
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
