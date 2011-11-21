package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.HummingbirdVersionNumber;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetState4CommandStrategy extends CreateLabHIDReturnValueCommandStrategy<HummingbirdState4>
   {
   private static final byte COMMAND_PREFIX_1 = 'G';
   private static final byte COMMAND_PREFIX_2 = '4';

   /** The size of the expected response, in bytes */
   public static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 5;

   private final byte[] command = new byte[]{COMMAND_PREFIX_1, COMMAND_PREFIX_2};

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
   public HummingbirdState4 convertResponse(final HIDCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdState4Impl.create(response.getData());
         }
      return null;
      }

   private static final class HummingbirdState4Impl implements HummingbirdState4
      {
      private static final Logger LOG = Logger.getLogger(HummingbirdState4.class);
      private static final String EOL = System.getProperty("line.separator", "\n");

      public static HummingbirdState4 create(final byte[] state)
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

         return new HummingbirdState4Impl(state);
         }

      private final HummingbirdVersionNumber hardwareVersion;
      private final HummingbirdVersionNumber firmwareVersion;

      private HummingbirdState4Impl(final byte[] state)
         {
         hardwareVersion = new HummingbirdVersionNumber(ByteUtils.unsignedByteToInt(state[0]),
                                                        ByteUtils.unsignedByteToInt(state[1]));
         firmwareVersion = new HummingbirdVersionNumber(ByteUtils.unsignedByteToInt(state[2]),
                                                        ByteUtils.unsignedByteToInt(state[3]),
                                                        String.valueOf((char)state[4]));
         }

      @Override
      public HummingbirdVersionNumber getHardwareVersion()
         {
         return hardwareVersion;
         }

      @Override
      public HummingbirdVersionNumber getFirmwareVersion()
         {
         return firmwareVersion;
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

         final HummingbirdState4Impl that = (HummingbirdState4Impl)o;

         if (firmwareVersion != null ? !firmwareVersion.equals(that.firmwareVersion) : that.firmwareVersion != null)
            {
            return false;
            }
         if (hardwareVersion != null ? !hardwareVersion.equals(that.hardwareVersion) : that.hardwareVersion != null)
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = hardwareVersion != null ? hardwareVersion.hashCode() : 0;
         result = 31 * result + (firmwareVersion != null ? firmwareVersion.hashCode() : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState4" + EOL);
         s.append("   Hardware Version: ").append(hardwareVersion.getMajorMinorRevision()).append(EOL);
         s.append("   Firmware Version: ").append(firmwareVersion.getMajorMinorRevision()).append(EOL);

         return s.toString();
         }
      }
   }
