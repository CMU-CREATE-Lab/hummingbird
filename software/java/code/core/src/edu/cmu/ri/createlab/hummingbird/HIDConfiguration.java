package edu.cmu.ri.createlab.hummingbird;

import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;

/**
 * <p>
 * <code>HIDConfiguration</code> defines the various propertis for a USB HID Hummingbird.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDConfiguration
   {
   public static final short USB_VENDOR_ID = 0x2354;
   public static final short USB_PRODUCT_ID = 0x2222;

   private static final int INPUT_REPORT_LENGTH_IN_BYTES = 9;  // count includes the report ID
   private static final int OUTPUT_REPORT_LENGTH_IN_BYTES = 9; // count includes the report ID

   public static final HIDDeviceDescriptor HUMMINGBIRD_HID_DEVICE_DESCRIPTOR = new HIDDeviceDescriptor(USB_VENDOR_ID,
                                                                                                       USB_PRODUCT_ID,
                                                                                                       INPUT_REPORT_LENGTH_IN_BYTES,
                                                                                                       OUTPUT_REPORT_LENGTH_IN_BYTES,
                                                                                                       "Hummingbird with USB HID support");

   private HIDConfiguration()
      {
      // private to prevent instantiation
      }
   }
