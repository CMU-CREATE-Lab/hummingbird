package edu.cmu.ri.createlab.hummingbird;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.commands.hid.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetAnalogInputCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetState0CommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetState1CommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetState2CommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetState3CommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetState4CommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.HummingbirdState0;
import edu.cmu.ri.createlab.hummingbird.commands.hid.HummingbirdState1;
import edu.cmu.ri.createlab.hummingbird.commands.hid.HummingbirdState2;
import edu.cmu.ri.createlab.hummingbird.commands.hid.HummingbirdState3;
import edu.cmu.ri.createlab.hummingbird.commands.hid.HummingbirdState4;
import edu.cmu.ri.createlab.hummingbird.commands.hid.LEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.MotorCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.ServoCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.VibrationMotorCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandExecutionQueue;
import edu.cmu.ri.createlab.usb.hid.HIDConnectionException;
import edu.cmu.ri.createlab.usb.hid.HIDDevice;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceFactory;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotFoundException;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceReturnValueCommandExecutor;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDHummingbirdProxy extends BaseHummingbirdProxy
   {
   private static final Logger LOG = Logger.getLogger(HIDHummingbirdProxy.class);
   private static final HummingbirdVersionNumber DEFAULT_HUMMINGBIRD_VERSION_NUMBER = new HummingbirdVersionNumber(0, 0);

   /**
    * Tries to create a <code>Hummingbird</code> by connecting to a USB HID hummingbird.  Returns <code>null</code> if
    * the connection could not be established.
    */
   static Hummingbird create()
      {
      try
         {
         // create the HID device
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HIDHummingbirdProxy.create(): creating HID device for vendor ID [" +
                      HIDHummingbirdProperties.UsbHidConfiguration.HUMMINGBIRD_HID_DEVICE_DESCRIPTOR.getVendorIdAsHexString() +
                      "] and product ID [" +
                      HIDHummingbirdProperties.UsbHidConfiguration.HUMMINGBIRD_HID_DEVICE_DESCRIPTOR.getProductIdAsHexString() +
                      "]");
            }
         final HIDDevice hidDevice = HIDDeviceFactory.create(HIDHummingbirdProperties.UsbHidConfiguration.HUMMINGBIRD_HID_DEVICE_DESCRIPTOR);

         LOG.debug("HIDHummingbirdProxy.create(): attempting connection...");
         hidDevice.connectExclusively();

         // create the HID device command execution queue (which will attempt to connect to the device)
         final HIDCommandExecutionQueue commandQueue = new HIDCommandExecutionQueue(hidDevice);
         if (commandQueue != null)
            {
            // create the HIDHummingbirdProxy
            final HIDHummingbirdProxy proxy = new HIDHummingbirdProxy(commandQueue, hidDevice);

            // call the emergency stop command immediately, to make sure the LED and motors are turned off.
            proxy.emergencyStop();

            return proxy;
            }
         }
      catch (UnsupportedOperationException e)
         {
         LOG.error("UnsupportedOperationException caught while trying to create the HIDCommandExecutionQueue", e);
         System.exit(1);
         }
      catch (HIDConnectionException e)
         {
         LOG.error("HIDConnectionException while trying to connect to the Hummingbird, returning null", e);
         }
      catch (HIDDeviceNotFoundException ignored)
         {
         LOG.error("HIDDeviceNotFoundException while trying to connect to the Hummingbird, returning null");
         }
      return null;
      }

   private final HummingbirdProperties hummingbirdProperties = HIDHummingbirdProperties.getInstance();

   private final HIDCommandExecutionQueue commandQueue;
   private final HIDDevice hidDevice;

   private final CreateLabHIDCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final Map<Integer, GetAnalogInputCommandStrategy> analogInputCommandStategyMap = new HashMap<Integer, GetAnalogInputCommandStrategy>();
   private final GetState0CommandStrategy getState0CommandStrategy = new GetState0CommandStrategy(hummingbirdProperties);
   private final GetState1CommandStrategy getState1CommandStrategy = new GetState1CommandStrategy(hummingbirdProperties);
   private final GetState2CommandStrategy getState2CommandStrategy = new GetState2CommandStrategy(hummingbirdProperties);
   private final GetState3CommandStrategy getState3CommandStrategy = new GetState3CommandStrategy(hummingbirdProperties);
   private final EmergencyStopCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();

   private final HIDDeviceNoReturnValueCommandExecutor noReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<HummingbirdState0> hummingbirdState0ReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<HummingbirdState1> hummingbirdState1ReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<HummingbirdState2> hummingbirdState2ReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<HummingbirdState3> hummingbirdState3ReturnValueCommandExecutor;

   private final HummingbirdVersionNumber hardwareVersionNumber;
   private final HummingbirdVersionNumber firmwareVersionNumber;

   private HIDHummingbirdProxy(final HIDCommandExecutionQueue commandQueue, final HIDDevice hidDevice)
      {
      this.commandQueue = commandQueue;
      this.hidDevice = hidDevice;

      noReturnValueCommandExecutor = new HIDDeviceNoReturnValueCommandExecutor(commandQueue, this);
      integerReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<Integer>(commandQueue, this);
      hummingbirdState0ReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<HummingbirdState0>(commandQueue, this);
      hummingbirdState1ReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<HummingbirdState1>(commandQueue, this);
      hummingbirdState2ReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<HummingbirdState2>(commandQueue, this);
      hummingbirdState3ReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<HummingbirdState3>(commandQueue, this);

      // get the hardware and firmware version numbers
      final HIDDeviceReturnValueCommandExecutor<HummingbirdState4> hummingbirdState4ReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<HummingbirdState4>(commandQueue, this);
      final HummingbirdState4 hummingbirdState4 = hummingbirdState4ReturnValueCommandExecutor.execute(new GetState4CommandStrategy());
      if (hummingbirdState4 != null)
         {
         if (hummingbirdState4.getHardwareVersion() != null)
            {
            hardwareVersionNumber = hummingbirdState4.getHardwareVersion();
            }
         else
            {
            hardwareVersionNumber = DEFAULT_HUMMINGBIRD_VERSION_NUMBER;
            }
         if (hummingbirdState4.getFirmwareVersion() != null)
            {
            firmwareVersionNumber = hummingbirdState4.getFirmwareVersion();
            }
         else
            {
            firmwareVersionNumber = DEFAULT_HUMMINGBIRD_VERSION_NUMBER;
            }
         }
      else
         {
         hardwareVersionNumber = DEFAULT_HUMMINGBIRD_VERSION_NUMBER;
         firmwareVersionNumber = DEFAULT_HUMMINGBIRD_VERSION_NUMBER;
         }

      // initialize the analog input command strategy map
      for (int i = 0; i < hummingbirdProperties.getAnalogInputDeviceCount(); i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i, hummingbirdProperties));
         }
      }

   @Override
   public String getPortName()
      {
      return hidDevice.getDeviceFilename();
      }

   /** Returns the {@link HummingbirdProperties} for this hummingbird. */
   @Override
   public HummingbirdProperties getHummingbirdProperties()
      {
      return hummingbirdProperties;
      }

   @Override
   public HummingbirdVersionNumber getHardwareVersion()
      {
      return hardwareVersionNumber;
      }

   @Override
   public HummingbirdVersionNumber getFirmwareVersion()
      {
      return firmwareVersionNumber;
      }

   @Override
   public HummingbirdState getState()
      {
      final HummingbirdState0 state0 = hummingbirdState0ReturnValueCommandExecutor.execute(getState0CommandStrategy);
      if (state0 != null)
         {
         final HummingbirdState1 state1 = hummingbirdState1ReturnValueCommandExecutor.execute(getState1CommandStrategy);
         if (state1 != null)
            {
            final HummingbirdState2 state2 = hummingbirdState2ReturnValueCommandExecutor.execute(getState2CommandStrategy);
            if (state2 != null)
               {
               final HummingbirdState3 state3 = hummingbirdState3ReturnValueCommandExecutor.execute(getState3CommandStrategy);
               if (state3 != null)
                  {
                  return new HummingbirdStateImpl(state0, state1, state2, state3, hummingbirdProperties);
                  }
               }
            }
         }
      return null;
      }

   @Override
   public Integer getAnalogInputValue(final int analogInputPortId)
      {
      final GetAnalogInputCommandStrategy strategy = analogInputCommandStategyMap.get(analogInputPortId);

      if (strategy != null)
         {
         return integerReturnValueCommandExecutor.execute(strategy);
         }

      throw new IllegalArgumentException("Invalid analog input port id: [" + analogInputPortId + "]");
      }

   @Override
   public boolean isMotorPowerEnabled()
      {
      final HummingbirdState3 state3 = hummingbirdState3ReturnValueCommandExecutor.execute(getState3CommandStrategy);
      return state3 != null && state3.isMotorPowerEnabled();
      }

   @Override
   public boolean setMotorVelocity(final int motorId, final int velocity)
      {
      return noReturnValueCommandExecutor.execute(new MotorCommandStrategy(motorId, velocity, hummingbirdProperties));
      }

   @Override
   public int[] setMotorVelocities(final boolean[] mask, final int[] velocities)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(velocities.length, hummingbirdProperties.getMotorDeviceCount()));

      boolean wereAllCommandsSuccessful = true;
      for (final int index : maskedIndeces)
         {
         wereAllCommandsSuccessful = wereAllCommandsSuccessful && setMotorVelocity(index, velocities[index]);
         }

      if (wereAllCommandsSuccessful)
         {
         final HummingbirdState2 state = hummingbirdState2ReturnValueCommandExecutor.execute(getState2CommandStrategy);
         if (state != null)
            {
            return state.getMotorVelocities();
            }
         }

      return null;
      }

   @Override
   public boolean setVibrationMotorSpeed(final int motorId, final int speed)
      {
      return noReturnValueCommandExecutor.execute(new VibrationMotorCommandStrategy(motorId, speed, hummingbirdProperties));
      }

   @Override
   public int[] setVibrationMotorSpeeds(final boolean[] mask, final int[] speeds)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(speeds.length, hummingbirdProperties.getVibrationMotorDeviceCount()));

      boolean wereAllCommandsSuccessful = true;
      for (final int index : maskedIndeces)
         {
         wereAllCommandsSuccessful = wereAllCommandsSuccessful && setVibrationMotorSpeed(index, speeds[index]);
         }

      if (wereAllCommandsSuccessful)
         {
         final HummingbirdState2 state = hummingbirdState2ReturnValueCommandExecutor.execute(getState2CommandStrategy);
         if (state != null)
            {
            return state.getVibrationMotorSpeeds();
            }
         }

      return null;
      }

   @Override
   public boolean setServoPosition(final int servoId, final int position)
      {
      return noReturnValueCommandExecutor.execute(new ServoCommandStrategy(servoId, position, hummingbirdProperties));
      }

   @Override
   public int[] setServoPositions(final boolean[] mask, final int[] positions)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(positions.length, hummingbirdProperties.getSimpleServoDeviceCount()));

      boolean wereAllCommandsSuccessful = true;
      for (final int index : maskedIndeces)
         {
         wereAllCommandsSuccessful = wereAllCommandsSuccessful && setServoPosition(index, positions[index]);
         }

      if (wereAllCommandsSuccessful)
         {
         final HummingbirdState1 state = hummingbirdState1ReturnValueCommandExecutor.execute(getState1CommandStrategy);
         if (state != null)
            {
            return state.getServoPositions();
            }
         }

      return null;
      }

   @Override
   public boolean setLED(final int ledId, final int intensity)
      {
      return noReturnValueCommandExecutor.execute(new LEDCommandStrategy(ledId, intensity, hummingbirdProperties));
      }

   @Override
   public int[] setLEDs(final boolean[] mask, final int[] intensities)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(intensities.length, hummingbirdProperties.getSimpleLedDeviceCount()));

      boolean wereAllCommandsSuccessful = true;
      for (final int index : maskedIndeces)
         {
         wereAllCommandsSuccessful = wereAllCommandsSuccessful && setLED(index, intensities[index]);
         }

      if (wereAllCommandsSuccessful)
         {
         // LED state is spread across 2 different states...
         final HummingbirdState0 state0 = hummingbirdState0ReturnValueCommandExecutor.execute(getState0CommandStrategy);
         if (state0 != null)
            {
            final HummingbirdState1 state1 = hummingbirdState1ReturnValueCommandExecutor.execute(getState1CommandStrategy);
            if (state1 != null)
               {
               final int[] intensitiesOfLeds1Through3 = state1.getLedIntensities();
               if (intensitiesOfLeds1Through3 != null)
                  {
                  final int[] actualIntensities = new int[hummingbirdProperties.getSimpleLedDeviceCount()];
                  actualIntensities[0] = state0.getLed0Intensity();
                  for (int i = 0; i < Math.min(intensitiesOfLeds1Through3.length, actualIntensities.length - 1); i++)
                     {
                     actualIntensities[i + 1] = intensitiesOfLeds1Through3[i];
                     }
                  return actualIntensities;
                  }
               }
            }
         }

      return null;
      }

   @Override
   public boolean setFullColorLED(final int ledId, final int red, final int green, final int blue)
      {
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(ledId, red, green, blue, hummingbirdProperties));
      }

   @Override
   public Color[] setFullColorLEDs(final boolean[] mask, final Color[] colors)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(colors.length, hummingbirdProperties.getFullColorLedDeviceCount()));

      boolean wereAllCommandsSuccessful = true;
      for (final int index : maskedIndeces)
         {
         final Color color = colors[index];
         if (color != null)
            {
            wereAllCommandsSuccessful = wereAllCommandsSuccessful && setFullColorLED(index, color.getRed(), color.getGreen(), color.getBlue());
            }
         }

      if (wereAllCommandsSuccessful)
         {
         final HummingbirdState0 state = hummingbirdState0ReturnValueCommandExecutor.execute(getState0CommandStrategy);
         if (state != null)
            {
            return state.getFullColorLEDs();
            }
         }

      return null;
      }

   @Override
   public boolean emergencyStop()
      {
      return noReturnValueCommandExecutor.execute(emergencyStopCommandStrategy);
      }

   @Override
   protected boolean disconnectAndReturnStatus() throws Exception
      {
      return commandQueue.executeAndReturnStatus(disconnectCommandStrategy);
      }

   @Override
   protected void shutdownCommandQueue()
      {
      commandQueue.shutdown();
      }

   @Override
   protected CommandResponse executePingCommand() throws Exception
      {
      return commandQueue.execute(analogInputCommandStategyMap.get(0));
      }

   private static final class HummingbirdStateImpl implements HummingbirdState
      {
      private static final String EOL = System.getProperty("line.separator", "\n");

      private final HummingbirdState0 state0;
      private final HummingbirdState1 state1;
      private final HummingbirdState2 state2;
      private final HummingbirdState3 state3;
      private final HummingbirdProperties hummingbirdProperties;

      private HummingbirdStateImpl(final HummingbirdState0 state0,
                                   final HummingbirdState1 state1,
                                   final HummingbirdState2 state2,
                                   final HummingbirdState3 state3,
                                   final HummingbirdProperties hummingbirdProperties)
         {

         this.state0 = state0;
         this.state1 = state1;
         this.state2 = state2;
         this.state3 = state3;
         this.hummingbirdProperties = hummingbirdProperties;
         }

      @Override
      public Color[] getFullColorLEDs()
         {
         return state0.getFullColorLEDs();
         }

      @Override
      public int[] getLedIntensities()
         {
         final int[] intensitiesOfLeds1Through3 = state1.getLedIntensities();
         if (intensitiesOfLeds1Through3 != null)
            {
            final int[] intensities = new int[hummingbirdProperties.getSimpleLedDeviceCount()];
            intensities[0] = state0.getLed0Intensity();
            for (int i = 0; i < Math.min(intensitiesOfLeds1Through3.length, intensities.length - 1); i++)
               {
               intensities[i + 1] = intensitiesOfLeds1Through3[i];
               }
            return intensities;
            }

         return null;
         }

      @Override
      public int[] getServoPositions()
         {
         return state1.getServoPositions();
         }

      @Override
      public int[] getMotorVelocities()
         {
         return state2.getMotorVelocities();
         }

      @Override
      public int[] getVibrationMotorSpeeds()
         {
         return state2.getVibrationMotorSpeeds();
         }

      @Override
      public int[] getAnalogInputValues()
         {
         return state3.getAnalogInputValues();
         }

      @Override
      public boolean isMotorPowerEnabled()
         {
         return state3.isMotorPowerEnabled();
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

         if (state0 != null ? !state0.equals(that.state0) : that.state0 != null)
            {
            return false;
            }
         if (state1 != null ? !state1.equals(that.state1) : that.state1 != null)
            {
            return false;
            }
         if (state2 != null ? !state2.equals(that.state2) : that.state2 != null)
            {
            return false;
            }
         if (state3 != null ? !state3.equals(that.state3) : that.state3 != null)
            {
            return false;
            }

         return true;
         }

      @Override
      public int hashCode()
         {
         int result = state0 != null ? state0.hashCode() : 0;
         result = 31 * result + (state1 != null ? state1.hashCode() : 0);
         result = 31 * result + (state2 != null ? state2.hashCode() : 0);
         result = 31 * result + (state3 != null ? state3.hashCode() : 0);
         return result;
         }

      @Override
      public String toString()
         {
         final StringBuilder s = new StringBuilder("HummingbirdState" + EOL);
         final Color[] orbs = getFullColorLEDs();
         final int[] leds = getLedIntensities();
         final int[] servos = getServoPositions();
         final int[] motors = getMotorVelocities();
         final int[] vibeMotors = getVibrationMotorSpeeds();
         final int[] analogInputs = getAnalogInputValues();
         for (int i = 0; i < orbs.length; i++)
            {
            s.append("   Orb ").append(i).append(":        (").append(orbs[i].getRed()).append(",").append(orbs[i].getGreen()).append(",").append(orbs[i].getBlue()).append(")").append(EOL);
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
         s.append("   Motor Power Enabled: ").append(isMotorPowerEnabled()).append(EOL);

         return s.toString();
         }
      }
   }
