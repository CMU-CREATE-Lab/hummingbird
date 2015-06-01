package edu.cmu.ri.createlab.hummingbird;

import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseHIDHummingbirdProperties extends BaseHummingbirdProperties
   {
   private static final Logger LOG = Logger.getLogger(BaseHIDHummingbirdProperties.class);

   private static final int ANALOG_INPUT_DEVICE_COUNT = 4;

   static HummingbirdHardwareType getHardwareTypeFromHardwareVersion(final HummingbirdVersionNumber hardwareVersionNumber)
      {
      final String hardwareMajorVersionStr = hardwareVersionNumber.getMajorVersion();
      try
         {
         final int hardwareMajorVersion = Integer.valueOf(hardwareMajorVersionStr);
         if (hardwareMajorVersion >= 3)
            {
            return HummingbirdHardwareType.DUO;
            }
         }
      catch (final NumberFormatException e)
         {
         LOG.error("NumberFormatException while trying to parse the hardware major version number [" + hardwareMajorVersionStr + "], defaulting to hardware type [" + HummingbirdHardwareType.HID + "]", e);
         }
      return HummingbirdHardwareType.HID;
      }

   private final String deviceCommonName;
   private final HummingbirdHardwareType hardwareType;

   BaseHIDHummingbirdProperties(final String deviceCommonName,
                                final HummingbirdHardwareType hardwareType)
      {
      // private to prevent instantiation
      this.deviceCommonName = deviceCommonName;
      this.hardwareType = hardwareType;
      }

   @Override
   public final String getDeviceCommonName()
      {
      return deviceCommonName;
      }

   @Override
   public final HummingbirdHardwareType getHardwareType()
      {
      return hardwareType;
      }

   /** Returns the number of analog inputs. */
   @Override
   public final int getAnalogInputDeviceCount()
      {
      return ANALOG_INPUT_DEVICE_COUNT;
      }
   }
