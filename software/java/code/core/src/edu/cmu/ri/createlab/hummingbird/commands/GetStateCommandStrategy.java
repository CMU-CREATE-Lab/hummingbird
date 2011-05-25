package edu.cmu.ri.createlab.hummingbird.commands;

import java.awt.Color;
import java.util.Arrays;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetStateCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Hummingbird.HummingbirdState>
   {
   /** The command character used to request the hummingbird's state. */
   private static final byte COMMAND_PREFIX = 'G';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = HummingbirdConstants.SIZE_IN_BYTES_OF_STATE_ARRAY;

   private final byte[] command;

   public GetStateCommandStrategy()
      {
      this.command = new byte[]{COMMAND_PREFIX};
      }

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }

   @Override
   public Hummingbird.HummingbirdState convertResponse(final SerialPortCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdStateImpl.create(response.getData());
         }
      return null;
      }

   /**
    * <p>
    * <code>HummingbirdState</code> represents the internal state of the hummingbird.  Instances of this class are immutable.
    * </p>
    *
    * @author Chris Bartley (bartley@cmu.edu)
    */
   private static final class HummingbirdStateImpl implements Hummingbird.HummingbirdState
      {
      private static final Logger LOG = Logger.getLogger(HummingbirdStateImpl.class);

      private static final String EOL = System.getProperty("line.separator", "\n");

      private final Orb[] orbs = new Orb[HummingbirdConstants.FULL_COLOR_LED_DEVICE_COUNT];
      private final int[] leds = new int[HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT];
      private final int[] servos = new int[HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT];
      private final int[] motors = new int[HummingbirdConstants.MOTOR_DEVICE_COUNT];
      private final int[] vibeMotors = new int[HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT];
      private final int[] analogInputs = new int[HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT];

      /**
       * Creates a new <code>HummingbirdState</code> using the given state array.  Returns <code>null</code> if the given
       * array is null or of a size other than {@link HummingbirdConstants#SIZE_IN_BYTES_OF_STATE_ARRAY}.
       */
      private static HummingbirdStateImpl create(final byte[] state)
         {
         if (state == null)
            {
            LOG.error("Invalid state array.  The state array cannot be null.");
            return null;
            }
         if (state.length != HummingbirdConstants.SIZE_IN_BYTES_OF_STATE_ARRAY)
            {
            LOG.error("Invalid state array.  Array must be exactly " + HummingbirdConstants.SIZE_IN_BYTES_OF_STATE_ARRAY + " bytes.  Received array was " + state.length + " byte(s).");
            return null;
            }

         return new HummingbirdStateImpl(state);
         }

      private HummingbirdStateImpl(final byte[] state)
         {
         // read state positions 0-2 for the first full-color LED
         this.orbs[0] = new Orb(ByteUtils.unsignedByteToInt(state[0]),
                                ByteUtils.unsignedByteToInt(state[1]),
                                ByteUtils.unsignedByteToInt(state[2]));

         // read state positions 3-5 for the second full-color LED
         this.orbs[1] = new Orb(ByteUtils.unsignedByteToInt(state[3]),
                                ByteUtils.unsignedByteToInt(state[4]),
                                ByteUtils.unsignedByteToInt(state[5]));

         // read state positions 6-9 for the LEDs
         for (int i = 0; i < leds.length; i++)
            {
            this.leds[i] = ByteUtils.unsignedByteToInt(state[6 + i]);
            }

         // read state positions 10-13 for the LEDs
         for (int i = 0; i < servos.length; i++)
            {
            this.servos[i] = ByteUtils.unsignedByteToInt(state[10 + i]);
            }

         // read state positions 14-17 for motors 1 and 2 (first byte is direction, second is speed)
         this.motors[0] = computeMotorVelocity(ByteUtils.unsignedByteToInt(state[14]),
                                               ByteUtils.unsignedByteToInt(state[15]));
         this.motors[1] = computeMotorVelocity(ByteUtils.unsignedByteToInt(state[16]),
                                               ByteUtils.unsignedByteToInt(state[17]));

         // read state positions 18-19 for the vibe motors
         for (int i = 0; i < vibeMotors.length; i++)
            {
            this.vibeMotors[i] = ByteUtils.unsignedByteToInt(state[18 + i]);
            }

         // read state positions 20-21 for the analog inputs
         for (int i = 0; i < analogInputs.length; i++)
            {
            this.analogInputs[i] = ByteUtils.unsignedByteToInt(state[20 + i]);
            }
         }

      private int computeMotorVelocity(final int direction, final int speed)
         {
         // convention is that a direction of 0 means negative, 1 means positive
         final int sign = (direction == 0) ? -1 : 1;

         return sign * speed;
         }

      public Color[] getFullColorLEDs()
         {
         return new Color[]{orbs[0].getColor(),
                            orbs[1].getColor()};
         }

      public int[] getLedIntensities()
         {
         return leds.clone();
         }

      public int[] getServoPositions()
         {
         return servos.clone();
         }

      public int[] getMotorVelocities()
         {
         return motors.clone();
         }

      public int[] getVibrationMotorSpeeds()
         {
         return vibeMotors.clone();
         }

      public int[] getAnalogInputValues()
         {
         return analogInputs.clone();
         }

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

         final HummingbirdStateImpl that = (HummingbirdStateImpl)o;

         if (!Arrays.equals(leds, that.leds))
            {
            return false;
            }
         if (!Arrays.equals(motors, that.motors))
            {
            return false;
            }
         if (!Arrays.equals(orbs, that.orbs))
            {
            return false;
            }
         if (!Arrays.equals(analogInputs, that.analogInputs))
            {
            return false;
            }
         if (!Arrays.equals(servos, that.servos))
            {
            return false;
            }
         if (!Arrays.equals(vibeMotors, that.vibeMotors))
            {
            return false;
            }

         return true;
         }

      public int hashCode()
         {
         int result = (orbs != null ? Arrays.hashCode(orbs) : 0);
         result = 31 * result + (leds != null ? Arrays.hashCode(leds) : 0);
         result = 31 * result + (servos != null ? Arrays.hashCode(servos) : 0);
         result = 31 * result + (motors != null ? Arrays.hashCode(motors) : 0);
         result = 31 * result + (vibeMotors != null ? Arrays.hashCode(vibeMotors) : 0);
         result = 31 * result + (analogInputs != null ? Arrays.hashCode(analogInputs) : 0);
         return result;
         }

      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState" + EOL);
         for (int i = 0; i < orbs.length; i++)
            {
            s.append("   Orb ").append(i + 1).append(":        (").append(orbs[i].getR()).append(",").append(orbs[i].getG()).append(",").append(orbs[i].getB()).append(")").append(EOL);
            }
         for (int i = 0; i < leds.length; i++)
            {
            s.append("   LED ").append(i + 1).append(":        ").append(leds[i]).append(EOL);
            }
         for (int i = 0; i < servos.length; i++)
            {
            s.append("   Servo ").append(i + 1).append(":      ").append(servos[i]).append(EOL);
            }
         for (int i = 0; i < motors.length; i++)
            {
            s.append("   Motor ").append(i + 1).append(":      ").append(motors[i]).append(EOL);
            }
         for (int i = 0; i < vibeMotors.length; i++)
            {
            s.append("   Vibe Motor ").append(i + 1).append(": ").append(vibeMotors[i]).append(EOL);
            }
         for (int i = 0; i < analogInputs.length; i++)
            {
            s.append("   Sensor ").append(i + 1).append(":     ").append(analogInputs[i]).append(EOL);
            }

         return s.toString();
         }

      private static final class Orb
         {
         private final int r;
         private final int g;
         private final int b;

         private Orb(final int r, final int g, final int b)
            {
            this.r = r;
            this.g = g;
            this.b = b;
            }

         public int getR()
            {
            return r;
            }

         public int getG()
            {
            return g;
            }

         public int getB()
            {
            return b;
            }

         public Color getColor()
            {
            return new Color(r, g, b);
            }

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

            final Orb that = (Orb)o;

            if (b != that.b)
               {
               return false;
               }
            if (g != that.g)
               {
               return false;
               }
            if (r != that.r)
               {
               return false;
               }

            return true;
            }

         public int hashCode()
            {
            int result = r;
            result = 31 * result + g;
            result = 31 * result + b;
            return result;
            }
         }
      }
   }