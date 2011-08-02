package edu.cmu.ri.createlab.hummingbird;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.hummingbird.commands.hid.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetAnalogInputCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.hid.GetStateCommandStrategy;
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
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDHummingbirdProxy extends BaseHummingbirdProxy
   {
   private static final Logger LOG = Logger.getLogger(HIDHummingbirdProxy.class);

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
                      HummingbirdConstants.UsbHid.HUMMINGBIRD_HID_DEVICE_DESCRIPTOR.getVendorIdAsHexString() +
                      "] and product ID [" +
                      HummingbirdConstants.UsbHid.HUMMINGBIRD_HID_DEVICE_DESCRIPTOR.getProductIdAsHexString() +
                      "]");
            }
         final HIDDevice hidDevice = HIDDeviceFactory.create(HummingbirdConstants.UsbHid.HUMMINGBIRD_HID_DEVICE_DESCRIPTOR);

         LOG.debug("HIDHummingbirdProxy.create(): attempting connection...");
         hidDevice.connectExclusively();

         // create the HID device command execution queue (which will attempt to connect to the device)
         final HIDCommandExecutionQueue commandQueue = new HIDCommandExecutionQueue(hidDevice);
         if (commandQueue != null)
            {
            // create the HIDHummingbirdProxy
            final HIDHummingbirdProxy finchController = new HIDHummingbirdProxy(commandQueue, hidDevice);

            // call the emergency stop command immediately, to make sure the LED and motors are turned off.
            finchController.emergencyStop();

            return finchController;
            }
         }
      catch (NotImplementedException e)
         {
         LOG.error("NotImplementedException caught while trying to create the HIDCommandExecutionQueue", e);
         System.exit(1);
         }
      catch (HIDConnectionException e)
         {
         LOG.error("HIDConnectionException while trying to connect to the Hummingbird, returning null", e);
         }
      catch (HIDDeviceNotFoundException e)
         {
         LOG.error("HIDDeviceNotFoundException while trying to connect to the Hummingbird, returning null");
         }
      return null;
      }

   private final HIDCommandExecutionQueue commandQueue;
   private final HIDDevice hidDevice;

   private final CreateLabHIDCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final Map<Integer, GetAnalogInputCommandStrategy> analogInputCommandStategyMap = new HashMap<Integer, GetAnalogInputCommandStrategy>();
   private final GetStateCommandStrategy getStateCommandStrategy = new GetStateCommandStrategy();
   private final EmergencyStopCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();

   private final HIDDeviceNoReturnValueCommandExecutor noReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor;
   private final HIDDeviceReturnValueCommandExecutor<HummingbirdState> hummingbirdStateReturnValueCommandExecutor;

   private HIDHummingbirdProxy(final HIDCommandExecutionQueue commandQueue, final HIDDevice hidDevice)
      {
      this.commandQueue = commandQueue;
      this.hidDevice = hidDevice;

      noReturnValueCommandExecutor = new HIDDeviceNoReturnValueCommandExecutor(commandQueue, this);
      integerReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<Integer>(commandQueue, this);
      hummingbirdStateReturnValueCommandExecutor = new HIDDeviceReturnValueCommandExecutor<HummingbirdState>(commandQueue, this);

      // initialize the analog input command strategy map
      for (int i = 0; i < HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT; i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i));
         }
      }

   @Override
   public String getPortName()
      {
      return hidDevice.getDeviceFilename();
      }

   @Override
   public HummingbirdState getState()
      {
      return hummingbirdStateReturnValueCommandExecutor.execute(getStateCommandStrategy);
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
   public boolean setMotorVelocity(final int motorId, final int velocity)
      {
      return noReturnValueCommandExecutor.execute(new MotorCommandStrategy(motorId, velocity));
      }

   @Override
   protected boolean setMotorVelocitiesAndReturnStatus(final boolean[] mask, final int[] velocities)
      {
      return noReturnValueCommandExecutor.execute(new MotorCommandStrategy(mask, velocities));
      }

   @Override
   public boolean setVibrationMotorSpeed(final int motorId, final int speed)
      {
      return noReturnValueCommandExecutor.execute(new VibrationMotorCommandStrategy(motorId, speed));
      }

   @Override
   protected boolean setVibrationMotorSpeedsAndReturnStatus(final boolean[] mask, final int[] speeds)
      {
      return noReturnValueCommandExecutor.execute(new VibrationMotorCommandStrategy(mask, speeds));
      }

   @Override
   public boolean setServoPosition(final int servoId, final int position)
      {
      return noReturnValueCommandExecutor.execute(new ServoCommandStrategy(servoId, position));
      }

   @Override
   protected boolean setServoPositionsAndReturnStatus(final boolean[] mask, final int[] positions)
      {
      return noReturnValueCommandExecutor.execute(new ServoCommandStrategy(mask, positions));
      }

   @Override
   public boolean setLED(final int ledId, final int intensity)
      {
      return noReturnValueCommandExecutor.execute(new LEDCommandStrategy(ledId, intensity));
      }

   @Override
   protected boolean setLEDsAndReturnStatus(final boolean[] mask, final int[] intensities)
      {
      return noReturnValueCommandExecutor.execute(new LEDCommandStrategy(mask, intensities));
      }

   @Override
   public boolean setFullColorLED(final int ledId, final int red, final int green, final int blue)
      {
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(ledId, red, green, blue));
      }

   @Override
   protected boolean setFullColorLEDsAndReturnStatus(final boolean[] mask, final Color[] colors)
      {
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(mask, colors));
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
   }
