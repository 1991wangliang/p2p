/*
 * Stun4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.stun4j;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StunTestSuite
    extends TestCase
{

    public StunTestSuite(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        //attributes
        suite.addTestSuite(net.java.stun4j.attribute.
                           AddressAttributeTest.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           XorOnlyTest.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           AttributeDecoderTest.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           ChangeRequestAttributeTest.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           ErrorCodeAttributeTest.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           UnknownAttributesAttributeTest.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           TestServerAttribute.class);
        suite.addTestSuite(net.java.stun4j.attribute.
                           OptionalAttributeAttributeTest.class);
        //messages
        suite.addTestSuite(net.java.stun4j.message.MessageFactoryTest.class);
        suite.addTestSuite(net.java.stun4j.message.MessageTest.class);

        //stack
        suite.addTestSuite(net.java.stun4j.stack.ShallowStackTest.class);

        //event dispatching
        suite.addTestSuite(net.java.stun4j.MessageEventDispatchingTest.class);

        //transactions
        suite.addTestSuite(net.java.stun4j.TransactionSupportTests.class);

        //client
        suite.addTestSuite(net.java.stun4j.client.StunAddressDiscovererTest.class);
        suite.addTestSuite(net.java.stun4j.client.StunAddressDiscovererTest_v6.class);
        suite.addTestSuite(net.java.stun4j.client.StunAddressDiscovererTest_v4v6.class);

        return suite;
    }
}
