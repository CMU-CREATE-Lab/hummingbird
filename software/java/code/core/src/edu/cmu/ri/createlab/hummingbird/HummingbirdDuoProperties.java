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

   /** Max voltage for the motor power port */
   private static final double MAX_MOTOR_POWER_PORT_VOLTAGE = 10.0;

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

   @Override
   public double getMaxMotorPowerPortVoltage()
      {
      return MAX_MOTOR_POWER_PORT_VOLTAGE;
      }
   }
