package edu.cmu.ri.createlab.hummingbird;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDHummingbirdProperties extends BaseHIDHummingbirdProperties
   {
   private static final HummingbirdProperties INSTANCE = new HIDHummingbirdProperties();

   /** Max voltage for the motor power port */
   private static final double MAX_MOTOR_POWER_PORT_VOLTAGE = 5.0;

   static HummingbirdProperties getInstance()
      {
      return INSTANCE;
      }

   // private to prevent instantiation
   private HIDHummingbirdProperties()
      {
      super("HID Hummingbird", HummingbirdHardwareType.HID);
      }

   @Override
   public double getMaxMotorPowerPortVoltage()
      {
      return MAX_MOTOR_POWER_PORT_VOLTAGE;
      }
   }
