package edu.cmu.ri.createlab.hummingbird;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.audio.AudioHelper;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.hummingbird.commands.DisconnectCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.EmergencyStopCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.FullColorLEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.GetAnalogInputCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.GetStateCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.HandshakeCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.LEDCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.MotorCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.ServoCommandStrategy;
import edu.cmu.ri.createlab.hummingbird.commands.VibrationMotorCommandStrategy;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;
import edu.cmu.ri.createlab.serial.SerialDeviceNoReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.SerialDeviceReturnValueCommandExecutor;
import edu.cmu.ri.createlab.serial.config.BaudRate;
import edu.cmu.ri.createlab.serial.config.CharacterSize;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.serial.config.StopBits;
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdProxy implements Hummingbird
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdProxy.class);

   public static final String APPLICATION_NAME = "Hummingbird";
   private static final int DELAY_IN_SECONDS_BETWEEN_PEER_PINGS = 2;

   /**
    * Tries to create a <code>Hummingbird</code> by connecting to a hummingbird on the serial port specified by
    * the given <code>serialPortName</code>.  Returns <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   public static Hummingbird create(final String serialPortName)
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
         final SerialDeviceCommandExecutionQueue commandQueue = SerialDeviceCommandExecutionQueue.create(APPLICATION_NAME, config);

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
               return new HummingbirdProxy(commandQueue, serialPortName);
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
         LOG.error("Exception while trying to create the HummingbirdProxy", e);
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

   private final Pinger pinger = new Pinger();
   private final ScheduledExecutorService pingExecutorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("HummingbirdProxy.pingExecutorService"));
   private final ScheduledFuture<?> pingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   private HummingbirdProxy(final SerialDeviceCommandExecutionQueue commandQueue, final String serialPortName)
      {
      this.commandQueue = commandQueue;
      this.serialPortName = serialPortName;

      final CommandExecutionFailureHandler commandExecutionFailureHandler =
            new CommandExecutionFailureHandler()
            {
            public void handleExecutionFailure()
               {
               pinger.forceFailure();
               }
            };
      noReturnValueCommandExecutor = new SerialDeviceNoReturnValueCommandExecutor(commandQueue, commandExecutionFailureHandler);
      integerReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<Integer>(commandQueue, commandExecutionFailureHandler);
      hummingbirdStateReturnValueCommandExecutor = new SerialDeviceReturnValueCommandExecutor<HummingbirdState>(commandQueue, commandExecutionFailureHandler);

      // initialize the analog input command strategy map
      for (int i = 0; i < HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT; i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i));
         }

      // schedule periodic pings
      pingScheduledFuture = pingExecutorService.scheduleAtFixedRate(pinger,
                                                                    DELAY_IN_SECONDS_BETWEEN_PEER_PINGS, // delay before first ping
                                                                    DELAY_IN_SECONDS_BETWEEN_PEER_PINGS, // delay between pings
                                                                    TimeUnit.SECONDS);
      }

   @Override
   public String getPortName()
      {
      return serialPortName;
      }

   @Override
   public void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.add(listener);
         }
      }

   @Override
   public void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.remove(listener);
         }
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
   public int[] setMotorVelocities(final boolean[] mask, final int[] velocities)
      {
      if (noReturnValueCommandExecutor.execute(new MotorCommandStrategy(mask, velocities)))
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
      return noReturnValueCommandExecutor.execute(new VibrationMotorCommandStrategy(motorId, speed));
      }

   @Override
   public int[] setVibrationMotorSpeeds(final boolean[] mask, final int[] speeds)
      {
      if (noReturnValueCommandExecutor.execute(new VibrationMotorCommandStrategy(mask, speeds)))
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
      return noReturnValueCommandExecutor.execute(new ServoCommandStrategy(servoId, position));
      }

   @Override
   public int[] setServoPositions(final boolean[] mask, final int[] positions)
      {
      if (noReturnValueCommandExecutor.execute(new ServoCommandStrategy(mask, positions)))
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
      return noReturnValueCommandExecutor.execute(new LEDCommandStrategy(ledId, intensity));
      }

   @Override
   public int[] setLEDs(final boolean[] mask, final int[] intensities)
      {
      if (noReturnValueCommandExecutor.execute(new LEDCommandStrategy(mask, intensities)))
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
      return noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(ledId, red, green, blue));
      }

   @Override
   public Color[] setFullColorLEDs(final boolean[] mask, final Color[] colors)
      {
      if (noReturnValueCommandExecutor.execute(new FullColorLEDCommandStrategy(mask, colors)))
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
   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      AudioHelper.playTone(frequency, amplitude, duration);
      }

   @Override
   public void playClip(final byte[] data)
      {
      AudioHelper.playClip(data);
      }

   @Override
   public boolean emergencyStop()
      {
      return noReturnValueCommandExecutor.execute(emergencyStopCommandStrategy);
      }

   @Override
   public void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("HummingbirdProxy.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // turn off the pinger
      try
         {
         pingScheduledFuture.cancel(false);
         pingExecutorService.shutdownNow();
         LOG.debug("HummingbirdProxy.disconnect(): Successfully shut down the Hummingbird pinger.");
         }
      catch (Exception e)
         {
         LOG.error("HummingbirdProxy.disconnect(): Exception caught while trying to shut down pinger", e);
         }

      // optionally send goodbye command to the Hummingbird
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("HummingbirdProxy.disconnect(): Now attempting to send the disconnect command to the Hummingbird");
         try
            {
            if (commandQueue.executeAndReturnStatus(disconnectCommandStrategy))
               {
               LOG.debug("HummingbirdProxy.disconnect(): Successfully disconnected from the Hummingbird.");
               }
            else
               {
               LOG.error("HummingbirdProxy.disconnect(): Failed to disconnect from the Hummingbird.");
               }
            }
         catch (Exception e)
            {
            LOG.error("Exception caught while trying to execute the disconnect", e);
            }
         }

      // shut down the command queue, which closes the serial port
      try
         {
         LOG.debug("HummingbirdProxy.disconnect(): shutting down the SerialDeviceCommandExecutionQueue...");
         commandQueue.shutdown();
         LOG.debug("HummingbirdProxy.disconnect(): done shutting down the SerialDeviceCommandExecutionQueue");
         }
      catch (Exception e)
         {
         LOG.error("HummingbirdProxy.disconnect(): Exception while trying to shut down the SerialDeviceCommandExecutionQueue", e);
         }
      }

   private class Pinger implements Runnable
      {
      public void run()
         {
         try
            {
            // ping the device (all we do here is request an analog input and make sure it was successful)
            final SerialDeviceCommandResponse response = commandQueue.execute(analogInputCommandStategyMap.get(0));
            final boolean pingSuccessful = (response != null) && response.wasSuccessful();

            // if the ping failed, then we know we have a problem, so disconnect (which
            // probably won't work) and then notify the listeners
            if (!pingSuccessful)
               {
               handlePingFailure();
               }
            }
         catch (Exception e)
            {
            LOG.error("HummingbirdProxy$Pinger.run(): Exception caught while executing the pinger", e);
            }
         }

      private void handlePingFailure()
         {
         try
            {
            LOG.debug("HummingbirdProxy$Pinger.handlePingFailure(): Peer ping failed.  Attempting to disconnect...");
            disconnect(false);
            LOG.debug("HummingbirdProxy$Pinger.handlePingFailure(): Done disconnecting from the Hummingbird");
            }
         catch (Exception e)
            {
            LOG.error("HummingbirdProxy$Pinger.handlePingFailure(): Exeption caught while trying to disconnect from the Hummingbird", e);
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("HummingbirdProxy$Pinger.handlePingFailure(): Notifying " + createLabDevicePingFailureEventListeners.size() + " listeners of ping failure...");
            }
         for (final CreateLabDevicePingFailureEventListener listener : createLabDevicePingFailureEventListeners)
            {
            try
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("   HummingbirdProxy$Pinger.handlePingFailure(): Notifying " + listener);
                  }
               listener.handlePingFailureEvent();
               }
            catch (Exception e)
               {
               LOG.error("HummingbirdProxy$Pinger.handlePingFailure(): Exeption caught while notifying SerialDevicePingFailureEventListener", e);
               }
            }
         }

      private void forceFailure()
         {
         handlePingFailure();
         }
      }
   }
