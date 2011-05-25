package edu.cmu.ri.createlab.hummingbird;

import java.awt.Color;
import java.util.SortedSet;
import java.util.concurrent.Semaphore;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;
import edu.cmu.ri.createlab.device.connectivity.BaseCreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionEventListener;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectionState;
import edu.cmu.ri.createlab.device.connectivity.CreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import edu.cmu.ri.createlab.terk.services.ServiceManager;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HummingbirdDevice implements Hummingbird
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdDevice.class);

   private final Semaphore connectionCompleteSemaphore = new Semaphore(1);
   private final CreateLabDeviceConnectivityManager connectivityManager = new HummingbirdConnectivityManager();
   private Hummingbird hummingbird = null;

   /**
    * Creates the {@link HummingbirdDevice} by attempting to connect to all available serial ports and connecting to the first
    * Hummingbird it finds.
    */
   public HummingbirdDevice()
      {
      this(null);
      }

   /**
    * Creates the {@link HummingbirdDevice} by attempting to connect to a Hummingbird on the given serial port(s).  Note that
    * if one ore more serial ports is already specified as a system property (e.g. by using the -D command line switch), 
    * then the serial port(s) specified in the argument to this constructor are appended to the port names specified in 
    * the system property, and the system property value is updated.
    */
   public HummingbirdDevice(final String userDefinedSerialPortNames)
      {
      if (userDefinedSerialPortNames != null && userDefinedSerialPortNames.trim().length() > 0)
         {
         LOG.debug("HummingbirdDevice.HummingbirdDevice(): processing user-defined serial port names...");

         // initialize the set of names to those specified in the argument to this constructor
         final StringBuilder serialPortNames = new StringBuilder(userDefinedSerialPortNames);

         // Now see if there are also serial ports already specified in the system property (e.g. via the -D command line switch).
         // If so, then those take precedence and the ones specified in the constructor argument will be appended
         final String serialPortNamesAlreadyInSystemProperty = System.getProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, null);
         if (serialPortNamesAlreadyInSystemProperty != null && serialPortNamesAlreadyInSystemProperty.trim().length() > 0)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("HummingbirdDevice.HummingbirdDevice(): Existing system property value = [" + serialPortNamesAlreadyInSystemProperty + "]");
               }
            serialPortNames.insert(0, System.getProperty("path.separator", ":"));
            serialPortNames.insert(0, serialPortNamesAlreadyInSystemProperty);
            }

         // now set the system property
         System.setProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, serialPortNames.toString());
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HummingbirdDevice.HummingbirdDevice(): System property [" + SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY + "] now set to [" + serialPortNames + "]");
            }
         }

      LOG.debug("Connecting to the Hummingbird...this may take a few seconds...");

      connectivityManager.addConnectionEventListener(
            new CreateLabDeviceConnectionEventListener()
            {
            public void handleConnectionStateChange(final CreateLabDeviceConnectionState oldState, final CreateLabDeviceConnectionState newState, final String portName)
               {
               if (CreateLabDeviceConnectionState.CONNECTED.equals(newState))
                  {
                  LOG.debug("HummingbirdDevice.handleConnectionStateChange(): Connected");

                  // connection complete, so release the lock
                  connectionCompleteSemaphore.release();
                  hummingbird = (HummingbirdProxy)connectivityManager.getCreateLabDeviceProxy();
                  }
               else if (CreateLabDeviceConnectionState.DISCONNECTED.equals(newState))
                  {
                  LOG.debug("HummingbirdDevice.handleConnectionStateChange(): Disconnected");
                  }
               else if (CreateLabDeviceConnectionState.SCANNING.equals(newState))
                  {
                  LOG.debug("HummingbirdDevice.handleConnectionStateChange(): Scanning...");
                  }
               else
                  {
                  LOG.error("HummingbirdDevice.handleConnectionStateChange(): Unexpected CreateLabDeviceConnectionState [" + newState + "]");
                  }
               }
            });

      LOG.trace("HummingbirdDevice.HummingbirdDevice(): 1) aquiring connection lock");

      // acquire the lock, which will be released once the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("HummingbirdDevice.HummingbirdDevice(): 2) connecting");

      // try to connect
      connectivityManager.scanAndConnect();

      LOG.trace("HummingbirdDevice.HummingbirdDevice(): 3) waiting for connection to complete");

      // try to acquire the lock again, which will block until the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("HummingbirdDevice.HummingbirdDevice(): 4) releasing lock");

      // we know the connection has completed (i.e. either connected or the connection failed) at this point, so just release the lock
      connectionCompleteSemaphore.release();

      LOG.trace("HummingbirdDevice.HummingbirdDevice(): 5) make sure we're actually connected");

      // if we're not connected, then throw an exception
      if (!CreateLabDeviceConnectionState.CONNECTED.equals(connectivityManager.getConnectionState()))
         {
         LOG.error("HummingbirdDevice.HummingbirdDevice(): Failed to connect to the Hummingbird!  Aborting.");
         System.exit(1);
         }

      LOG.trace("HummingbirdDevice.HummingbirdDevice(): 6) All done!");
      }

   @Override
   public ServiceManager getServiceManager()
      {
      return hummingbird.getServiceManager();
      }

   @Override
   public HummingbirdState getState()
      {
      return hummingbird.getState();
      }

   @Override
   public Integer getAnalogInputValue(final int analogInputPortId)
      {
      return hummingbird.getAnalogInputValue(analogInputPortId);
      }

   @Override
   public boolean setMotorVelocity(final int motorId, final int velocity)
      {
      return hummingbird.setMotorVelocity(motorId, velocity);
      }

   @Override
   public int[] setMotorVelocities(final boolean[] mask, final int[] velocities)
      {
      return hummingbird.setMotorVelocities(mask, velocities);
      }

   @Override
   public boolean setVibrationMotorSpeed(final int motorId, final int speed)
      {
      return hummingbird.setVibrationMotorSpeed(motorId, speed);
      }

   @Override
   public int[] setVibrationMotorSpeeds(final boolean[] mask, final int[] speeds)
      {
      return hummingbird.setVibrationMotorSpeeds(mask, speeds);
      }

   @Override
   public boolean setServoPosition(final int servoId, final int position)
      {
      return hummingbird.setServoPosition(servoId, position);
      }

   @Override
   public int[] setServoPositions(final boolean[] mask, final int[] positions)
      {
      return hummingbird.setServoPositions(mask, positions);
      }

   @Override
   public boolean setLED(final int ledId, final int intensity)
      {
      return hummingbird.setLED(ledId, intensity);
      }

   @Override
   public int[] setLEDs(final boolean[] mask, final int[] intensities)
      {
      return hummingbird.setLEDs(mask, intensities);
      }

   @Override
   public boolean setFullColorLED(final int ledId, final int red, final int green, final int blue)
      {
      return hummingbird.setFullColorLED(ledId, red, green, blue);
      }

   @Override
   public Color[] setFullColorLEDs(final boolean[] mask, final Color[] colors)
      {
      return hummingbird.setFullColorLEDs(mask, colors);
      }

   @Override
   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      hummingbird.playTone(frequency, amplitude, duration);
      }

   @Override
   public void playClip(final byte[] data)
      {
      hummingbird.playClip(data);
      }

   @Override
   public boolean emergencyStop()
      {
      return hummingbird.emergencyStop();
      }

   @Override
   public String getPortName()
      {
      return hummingbird.getPortName();
      }

   @Override
   public void disconnect()
      {
      hummingbird.disconnect();
      }

   @Override
   public void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      hummingbird.addCreateLabDevicePingFailureEventListener(listener);
      }

   @Override
   public void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      hummingbird.removeCreateLabDevicePingFailureEventListener(listener);
      }

   private static final class HummingbirdConnectivityManager extends BaseCreateLabDeviceConnectivityManager
      {
      protected CreateLabDeviceProxy scanForDeviceAndCreateProxy()
         {
         LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy()");

         // If the user specified one or more serial ports, then just start trying to connect to it/them.  Otherwise,
         // check each available serial port for the target serial device, and connect to the first one found.  This
         // makes connection time much faster for when you know the name of the serial port.
         final SortedSet<String> availableSerialPorts;
         if (SerialPortEnumerator.didUserDefineSetOfSerialPorts())
            {
            availableSerialPorts = SerialPortEnumerator.getSerialPorts();
            }
         else
            {
            availableSerialPorts = SerialPortEnumerator.getAvailableSerialPorts();
            }

         // try the serial ports
         if ((availableSerialPorts != null) && (!availableSerialPorts.isEmpty()))
            {
            for (final String portName : availableSerialPorts)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): checking serial port [" + portName + "]");
                  }

               final CreateLabDeviceProxy proxy = HummingbirdProxy.create(portName);

               if (proxy == null)
                  {
                  LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): connection failed, maybe it's not the device we're looking for?");
                  }
               else
                  {
                  LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): connection established, returning proxy!");
                  return proxy;
                  }
               }
            }
         else
            {
            LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): No available serial ports, returning null.");
            }

         return null;
         }
      }
   }
