package edu.cmu.ri.createlab.hummingbird.commands.hid;

import java.awt.Color;
import java.util.Arrays;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetState0CommandStrategy extends CreateLabHIDReturnValueCommandStrategy<HummingbirdState0>
   {
   private static final byte COMMAND_PREFIX_1 = 'G';
   private static final byte COMMAND_PREFIX_2 = '0';

   /** The size of the expected response, in bytes */
   public static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 7;

   private final byte[] command = new byte[]{COMMAND_PREFIX_1, COMMAND_PREFIX_2};

   private final HummingbirdProperties hummingbirdProperties;

   public GetState0CommandStrategy(final HummingbirdProperties hummingbirdProperties)
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
   public HummingbirdState0 convertResponse(final HIDCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdState0Impl.create(response.getData(), hummingbirdProperties);
         }
      return null;
      }

   private static final class HummingbirdState0Impl implements HummingbirdState0
      {
      private static final Logger LOG = Logger.getLogger(HummingbirdState0.class);
      private static final String EOL = System.getProperty("line.separator", "\n");

      public static HummingbirdState0 create(final byte[] state, final HummingbirdProperties hummingbirdProperties)
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

         return new HummingbirdState0Impl(state, hummingbirdProperties);
         }

      private final Color[] fullColorLeds;
      private final int led0Intensity;

      private HummingbirdState0Impl(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         // read state positions 0-2 for the first full-color LED
         fullColorLeds = new Color[hummingbirdProperties.getFullColorLedDeviceCount()];
         this.fullColorLeds[0] = new Color(ByteUtils.unsignedByteToInt(state[0]),
                                           ByteUtils.unsignedByteToInt(state[1]),
                                           ByteUtils.unsignedByteToInt(state[2]));

         // read state positions 3-5 for the second full-color LED
         this.fullColorLeds[1] = new Color(ByteUtils.unsignedByteToInt(state[3]),
                                           ByteUtils.unsignedByteToInt(state[4]),
                                           ByteUtils.unsignedByteToInt(state[5]));

         led0Intensity = ByteUtils.unsignedByteToInt(state[6]);
         }

      @Override
      public Color[] getFullColorLEDs()
         {
         return fullColorLeds.clone();
         }

      @Override
      public int getLed0Intensity()
         {
         return led0Intensity;
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

         final HummingbirdState0Impl that = (HummingbirdState0Impl)o;

         if (led0Intensity != that.led0Intensity)
            {
            return false;
            }
         if (!Arrays.equals(fullColorLeds, that.fullColorLeds))
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = fullColorLeds != null ? Arrays.hashCode(fullColorLeds) : 0;
         result = 31 * result + led0Intensity;
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState0" + EOL);
         for (int i = 0; i < fullColorLeds.length; i++)
            {
            s.append("   Orb ").append(i).append(":        (").append(fullColorLeds[i].getRed()).append(",").append(fullColorLeds[i].getGreen()).append(",").append(fullColorLeds[i].getBlue()).append(")").append(EOL);
            }
         s.append("   LED 0").append(":        ").append(led0Intensity).append(EOL);

         return s.toString();
         }
      }
   }
