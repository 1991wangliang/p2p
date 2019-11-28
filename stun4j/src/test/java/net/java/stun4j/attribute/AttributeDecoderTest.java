/*
 * Stun4j, the OpenSource Java Solution for NAT and Firewall Traversal.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.stun4j.attribute;

import junit.framework.TestCase;
import net.java.stun4j.MsgFixture;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;


/**
 * We have already tested individual decode methods, so our job here
 * is to verify that that AttributeDecoder.decode distributes the right way.
 */
public class AttributeDecoderTest extends TestCase {
    private AttributeDecoder attributeDecoder = null;
    private MsgFixture msgFixture;
    private byte[] expectedAttributeValue = null;

    public AttributeDecoderTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        attributeDecoder = new AttributeDecoder();
        msgFixture = new MsgFixture();

        //init a sample body
        int offset = Attribute.HEADER_LENGTH;
        expectedAttributeValue =
            new byte[msgFixture.unknownOptionalAttribute.length - offset];
        System.arraycopy(msgFixture.unknownOptionalAttribute, offset,
                         expectedAttributeValue, 0,
                         expectedAttributeValue.length);

        msgFixture.setUp();
    }

    protected void tearDown() throws Exception {
        attributeDecoder = null;
        msgFixture.tearDown();

        msgFixture = null;
        super.tearDown();
    }

    public void testDecodeMappedAddress()
        throws StunException
    {
        //
        byte[] bytes = msgFixture.mappedAddress;
        char offset = 0;
        char length = (char)bytes.length;

        //create the message
        MappedAddressAttribute expectedReturn = new MappedAddressAttribute();

        expectedReturn.setAddress(
                            new StunAddress(msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                        msgFixture.ADDRESS_ATTRIBUTE_PORT));

        Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
        assertEquals("AttributeDecoder.decode() failed for a MAPPED-ADDRESS attribute",
                     expectedReturn, actualReturn);
    }

    public void testDecodeMappedAddress_v6()
        throws StunException
    {
        //
        byte[] bytes = msgFixture.mappedAddressv6;
        char offset = 0;
        char length = (char)bytes.length;

        //create the message
        MappedAddressAttribute expectedReturn = new MappedAddressAttribute();

        expectedReturn.setAddress(
                            new StunAddress(msgFixture.ADDRESS_ATTRIBUTE_ADDRESS_V6,
                                        msgFixture.ADDRESS_ATTRIBUTE_PORT));

        Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
        assertEquals("AttributeDecoder.decode() failed for a MAPPED-ADDRESS attribute",
                     expectedReturn, actualReturn);
    }

    public void testDecodeChangeRequest()
        throws StunException
    {
        //
        byte[] bytes = msgFixture.chngReqTestValue1;
        char offset = 0;
        char length = (char)bytes.length;

        //create the message
        ChangeRequestAttribute expectedReturn = new ChangeRequestAttribute();
        expectedReturn.setChangeIpFlag(msgFixture.CHANGE_IP_FLAG_1);
        expectedReturn.setChangePortFlag(msgFixture.CHANGE_PORT_FLAG_1);

        Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
        assertEquals("AttributeDecoder.decode() failed for a CHANGE-REQUEST attribute",
                     expectedReturn, actualReturn);

    }


   public void testDecodeErrorCode()
       throws StunException
   {
       //
       byte[] bytes = msgFixture.errCodeTestValue;
       char offset = 0;
       char length = (char)bytes.length;

       //create the message
       ErrorCodeAttribute expectedReturn = new ErrorCodeAttribute();
       expectedReturn.setErrorClass(msgFixture.ERROR_CLASS);
       expectedReturn.setErrorNumber(msgFixture.ERROR_NUMBER);
       expectedReturn.setReasonPhrase(msgFixture.REASON_PHRASE);

       Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
       assertEquals("AttributeDecoder.decode() failed for a ERROR-CODE attribute",
                    expectedReturn, actualReturn);

   }

   public void testDecodeUnknownAttributes()
       throws StunException
   {
       //unknown attributes
       byte[] bytes = msgFixture.unknownAttsDecodeTestValue;
       char offset = 0;
       char length = (char)msgFixture.mappedAddress.length;

       //create the message
       UnknownAttributesAttribute expectedReturn = new UnknownAttributesAttribute();
       expectedReturn.addAttributeID(msgFixture.UNKNOWN_ATTRIBUTES_1ST_ATT);
       expectedReturn.addAttributeID(msgFixture.UNKNOWN_ATTRIBUTES_2ND_ATT);
       expectedReturn.addAttributeID(msgFixture.UNKNOWN_ATTRIBUTES_3D_ATT);

       Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
       assertEquals("AttributeDecoder.decode() failed for a ERROR-CODE attribute",
                    expectedReturn, actualReturn);

   }

   public void testDecodeUnknownOptionalAttribute()
       throws StunException
   {
       //unknown attributes
       byte[] bytes = msgFixture.unknownOptionalAttribute;
       char offset = 0;
       char length = (char)msgFixture.mappedAddress.length;

       //create the message
       OptionalAttribute expectedReturn =
           new OptionalAttribute(Attribute.UNKNOWN_OPTIONAL_ATTRIBUTE);
       expectedReturn.setBody(expectedAttributeValue, 0,
                              expectedAttributeValue.length);

       Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
       assertEquals("AttributeDecoder.decode() failed for a UNKNOWN_OPTIONAL attribute",
                    expectedReturn, actualReturn);

   }




}
