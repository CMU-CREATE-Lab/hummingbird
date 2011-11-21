package edu.cmu.ri.createlab.hummingbird.commands.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdState3
   {
   int[] getAnalogInputValues();

   boolean isMotorPowerEnabled();
   }