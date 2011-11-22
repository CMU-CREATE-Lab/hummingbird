package edu.cmu.ri.createlab.hummingbird.commands;

import java.awt.Color;
import java.util.Arrays;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetStateCommandStrategyHelper
   {
   /** The command character used to request the hummingbird's state. */
   private static final byte COMMAND_PREFIX = 'G';

   /** The size, in bytes, of the state array */
   private static final int SIZE_IN_BYTES_OF_STATE_ARRAY = 22;

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = SIZE_IN_BYTES_OF_STATE_ARRAY;

   private final byte[] command;
   private final HummingbirdProperties hummingbirdProperties;

   public GetStateCommandStrategyHelper(final HummingbirdProperties hummingbirdProperties)
      {
      this.hummingbirdProperties = hummingbirdProperties;
      this.command = new byte[]{COMMAND_PREFIX};
      }

   public int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   public byte[] getCommand()
      {
      return command.clone();
      }

   public Hummingbird.HummingbirdState convertResponse(final CommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return HummingbirdStateImpl.create(response.getData(), hummingbirdProperties);
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

      private static final boolean IS_MOTOR_POWER_ENABLED = true; // always true for serial hummingbirds

      /**
       * Creates a new <code>HummingbirdState</code> using the given state array.  Returns <code>null</code> if the given
       * array is null or of an unexpected size.
       */
      private static HummingbirdStateImpl create(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         if (state == null)
            {
            LOG.error("Invalid state array.  The state array cannot be null.");
            return null;
            }
         if (state.length != SIZE_IN_BYTES_OF_STATE_ARRAY)
            {
            LOG.error("Invalid state array.  Array must be exactly " + SIZE_IN_BYTES_OF_STATE_ARRAY + " bytes.  Received array was " + state.length + " byte(s).");
            return null;
            }

         return new HummingbirdStateImpl(state, hummingbirdProperties);
         }

      private final Orb[] orbs;
      private final int[] leds;
      private final int[] servos;
      private final int[] motors;
      private final int[] vibeMotors;
      private final int[] analogInputs;

      private HummingbirdStateImpl(final byte[] state, final HummingbirdProperties hummingbirdProperties)
         {
         // read state positions 0-2 for the first full-color LED
         orbs = new Orb[hummingbirdProperties.getFullColorLedDeviceCount()];
         this.orbs[0] = new Orb(ByteUtils.unsignedByteToInt(state[0]),
                                ByteUtils.unsignedByteToInt(state[1]),
                                ByteUtils.unsignedByteToInt(state[2]));

         // read state positions 3-5 for the second full-color LED
         this.orbs[1] = new Orb(ByteUtils.unsignedByteToInt(state[3]),
                                ByteUtils.unsignedByteToInt(state[4]),
                                ByteUtils.unsignedByteToInt(state[5]));

         // read state positions 6-9 for the LEDs
         leds = new int[hummingbirdProperties.getSimpleLedDeviceCount()];
         for (int i = 0; i < leds.length; i++)
            {
            this.leds[i] = ByteUtils.unsignedByteToInt(state[6 + i]);
            }

         // read state positions 10-13 for the servos
         servos = new int[hummingbirdProperties.getSimpleServoDeviceCount()];
         for (int i = 0; i < servos.length; i++)
            {
            this.servos[i] = ByteUtils.unsignedByteToInt(state[10 + i]);
            }

         // read state positions 14-17 for motors 1 and 2 (first byte is direction, second is speed)
         motors = new int[hummingbirdProperties.getMotorDeviceCount()];
         this.motors[0] = computeMotorVelocity(ByteUtils.unsignedByteToInt(state[14]),
                                               ByteUtils.unsignedByteToInt(state[15]));
         this.motors[1] = computeMotorVelocity(ByteUtils.unsignedByteToInt(state[16]),
                                               ByteUtils.unsignedByteToInt(state[17]));

         // read state positions 18-19 for the vibe motors
         vibeMotors = new int[hummingbirdProperties.getVibrationMotorDeviceCount()];
         for (int i = 0; i < vibeMotors.length; i++)
            {
            this.vibeMotors[i] = ByteUtils.unsignedByteToInt(state[18 + i]);
            }

         // read state positions 20-21 for the analog inputs
         analogInputs = new int[hummingbirdProperties.getAnalogInputDeviceCount()];
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

      @Override
      public Color[] getFullColorLEDs()
         {
         return new Color[]{orbs[0].getColor(),
                            orbs[1].getColor()};
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
      public int[] getAnalogInputValues()
         {
         return analogInputs.clone();
         }

      @Override
      public boolean isMotorPowerEnabled()
         {
         return IS_MOTOR_POWER_ENABLED;
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

         final HummingbirdStateImpl that = (HummingbirdStateImpl)o;

         if (!Arrays.equals(analogInputs, that.analogInputs))
            {
            return false;
            }
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

      @Override
      public int hashCode()
         {
         int result = orbs != null ? Arrays.hashCode(orbs) : 0;
         result = 31 * result + (leds != null ? Arrays.hashCode(leds) : 0);
         result = 31 * result + (servos != null ? Arrays.hashCode(servos) : 0);
         result = 31 * result + (motors != null ? Arrays.hashCode(motors) : 0);
         result = 31 * result + (vibeMotors != null ? Arrays.hashCode(vibeMotors) : 0);
         result = 31 * result + (analogInputs != null ? Arrays.hashCode(analogInputs) : 0);
         result = 31 * result + (IS_MOTOR_POWER_ENABLED ? 1 : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState" + EOL);
         for (int i = 0; i < orbs.length; i++)
            {
            s.append("   Orb ").append(i).append(":        (").append(orbs[i].getR()).append(",").append(orbs[i].getG()).append(",").append(orbs[i].getB()).append(")").append(EOL);
            }
         for (int i = 0; i < leds.length; i++)
            {
            s.append("   LED ").append(i).append(":        ").append(leds[i]).append(EOL);
            }
         for (int i = 0; i < servos.length; i++)
            {
            s.append("   Servo ").append(i).append(":      ").append(servos[i]).append(EOL);
            }
         for (int i = 0; i < motors.length; i++)
            {
            s.append("   Motor ").append(i).append(":      ").append(motors[i]).append(EOL);
            }
         for (int i = 0; i < vibeMotors.length; i++)
            {
            s.append("   Vibe Motor ").append(i).append(": ").append(vibeMotors[i]).append(EOL);
            }
         for (int i = 0; i < analogInputs.length; i++)
            {
            s.append("   Sensor ").append(i).append(":     ").append(analogInputs[i]).append(EOL);
            }
         s.append("   Motor Power Enabled: ").append(IS_MOTOR_POWER_ENABLED).append(EOL);

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

         @Override
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