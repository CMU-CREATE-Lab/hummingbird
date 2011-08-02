package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAnalogInputCommandStrategy extends CreateLabHIDReturnValueCommandStrategy<Integer>
   {
   /** The command character used to request the value of one of the hummingbird's analog inputs. */
   private static final byte COMMAND_PREFIX = 'A';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 2;

   private final int analogInputPortId;

   public GetAnalogInputCommandStrategy(final int analogInputPortId)
      {
      this.analogInputPortId = analogInputPortId;
      }

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return new byte[]{COMMAND_PREFIX};
      }

   @Override
   public Integer convertResponse(final HIDCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return ByteUtils.unsignedByteToInt(response.getData()[analogInputPortId]);
         }
      return null;
      }
   }