package edu.cmu.ri.createlab.hummingbird.commands.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdState3
   {
   int[] getAnalogInputValues();

   /**
    * The Hummingbird Duo can now return an analog voltage for the motor power port.  For the Duo, this method returns
    * <code>true</code> if the voltage is greater than or equal to 2V, <code>false</code> otherwise.  For the older
    * HID Hummingbird, motor power is binary, so this method just reflects the value read.
    */
   boolean isMotorPowerEnabled();

   /**
    * The Hummingbird Duo can now return an analog voltage for the motor power port.  This method returns that value.
    * For the older HID Hummingbird, this will just return 5 if motor power is enabled, and 0 otherwise.
    */
   double getMotorPowerPortVoltage();
   }