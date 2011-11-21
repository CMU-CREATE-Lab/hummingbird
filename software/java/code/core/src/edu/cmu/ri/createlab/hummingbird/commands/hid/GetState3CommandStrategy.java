package edu.cmu.ri.createlab.hummingbird.commands.hid;

import java.util.Arrays;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetState3CommandStrategy extends CreateLabHIDReturnValueCommandStrategy<HummingbirdState3>
   {
   private static final byte COMMAND_PREFIX_1 = 'G';
   private static final byte COMMAND_PREFIX_2 = '3';

   /** The size of the expected response, in bytes */
   public static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 5;

   private final byte[] command = new byte[]{COMMAND_PREFIX_1, COMMAND_PREFIX_2};

   private final HummingbirdProperties hummingbirdProperties;

   public GetState3CommandStrategy(final HummingbirdProperties hummingbirdProperties)
      {
      this.hummingbirdProperties = hummingbirdProperties;
      }

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }

   @Override
   public HummingbirdState3 convertResponse(final HIDCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdState3Impl.create(response.getData(), hummingbirdProperties);
         }
      return null;
      }

   private static final class HummingbirdState3Impl implements HummingbirdState3
      {
      private static final Logger LOG = Logger.getLogger(HummingbirdState3.class);
      private static final String EOL = System.getProperty("line.separator", "\n");

      public static HummingbirdState3 create(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         if (state == null)
            {
            LOG.error("Invalid state array.  The state array cannot be null.");
            return null;
            }
         if (state.length != SIZE_IN_BYTES_OF_EXPECTED_RESPONSE)
            {
            LOG.error("Invalid state array.  Array must be exactly " + SIZE_IN_BYTES_OF_EXPECTED_RESPONSE + " bytes.  Received array was " + state.length + " byte(s).");
            return null;
            }

         return new HummingbirdState3Impl(state, hummingbirdProperties);
         }

      private final int[] analogInputs;
      private final boolean isMotorPowerEnabled;

      private HummingbirdState3Impl(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         // read state positions for the analog inputs
         analogInputs = new int[hummingbirdProperties.getAnalogInputDeviceCount()];
         for (int i = 0; i < analogInputs.length; i++)
            {
            this.analogInputs[i] = ByteUtils.unsignedByteToInt(state[i]);
            }

         // read state of motor power
         isMotorPowerEnabled = ByteUtils.unsignedByteToInt(state[4]) == 1;
         }

      private int computeMotorVelocity(final int direction, final int speed)
         {
         // convention is that a direction of 0 means negative, 1 means positive
         final int sign = (direction == 0) ? -1 : 1;

         return sign * speed;
         }

      @Override
      public int[] getAnalogInputValues()
         {
         return analogInputs.clone();
         }

      @Override
      public boolean isMotorPowerEnabled()
         {
         return isMotorPowerEnabled;
         }

      @Override
      public boolean equals(final Object o)
         {
         if (this == o)
            {
            return true;
            }
         if (o == null || getClass() != o.getClass())
            {
            return false;
            }

         final HummingbirdState3Impl that = (HummingbirdState3Impl)o;

         if (isMotorPowerEnabled != that.isMotorPowerEnabled)
            {
            return false;
            }
         if (!Arrays.equals(analogInputs, that.analogInputs))
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = analogInputs != null ? Arrays.hashCode(analogInputs) : 0;
         result = 31 * result + (isMotorPowerEnabled ? 1 : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState3" + EOL);
         for (int i = 0; i < analogInputs.length; i++)
            {
            s.append("   Sensor ").append(i).append(":     ").append(analogInputs[i]).append(EOL);
            }
         s.append("   Motor Power Enabled: ").append(isMotorPowerEnabled).append(EOL);

         return s.toString();
         }
      }
   }
