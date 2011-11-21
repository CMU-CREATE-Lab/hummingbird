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
public final class GetState2CommandStrategy extends CreateLabHIDReturnValueCommandStrategy<HummingbirdState2>
   {
   private static final byte COMMAND_PREFIX_1 = 'G';
   private static final byte COMMAND_PREFIX_2 = '2';

   /** The size of the expected response, in bytes */
   public static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 6;

   private final byte[] command = new byte[]{COMMAND_PREFIX_1, COMMAND_PREFIX_2};

   private final HummingbirdProperties hummingbirdProperties;

   public GetState2CommandStrategy(final HummingbirdProperties hummingbirdProperties)
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
   public HummingbirdState2 convertResponse(final HIDCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdState2Impl.create(response.getData(), hummingbirdProperties);
         }
      return null;
      }

   private static final class HummingbirdState2Impl implements HummingbirdState2
      {
      private static final Logger LOG = Logger.getLogger(HummingbirdState2.class);
      private static final String EOL = System.getProperty("line.separator", "\n");

      public static HummingbirdState2 create(final byte[] state, final HummingbirdProperties hummingbirdProperties)
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

         return new HummingbirdState2Impl(state, hummingbirdProperties);
         }

      private final int[] motors;
      private final int[] vibeMotors;

      private HummingbirdState2Impl(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         // read state positions for motors 1 and 2 (first byte is direction, second is speed)
         motors = new int[hummingbirdProperties.getMotorDeviceCount()];
         this.motors[0] = computeMotorVelocity(ByteUtils.unsignedByteToInt(state[0]),
                                               ByteUtils.unsignedByteToInt(state[1]));
         this.motors[1] = computeMotorVelocity(ByteUtils.unsignedByteToInt(state[2]),
                                               ByteUtils.unsignedByteToInt(state[3]));

         // read state positions for the vibe motors
         vibeMotors = new int[hummingbirdProperties.getVibrationMotorDeviceCount()];
         for (int i = 0; i < vibeMotors.length; i++)
            {
            this.vibeMotors[i] = ByteUtils.unsignedByteToInt(state[4 + i]);
            }
         }

      private int computeMotorVelocity(final int direction, final int speed)
         {
         // convention is that a direction of 0 means negative, 1 means positive
         final int sign = (direction == 0) ? -1 : 1;

         return sign * speed;
         }

      @Override
      public int[] getMotorVelocities()
         {
         return motors.clone();
         }

      @Override
      public int[] getVibrationMotorSpeeds()
         {
         return vibeMotors.clone();
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

         final HummingbirdState2Impl that = (HummingbirdState2Impl)o;

         if (!Arrays.equals(motors, that.motors))
            {
            return false;
            }
         if (!Arrays.equals(vibeMotors, that.vibeMotors))
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = motors != null ? Arrays.hashCode(motors) : 0;
         result = 31 * result + (vibeMotors != null ? Arrays.hashCode(vibeMotors) : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState2" + EOL);
         for (int i = 0; i < motors.length; i++)
            {
            s.append("   Motor ").append(i).append(":      ").append(motors[i]).append(EOL);
            }
         for (int i = 0; i < vibeMotors.length; i++)
            {
            s.append("   Vibe Motor ").append(i).append(": ").append(vibeMotors[i]).append(EOL);
            }

         return s.toString();
         }
      }
   }
