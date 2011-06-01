package edu.cmu.ri.createlab.hummingbird;

import java.util.SortedSet;
import edu.cmu.ri.createlab.device.connectivity.BaseCreateLabDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdConnectivityManager extends BaseCreateLabDeviceConnectivityManager<HummingbirdProxy>
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdConnectivityManager.class);

   @Override
   protected HummingbirdProxy scanForDeviceAndCreateProxy()
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

            final HummingbirdProxy proxy = HummingbirdProxy.create(portName);

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
