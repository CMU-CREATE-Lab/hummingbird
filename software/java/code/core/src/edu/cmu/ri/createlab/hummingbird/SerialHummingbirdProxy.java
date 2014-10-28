package edu.cmu.ri.createlab.hummingbird;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.hummingbird.commands.serial.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.GetAnalogInputCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.GetStateCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.HandshakeCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.LEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.MotorCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.ServoCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.serial.VibrationMotorCommandStrategy;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.SerialDeviceReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SerialHummingbirdProxy extends BaseHummingbirdProxy
   {
   private static final Logger LOG = Logger.getLogger(SerialHummingbirdProxy.class);

   /**
    * Tries to create a <code>Hummingbird</code> by connecting to a hummingbird on the serial port specified by
    * the given <code>serialPortName</code>.  Returns <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   static Hummingbird create(final String serialPortName)
      {
      // a little error checking...
      if (serialPortName == null)
         {
         throw new IllegalArgumentException("The serial port name may not be null");
         }

      // create the serial port configuration
      final SerialIOConfiguration config = new SerialIOConfiguration(serialPortName,
                                                                     SerialHummingbirdProperties.SerialConfiguration.BAUD_RATE,
                                                                     SerialHummingbirdProperties.SerialConfiguration.CHARACTER_SIZE,
                                                                     SerialHummingbirdProperties.SerialConfiguration.PARITY,
                                                                     SerialHummingbirdProperties.SerialConfiguration.STOP_BITS,
                                                                     SerialHummingbirdProperties.SerialConfiguration.FLOW_CONTROL);

      try
         {
         // create the serial port command queue
         final SerialDeviceCommandExecutionQueue commandQueue = SerialDeviceCommandExecutionQueue.create(SerialHummingbirdProperties.getInstance().getDeviceCommonName(), config);

         // see whether its creation was successful
         if (commandQueue == null)
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("Failed to open serial port '" + serialPortName + "'");
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("Serial port '" + serialPortName + "' opened.");
               }

            // now try to do the handshake with the hummingbird to establish communication
            final boolean wasHandshakeSuccessful = commandQueue.executeAndReturnStatus(new HandshakeCommandStrategy());

            // see if the handshake was a success
            if (wasHandshakeSuccessful)
               {
               LOG.info("Hummingbird handshake successful!");

               // now create and return the proxy
               return new SerialHummingbirdProxy(commandQueue, serialPortName);
               }
            else
               {
               LOG.error("Failed to handshake with hummingbird");
               }

            // the handshake failed, so shutdown the command queue to release the serial port
            commandQueue.shutdown();
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to create the SerialHummingbirdProxy", e);
         }

      return null;
      }

   private final HummingbirdProperties hummingbirdProperties = SerialHummingbirdProperties.getInstance();

   private final SerialDeviceCommandExecutionQueue commandQueue;
   private final String serialPortName;

   private final CreateLabSerialDeviceNoReturnValueCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final Map<Integer, GetAnalogInputCommandStrategy> analogInputCommandStategyMap = new HashMap<Integer, GetAnalogInputCommandStrategy>();
   private final GetStateCommandStrategy getStateCommandStrategy = new GetStateCommandStrategy(hummingbirdProperties);
   private final EmergencyStopCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();

   private final SerialDeviceNoReturnValueCommandExecutor noReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<Integer> integerReturnValueCommandExecutor;
   private final SerialDeviceReturnValueCommandExecutor<HummingbirdState> hummingbirdStateReturnValueCommandExecutor;

   private SerialHummingbirdProxy(final SerialDeviceCommandExecutionQueue commandQueue, final String serialPortName)
      {
      this.commandQueue = commandQueue;
      this.serialPortName = serialPortName;

      noReturnValueCommandExecutor = new SerialDeviceNoReturnValueCommandExecutor(commandQueue, this);
      integerReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<Integer>(commandQueue, this);
      hummingbirdStateReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<HummingbirdState>(commandQueue, this);

      // initialize the analog input command strategy map
      for (int i = 0; i < hummingbirdProperties.getAnalogInputDeviceCount(); i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i, hummingbirdProperties));
         }
      }

   @Override
   public String getPortName()
      {
      return serialPortName;
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
      return SerialHummingbirdProperties.HARDWARE_VERSION;
      }

   @Override
   public HummingbirdVersionNumber getFirmwareVersion()
      {
      return SerialHummingbirdProperties.FIRMWARE_VERSION;
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
   public int[] getAnalogInputValues()
      {
      final HummingbirdState state = getState();
      if (state != null)
         {
         return state.getAnalogInputValues();
         }
      return null;
      }

   @Override
   public boolean isMotorPowerEnabled()
      {
      return true; // always true for serial hummingbirds
      }

   @Override
   public boolean setMotorVelocity(final int motorId, final int velocity)
      {
      return noReturnValueCommandExecutor.execute(new MotorCommandStrategy(motorId, velocity, hummingbirdProperties));
      }

   @Override
   public int[] setMotorVelocities(final boolean[] mask, final int[] velocities)
      {
      if (noReturnValueCommandExecutor.execute(new MotorCommandStrategy(mask, velocities, hummingbirdProperties)))
         {
         final HummingbirdState state = getState();
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
      if (noReturnValueCommandExecutor.execute(new VibrationMotorCommandStrategy(mask, speeds, hummingbirdProperties)))
         {
         final HummingbirdState state = getState();
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
      if (noReturnValueCommandExecutor.execute(new ServoCommandStrategy(mask, positions, hummingbirdProperties)))
         {
         final HummingbirdState state = getState();
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
      if (noReturnValueCommandExecutor.execute(new LEDCommandStrategy(mask, intensities, hummingbirdProperties)))
         {
         final HummingbirdState state = getState();
         if (state != null)
            {
            return state.getLedIntensities();
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
      if (noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(mask, colors, hummingbirdProperties)))
         {
         final HummingbirdState state = getState();
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
   }
