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
import edu.cmu.ri.createlab.serial.config.BaudRate;
import edu.cmu.ri.createlab.serial.config.CharacterSize;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.serial.config.StopBits;
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
                                                                     BaudRate.BAUD_19200,
                                                                     CharacterSize.EIGHT,
                                                                     Parity.NONE,
                                                                     StopBits.ONE,
                                                                     FlowControl.NONE);

      try
         {
         // create the serial port command queue
         final SerialDeviceCommandExecutionQueue commandQueue = SerialDeviceCommandExecutionQueue.create(HummingbirdConstants.DEVICE_COMMON_NAME, config);

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

   private final SerialDeviceCommandExecutionQueue commandQueue;
   private final String serialPortName;

   private final CreateLabSerialDeviceNoReturnValueCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final Map<Integer, GetAnalogInputCommandStrategy> analogInputCommandStategyMap = new HashMap<Integer, GetAnalogInputCommandStrategy>();
   private final GetStateCommandStrategy getStateCommandStrategy = new GetStateCommandStrategy();
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
      for (int i = 0; i < HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT; i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i));
         }
      }

   @Override
   public String getPortName()
      {
      return serialPortName;
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
