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
 * Makes basic stun tests for cases where local network addresses and the public
 * NAT address are using different IP versions. (e.g. Local addresses are v4
 * public NAT address is v6 or vice versa)
 *
 *
 * The StunAddressDiscovererTest_XXX set of tests were created to verify stun
 * operation for scenarios of some basic types of firewalls. The purpose of
 * these tests is to make sure that transaction retransmissions and rereceptions
 * are handled transparently by the stack, as well as verify overal protocol
 * operations for IPv4/IPv6 and mixed environments.
 *
 * <p>Company: Net Research Team, Louis Pasteur University</p>
 * <p>@author Emil Ivov</p>
 */
public class StunAddressDiscovererTest_v4v6 extends TestCase {
    private NetworkConfigurationDiscoveryProcess  stunAddressDiscoverer_v6 = null;
    private NetworkConfigurationDiscoveryProcess  stunAddressDiscoverer_v4 = null;

    private StunAddress            discovererAddress_v4     = new StunAddress("127.0.0.1", 17555);
    private StunAddress            discovererAddress_v6     = new StunAddress("::1", 17555);

    private ResponseSequenceServer responseServer_v6        = null;
    private ResponseSequenceServer responseServer_v4        = null;

    private StunAddress            responseServerAddress_v6 = new StunAddress("::1", 21999);
    private StunAddress            responseServerAddress_v4 = new StunAddress("127.0.0.1", 21999);

    private StunAddress            mappedClientAddress_v6 = new StunAddress("2001:660:4701:1001:ff::1", 17612);
    private StunAddress            mappedClientAddress_v6_Port2 = new StunAddress("2001:660:4701:1001:ff::1", 17611);

    private StunAddress            mappedClientAddress_v4 = new StunAddress("130.79.99.55", 17612);
    private StunAddress            mappedClientAddress_v4_Port2 = new StunAddress("130.79.99.55", 17611);

    public StunAddressDiscovererTest_v4v6(String name)
        throws StunException
    {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        responseServer_v6 = new ResponseSequenceServer(responseServerAddress_v6);
        responseServer_v4 = new ResponseSequenceServer(responseServerAddress_v4);
        stunAddressDiscoverer_v6 = new NetworkConfigurationDiscoveryProcess(discovererAddress_v6, responseServerAddress_v6);
        stunAddressDiscoverer_v4 = new NetworkConfigurationDiscoveryProcess(discovererAddress_v4, responseServerAddress_v4);

        stunAddressDiscoverer_v6.start();
        stunAddressDiscoverer_v4.start();
        responseServer_v6.start();
        responseServer_v4.start();
    }

    protected void tearDown() throws Exception {

        responseServer_v6.shutDown();
        responseServer_v4.shutDown();
        stunAddressDiscoverer_v6.shutDown();
        stunAddressDiscoverer_v6 = null;
        stunAddressDiscoverer_v4.shutDown();
        stunAddressDiscoverer_v4 = null;

        //give the sockets the time to clear out
        Thread.currentThread().sleep(1000);

        super.tearDown();
    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Symmetric NAT.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeSymmetricNat_Local_v6_Public_v4() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
                                                        mappedClientAddress_v4, responseServerAddress_v6, responseServerAddress_v6);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
                                                        mappedClientAddress_v4_Port2, responseServerAddress_v6, responseServerAddress_v6);

        responseServer_v6.addMessage(testIResponse1);
        responseServer_v6.addMessage(testIResponse2);
        responseServer_v6.addMessage(testIResponse3);


        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.SYMMETRIC_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress_v4);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer_v6.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a v4-v6 sym env.",
                     expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Symmetric NAT.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeSymmetricNat_Local_v4_Public_v6() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
                                                        mappedClientAddress_v6, responseServerAddress_v4, responseServerAddress_v4);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
                                                        mappedClientAddress_v6_Port2, responseServerAddress_v4, responseServerAddress_v4);

        responseServer_v4.addMessage(testIResponse1);
        responseServer_v4.addMessage(testIResponse2);
        responseServer_v4.addMessage(testIResponse3);


        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.SYMMETRIC_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress_v6);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer_v4.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);

    }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Full Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeFullCone_Local_v6_Public_v4() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            mappedClientAddress_v4, responseServerAddress_v6, responseServerAddress_v6);
        Response testIResponse2 = MessageFactory.createBindingResponse(
            mappedClientAddress_v4, responseServerAddress_v6, responseServerAddress_v6);

        responseServer_v6.addMessage(testIResponse1);
        responseServer_v6.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.FULL_CONE_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress_v4);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer_v6.
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
    public void testRecognizeFullCone_Local_v4_Public_v6() throws StunException
    {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            mappedClientAddress_v6, responseServerAddress_v4, responseServerAddress_v4);
        Response testIResponse2 = MessageFactory.createBindingResponse(
            mappedClientAddress_v6, responseServerAddress_v4, responseServerAddress_v4);

        responseServer_v4.addMessage(testIResponse1);
        responseServer_v4.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.FULL_CONE_NAT);
        expectedReturn.setPublicAddress(mappedClientAddress_v6);

        StunDiscoveryReport actualReturn = stunAddressDiscoverer_v4.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

    }
}
