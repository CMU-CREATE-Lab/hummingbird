package edu.cmu.ri.createlab.hummingbird;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.cmu.ri.createlab.device.connectivity.ConnectionException;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>HummingbirdFactory</code> provides methods for creating a {@link Hummingbird} instance.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HummingbirdFactory
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdFactory.class);

   private static final String PATH_SEPARATOR = System.getProperty("path.separator", ":");

   /**
    * Creates the {@link Hummingbird} by repeatedly attempting to connect to one as a USB HID device or, if that fails,
    * by checking all available serial ports and connecting to the first Hummingbird it finds.  This method will
    * repeatedly retry and will only return when either a connection is established, or an unrecoverable failure is
    * encountered in which case <code>null</code> is returned.
    */
   public static Hummingbird create()
      {
      return create(null);
      }

   /**
    * Creates the {@link Hummingbird} by repeatedly attempting to connect to a Hummingbird as a USB HID device or, if
    * that fails, on the given serial port(s). Note that if one ore more serial ports is already specified as a system
    * property (e.g. by using the -D command line switch), then the serial port(s) specified in the argument to this
    * constructor are appended to the port names specified in the system property, and the system property value is
    * updated.  This method will repeatedly retry and will only return when either a connection is established, or an
    * unrecoverable failure is encountered in which case <code>null</code> is returned.
    */
   public static Hummingbird create(final List<String> userDefinedSerialPortNames) throws ConnectionException
      {
      if (userDefinedSerialPortNames != null && userDefinedSerialPortNames.size() > 0)
         {
         final Set<String> portNames = new HashSet<String>();

         // add all the ports currently defined in the system property to the set..
         final String currentPortNamesStr = System.getProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, "").trim();
         if (currentPortNamesStr.length() > 0)
            {
            final String[] currentPortNames = currentPortNamesStr.split(PATH_SEPARATOR);
            Collections.addAll(portNames, currentPortNames);
            }

         // now add the new user-defined ones
         for (final String portName : userDefinedSerialPortNames)
            {
            portNames.add(portName);
            }

         // now that we have a set of all the existing ports and the new ones defined by the user, update the system property...
         if (portNames.size() > 0)
            {
            // first build the string value
            String[] portNameArray = new String[portNames.size()];
            portNameArray = portNames.toArray(portNameArray);
            final StringBuilder newSystemPropertyValue = new StringBuilder(portNameArray[0]);
            for (int i = 1; i < portNameArray.length; i++)
               {
               newSystemPropertyValue.append(PATH_SEPARATOR).append(portNameArray[i]);
               }

            // now set the system property
            System.setProperty(SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY, newSystemPropertyValue.toString());
            if (LOG.isDebugEnabled())
               {
               LOG.debug("HummingbirdFactory.create(): System property [" + SerialPortEnumerator.SERIAL_PORTS_SYSTEM_PROPERTY_KEY + "] now set to [" + newSystemPropertyValue + "]");
               }
            }
         }

      try
         {
         LOG.debug("Connecting to the Hummingbird...");
         return new HummingbirdConnectivityManager().connect();
         }
      catch (ConnectionException e)
         {
         LOG.error("ConnectionException while trying to connect to the Hummingbird", e);
         }

      return null;
      }

   /**
    * Tries to create a <code>Hummingbird</code> by connecting to a hummingbird on the serial port specified by
    * the given <code>serialPortName</code>.  Returns <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   public static Hummingbird createSerialHummingbird(final String serialPortName)
      {
      return SerialHummingbirdProxy.create(serialPortName);
      }

   /**
    * Tries to create a <code>Hummingbird</code> by connecting to an HID hummingbird.  Returns <code>null</code> if the
    * connection could not be established.
    */
   public static Hummingbird createHidHummingbird()
      {
      return HIDHummingbirdProxy.create();
      }

   private HummingbirdFactory()
      {
      // private to prevent instantiation
      }
   }
