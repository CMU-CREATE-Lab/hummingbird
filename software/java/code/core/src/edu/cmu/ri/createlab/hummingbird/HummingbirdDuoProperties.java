package edu.cmu.ri.createlab.hummingbird;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdDuoProperties extends BaseHIDHummingbirdProperties
   {
   private static final HummingbirdProperties INSTANCE = new HummingbirdDuoProperties();

   static HummingbirdProperties getInstance()
      {
      return INSTANCE;
      }

   /**
    * The maximum supported safe speed--the Duo uses a 3V line for the vibration motors, so it's safe to go all the way
    * to 255 now
    */
   private static final int VIBRATION_MOTOR_DEVICE_MAX_SAFE_SPEED = 255;

   // private to prevent instantiation
   private HummingbirdDuoProperties()
      {
      super("Hummingbird Duo", HummingbirdHardwareType.DUO);
      }

   @Override
   public int getVibrationMotorDeviceMaxSafeSpeed()
      {
      return VIBRATION_MOTOR_DEVICE_MAX_SAFE_SPEED;
      }
   }
