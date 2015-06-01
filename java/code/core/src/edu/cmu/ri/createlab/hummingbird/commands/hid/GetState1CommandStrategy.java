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
public final class GetState1CommandStrategy extends CreateLabHIDReturnValueCommandStrategy<HummingbirdState1>
   {
   private static final byte COMMAND_PREFIX_1 = 'G';
   private static final byte COMMAND_PREFIX_2 = '1';

   /** The size of the expected response, in bytes */
   public static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 7;

   private final byte[] command = new byte[]{COMMAND_PREFIX_1, COMMAND_PREFIX_2};

   private final HummingbirdProperties hummingbirdProperties;

   public GetState1CommandStrategy(final HummingbirdProperties hummingbirdProperties)
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
   public HummingbirdState1 convertResponse(final HIDCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdState1Impl.create(response.getData(), hummingbirdProperties);
         }
      return null;
      }

   private static final class HummingbirdState1Impl implements HummingbirdState1
      {
      private static final Logger LOG = Logger.getLogger(HummingbirdState1.class);
      private static final String EOL = System.getProperty("line.separator", "\n");

      public static HummingbirdState1 create(final byte[] state, final HummingbirdProperties hummingbirdProperties)
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

         return new HummingbirdState1Impl(state, hummingbirdProperties);
         }

      private final int[] leds;
      private final int[] servos;

      private HummingbirdState1Impl(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         // read state positions 0-2 for the LEDs
         leds = new int[3];
         for (int i = 0; i < leds.length; i++)
            {
            this.leds[i] = ByteUtils.unsignedByteToInt(state[i]);
            }

         // read state positions 3-6 for the servos
         servos = new int[hummingbirdProperties.getSimpleServoDeviceCount()];
         for (int i = 0; i < servos.length; i++)
            {
            this.servos[i] = ByteUtils.unsignedByteToInt(state[3 + i]);
            }
         }

      @Override
      public int[] getLedIntensities()
         {
         return leds.clone();
         }

      @Override
      public int[] getServoPositions()
         {
         return servos.clone();
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

         final HummingbirdState1Impl that = (HummingbirdState1Impl)o;

         if (!Arrays.equals(leds, that.leds))
            {
            return false;
            }
         if (!Arrays.equals(servos, that.servos))
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = leds != null ? Arrays.hashCode(leds) : 0;
         result = 31 * result + (servos != null ? Arrays.hashCode(servos) : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState1" + EOL);
         for (int i = 0; i < leds.length; i++)
            {
            s.append("   LED ").append(i + 1).append(":        ").append(leds[i]).append(EOL);
            }
         for (int i = 0; i < servos.length; i++)
            {
            s.append("   Servo ").append(i).append(":      ").append(servos[i]).append(EOL);
            }

         return s.toString();
         }
      }
   }
