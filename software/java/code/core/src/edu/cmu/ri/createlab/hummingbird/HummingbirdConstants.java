package edu.cmu.ri.createlab.hummingbird;

import edu.cmu.ri.createlab.usb.hid.HIDDeviceDescriptor;

/**
 * <p>
 * <code>HummingbirdConstants</code> defines various constants for hummingbirds
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HummingbirdConstants
   {
   public static final String DEVICE_COMMON_NAME = "Hummingbird";

   /** The size, in bytes, of the state array */
   public static final int SIZE_IN_BYTES_OF_STATE_ARRAY = 22;

   /** The number of analog inputs */
   public static final int ANALOG_INPUT_DEVICE_COUNT = 2;

   /** The minimum value returned by analog inputs */
   public static final int ANALOG_INPUT_MIN_VALUE = 0;

   /** The maximum value returned by analog inputs */
   public static final int ANALOG_INPUT_MAX_VALUE = 255;

   /** The number of audio outputs */
   public static final int AUDIO_DEVICE_COUNT = 1;

   /** The minimum supported tone frequency */
   public static final int AUDIO_DEVICE_MIN_AMPLITUDE = 0;

   /** The maximum supported tone frequency */
   public static final int AUDIO_DEVICE_MAX_AMPLITUDE = 10;

   /** The minimum supported tone duration */
   public static final int AUDIO_DEVICE_MIN_DURATION = 0;

   /** The maximum supported tone duration */
   public static final int AUDIO_DEVICE_MAX_DURATION = Integer.MAX_VALUE;

   /** The minimum supported tone frequency */
   public static final int AUDIO_DEVICE_MIN_FREQUENCY = 0;

   /** The maximum supported tone frequency */
   public static final int AUDIO_DEVICE_MAX_FREQUENCY = Integer.MAX_VALUE;

   /** The number of full-color LEDS */
   public static final int FULL_COLOR_LED_DEVICE_COUNT = 2;

   /** The minimum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MAX_INTENSITY = 255;

   /** The number of hummingbirds */
   public static final int HUMMINGBIRD_DEVICE_COUNT = 1;

   /** The number of motors */
   public static final int MOTOR_DEVICE_COUNT = 2;

   /** The minimum supported velocity */
   public static final int MOTOR_DEVICE_MIN_VELOCITY = -255;

   /** The maximum supported velocity */
   public static final int MOTOR_DEVICE_MAX_VELOCITY = 255;

   /** The number of LEDs */
   public static final int SIMPLE_LED_DEVICE_COUNT = 4;

   /** The minimum supported LED intensity */
   public static final int SIMPLE_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported LED intensity */
   public static final int SIMPLE_LED_DEVICE_MAX_INTENSITY = 255;

   /** The number of servos */
   public static final int SIMPLE_SERVO_DEVICE_COUNT = 4;

   /** The minimum supported servo position */
   public static final int SIMPLE_SERVO_DEVICE_MIN_POSITION = 0;

   /** The maximum supported servo position */
   public static final int SIMPLE_SERVO_DEVICE_MAX_POSITION = 255;

   /** The number of vibration motors */
   public static final int VIBRATION_MOTOR_DEVICE_COUNT = 2;

   /** The minimum supported speed */
   public static final int VIBRATION_MOTOR_DEVICE_MIN_SPEED = 0;

   /** The maximum supported velocity */
   public static final int VIBRATION_MOTOR_DEVICE_MAX_SPEED = 255;

   public static final String HARDWARE_TYPE = "hummingbird";
   public static final String HARDWARE_VERSION = "1.0";

   public static final class UsbHid
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
      }

   private HummingbirdConstants()
      {
      // private to prevent instantiation
      }
   }
