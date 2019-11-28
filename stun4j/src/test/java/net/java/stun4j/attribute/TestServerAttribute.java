/*
 * Stun4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.stun4j.attribute;

import junit.framework.TestCase;
import net.java.stun4j.MsgFixture;
import net.java.stun4j.StunException;

import java.util.Arrays;

/**
 * Tests the server attribute class.
 * <p>Organization Network Research Team, Louis Pasteur University</p>
 * <p>@author Emil Ivov</p>
 */
public class TestServerAttribute extends TestCase
{
    private ServerAttribute serverAttribute = null;
    MsgFixture msgFixture = null;
    String serverValue = "turnserver.org";
    byte[] attributeBinValue = new byte[]{
            (byte)(ServerAttribute.SERVER>>8),
            (byte)(ServerAttribute.SERVER & 0x00FF),
            0, (byte)serverValue.length(),
            't', 'u', 'r', 'n', 's', 'e', 'r','v', 'e', 'r', '.', 'o', 'r', 'g'};

    protected void setUp() throws Exception
    {
        super.setUp();
        msgFixture = new MsgFixture();

        serverAttribute = new ServerAttribute();
        serverAttribute.setServer(serverValue.getBytes());

        msgFixture.setUp();
    }

    protected void tearDown() throws Exception
    {
        serverAttribute = null;
        msgFixture.tearDown();

        msgFixture = null;
        super.tearDown();
    }

    /**
     * Tests decoding of the server attribute.
     * @throws StunException upon a failure
     */
    public void testDecodeAttributeBody() throws StunException
    {
        char offset = 0;
        ServerAttribute decoded = new ServerAttribute();
        char length = (char)serverValue.length();
        decoded.decodeAttributeBody(serverValue.getBytes(), offset, length);

        //server value
        assertEquals( "decode failed", serverAttribute, decoded);
    }

    /**
     * Tests the encode method
     */
    public void testEncode()
    {
        assertTrue("encode failed",
                   Arrays.equals(serverAttribute.encode(),
                                 attributeBinValue));
    }

    /**
     * Test Equals
     */
    public void testEquals()
    {
        ServerAttribute serverAttribute2 = new ServerAttribute();
        serverAttribute2.setServer(serverValue.getBytes());

        //test positive equals
        assertEquals("testequals failed", serverAttribute, serverAttribute2);

        //test negative equals
        serverAttribute2 = new ServerAttribute();
        serverAttribute2.setServer("some other server".getBytes());

        //test positive equals
        assertFalse("testequals failed",
                    serverAttribute.equals(serverAttribute2));

        //test null equals
        assertFalse("testequals failed",
                    serverAttribute.equals(null));
    }

    /**
     * Tests extracting data length
     */
    public void testGetDataLength()
    {
        char expectedReturn = (char)serverValue.length();
        char actualReturn = serverAttribute.getDataLength();
        assertEquals("getDataLength - failed", expectedReturn, actualReturn);
    }

    /**
     * Tests getting the name
     */
    public void testGetName()
    {
        String expectedReturn = "SERVER";
        String actualReturn = serverAttribute.getName();
        assertEquals("getting name failed", expectedReturn, actualReturn);
    }

    public void testSetGetServer()
    {
        byte[] expectedReturn = serverValue.getBytes();

        ServerAttribute att = new ServerAttribute();
        att.setServer(expectedReturn);

        byte[] actualReturn = att.getServer();
        assertTrue("server setter or getter failed",
                     Arrays.equals( expectedReturn,
                                    actualReturn));
    }
}
