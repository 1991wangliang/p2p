/*
 * Stun4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.stun4j.client;

import junit.framework.TestCase;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Response;

/**
 * The StunAddressDiscovererTest_XXX set of tests were created to verify stun
 * operation for scenarios of some basic types of firewalls. The purpose of
 * these tests is to make sure that transaction retransmissions and rereceptions
 * are handled transparently by the stack, as well as verify overal protocol
 * operations for IPv4/IPv6 and mixed environments.
 *
 * <p>Company: Net Research Team, Louis Pasteur University</p>
 * <p>@author Emil Ivov</p>
 */
public class StunAddressDiscovererTest_v6 extends TestCase {
    private NetworkConfigurationDiscoveryProcess  stunAddressDiscoverer = null;
    private StunAddress            discovererAddress     = new StunAddress("::1", 16555);

    private ResponseSequenceServer responseServer        = null;
    private StunAddress            responseServerAddress = new StunAddress("::1", 20999);

    private StunAddress            mappedClientAddress = new StunAddress("2001:660:4701:1001:ff::1", 16612);
    private StunAddress            mappedClientAddressPort2 = new StunAddress("2001:660:4701:1001:ff::1", 16611);

    public StunAddressDiscovererTest_v6(String name)
        throws StunException
    {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        responseServer = new ResponseSequenceServer(responseServerAddress);
        stunAddressDiscoverer = new NetworkConfigurationDiscoveryProcess(discovererAddress, responseServerAddress);

        stunAddressDiscoverer.start();
        responseServer.start();
    }

    protected void tearDown() throws Exception {

        responseServer.shutDown();
        stunAddressDiscoverer.shutDown();
        stunAddressDiscoverer = null;

        super.tearDown();
    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it's in a network where UDP is blocked.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeBlockedUDP() throws StunException {

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.UDP_BLOCKING_FIREWALL);
        expectedReturn.setPublicAddress(null);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);

    }


    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Symmetric NAT.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeSymmetricNat() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
                                                        mappedClientAddress, responseServerAddress, responseServerAddress);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
                                                        mappedClientAddressPort2, responseServerAddress, responseServerAddress);

        responseServer.addMessage(testIResponse1);
        responseServer.addMessage(testIResponse2);
        responseServer.addMessage(testIResponse3);


        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.SYMMETRIC_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Port Restricted Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizePortRestrictedCone() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);
        Response testIResponse4 = null;

        responseServer.addMessage(testIResponse1);
        responseServer.addMessage(testIResponse2);
        responseServer.addMessage(testIResponse3);
        responseServer.addMessage(testIResponse4);


        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.PORT_RESTRICTED_CONE_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Restricted Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeRestrictedCone() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);
        Response testIResponse4 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);

        responseServer.addMessage(testIResponse1);
        responseServer.addMessage(testIResponse2);
        responseServer.addMessage(testIResponse3);
        responseServer.addMessage(testIResponse4);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.RESTRICTED_CONE_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Full Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeFullCone() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);
        Response testIResponse2 = MessageFactory.createBindingResponse(
            mappedClientAddress, responseServerAddress, responseServerAddress);

        responseServer.addMessage(testIResponse1);
        responseServer.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.FULL_CONE_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a UDP Symmetric Firewall.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeUdpSymmetricFirewall() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            discovererAddress, responseServerAddress, responseServerAddress);
        Response testIResponse2 = null;

        responseServer.addMessage(testIResponse1);
        responseServer.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.SYMMETRIC_UDP_FIREWALL);
        expectedReturn.setPublicAddress(discovererAddress);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Open Internet.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeOpenInternet() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            discovererAddress, responseServerAddress, responseServerAddress);
        Response testIResponse2 = MessageFactory.createBindingResponse(
            discovererAddress, responseServerAddress, responseServerAddress);

        responseServer.addMessage(testIResponse1);
        responseServer.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.OPEN_INTERNET);
        expectedReturn.setPublicAddress(discovererAddress);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

    }





}
