package edu.cmu.ri.createlab.hummingbird;

import java.util.SortedSet;
import edu.cmu.ri.createlab.device.connectivity.BaseCreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdConnectivityManager extends BaseCreateLabDeviceConnectivityManager<Hummingbird>
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdConnectivityManager.class);
   private final boolean willCheckSerialPorts;

   HummingbirdConnectivityManager(final boolean willCheckSerialPorts)
      {
      this.willCheckSerialPorts = willCheckSerialPorts;
      }

   @Override
   protected Hummingbird scanForDeviceAndCreateProxy()
      {
      LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy()");

      // first try to connect to an HID hummingbird...
      Hummingbird hummingbird = HummingbirdFactory.createHidHummingbird();

      // if we're checking serial ports, and if HIDHummingbirdProxy returned null, then it couldn't find an HID hummingbird, so try serial...
      if (willCheckSerialPorts && hummingbird == null)
         {
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

               hummingbird = HummingbirdFactory.createSerialHummingbird(portName);

               if (hummingbird == null)
                  {
                  LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): connection failed, maybe it's not the device we're looking for?");
                  }
               else
                  {
                  LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): connection established, returning hummingbird!");
                  return hummingbird;
                  }
               }
            }
         else
            {
            LOG.debug("HummingbirdConnectivityManager.scanForDeviceAndCreateProxy(): No available serial ports, returning null.");
            }
         }

      return hummingbird;
      }
   }
