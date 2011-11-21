package edu.cmu.ri.createlab.hummingbird;

import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDHummingbirdProperties extends BaseHummingbirdProperties
   {
   private static final HummingbirdProperties INSTANCE = new HIDHummingbirdProperties();

   private static final String DEVICE_COMMON_NAME = "HID Hummingbird";

   private static final String HARDWARE_TYPE = DEVICE_COMMON_NAME;

   private static final int ANALOG_INPUT_DEVICE_COUNT = 4;

   static final class UsbHidConfiguration
      {
      public static final short USB_VENDOR_ID = 0x2354;
      public static final short USB_PRODUCT_ID = 0x2222;

      private static final int INPUT_REPORT_LENGTH_IN_BYTES = 9;  // count includes the report ID
      private static final int OUTPUT_REPORT_LENGTH_IN_BYTES = 9; // count includes the report ID

      public static final HIDDeviceDescriptor HUMMINGBIRD_HID_DEVICE_DESCRIPTOR = new HIDDeviceDescriptor(USB_VENDOR_ID,
                                                                                                          USB_PRODUCT_ID,
                                                                                                          INPUT_REPORT_LENGTH_IN_BYTES,
                                                                                                          OUTPUT_REPORT_LENGTH_IN_BYTES,
                                                                                                          DEVICE_COMMON_NAME);

      private UsbHidConfiguration()
         {
         // private to prevent instantiation
         }
      }

   static HummingbirdProperties getInstance()
      {
      return INSTANCE;
      }

   private HIDHummingbirdProperties()
      {
      // private to prevent instantiation
      }

   @Override
   public String getDeviceCommonName()
      {
      return DEVICE_COMMON_NAME;
      }

   @Override
   public String getHardwareType()
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
