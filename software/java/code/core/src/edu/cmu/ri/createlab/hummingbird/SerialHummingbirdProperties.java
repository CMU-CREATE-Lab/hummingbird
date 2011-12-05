package edu.cmu.ri.createlab.hummingbird;

import edu.cmu.ri.createlab.serial.config.BaudRate;
import edu.cmu.ri.createlab.serial.config.CharacterSize;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.StopBits;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SerialHummingbirdProperties extends BaseHummingbirdProperties
   {
   private static final HummingbirdProperties INSTANCE = new SerialHummingbirdProperties();

   private static final String DEVICE_COMMON_NAME = "Serial Hummingbird";

   private static final HummingbirdHardwareType HARDWARE_TYPE = HummingbirdHardwareType.SERIAL;

   private static final int ANALOG_INPUT_DEVICE_COUNT = 2;

   static final HummingbirdVersionNumber HARDWARE_VERSION = new HummingbirdVersionNumber(1, 0);
   static final HummingbirdVersionNumber FIRMWARE_VERSION = new HummingbirdVersionNumber(1, 0);

   static final class SerialConfiguration
      {
      public static final BaudRate BAUD_RATE = BaudRate.BAUD_19200;
      public static final CharacterSize CHARACTER_SIZE = CharacterSize.EIGHT;
      public static final Parity PARITY = Parity.NONE;
      public static final StopBits STOP_BITS = StopBits.ONE;
      public static final FlowControl FLOW_CONTROL = FlowControl.NONE;

      private SerialConfiguration()
         {
         // private to prevent instantiation
         }
      }

   static HummingbirdProperties getInstance()
      {
      return INSTANCE;
      }

   private SerialHummingbirdProperties()
      {
      // private to prevent instantiation
      }

   @Override
   public String getDeviceCommonName()
      {
      return DEVICE_COMMON_NAME;
      }

   @Override
   public HummingbirdHardwareType getHardwareType()
      {
      return HARDWARE_TYPE;
      }

   /** Returns the number of analog inputs. */
   @Override
   public int getAnalogInputDeviceCount()
      {
      return ANALOG_INPUT_DEVICE_COUNT;
      }
   }
