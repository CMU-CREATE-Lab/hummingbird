package edu.cmu.ri.createlab.hummingbird;

import java.awt.Color;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface Hummingbird extends CreateLabDeviceProxy
   {
   /**
    * Gets the hummingbird's state.  Returns <code>null</code> if an error occurred while getting the state.
    */
   HummingbirdState getState();

   /**
    * Returns the value of the given port id; returns <code>-1</code> if an error occurred while trying to read the value.
    *
    * @throws IllegalArgumentException if the <code>analogInputPortId</code> specifies an invalid port
    */
   Integer getAnalogInputValue(int analogInputPortId);

   /**
    * Sets the motor specified by the given <code>motorId</code> to the given (signed) <code>velocity</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param motorId the motor to control [0 or 1]
    * @param velocity the signed velocity [-255 to 255]
    *
    * @throws IllegalArgumentException if the <code>motorId</code> specifies an invalid port
    */
   boolean setMotorVelocity(int motorId, int velocity);

   /**
    * Sets the motors specified by the given <code>mask</code> to the given <code>velocities</code>.  Returns
    * the current velocities as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   int[] setMotorVelocities(boolean[] mask, int[] velocities);

   /**
    * Sets the vibration motor specified by the given <code>motorId</code> to the given <code>speed</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param motorId the motor to control [0 or 1]
    * @param speed the speed [0 to 255]
    *
    * @throws IllegalArgumentException if the <code>motorId</code> specifies an invalid port
    */
   boolean setVibrationMotorSpeed(int motorId, int speed);

   /**
    * Sets the vibration motors specified by the given <code>mask</code> to the given <code>speeds</code>.  Returns
    * the current speeds as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   int[] setVibrationMotorSpeeds(boolean[] mask, int[] speeds);

   /**
    * Sets the servo specified by the given <code>servoId</code> to the given <code>position</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param servoId the servo to control [0 to 3]
    * @param position the position [0 to 255]
    *
    * @throws IllegalArgumentException if the <code>servoId</code> specifies an invalid port
    */
   boolean setServoPosition(int servoId, int position);

   /**
    * Sets the servo motors specified by the given <code>mask</code> to the given <code>positions</code>.  Returns
    * the current positions as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   int[] setServoPositions(boolean[] mask, int[] positions);

   /**
    * Sets the LED specified by the given <code>ledId</code> to the given <code>intensity</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param ledId the LED to control [0 or 1]
    * @param intensity the intensity [0 to 255]
    *
    * @throws IllegalArgumentException if the <code>ledId</code> specifies an invalid port
    */
   boolean setLED(int ledId, int intensity);

   /**
    * Sets the LEDs specified by the given <code>mask</code> to the given <code>intensities</code>.
    * Returns the current intensities as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   int[] setLEDs(boolean[] mask, int[] intensities);

   /**
    * Sets the full-color LED specified by the given <code>ledId</code> to the given red, green, and blue intensities.
    * Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param ledId the LED to control [0 or 1]
    * @param red the intensity of the LED's red component [0 to 255]
    * @param green the intensity of the LED's green component [0 to 255]
    * @param blue the intensity of the LED's blue component [0 to 255]
    *
    * @throws IllegalArgumentException if the <code>ledId</code> specifies an invalid port
    */
   boolean setFullColorLED(int ledId, int red, int green, int blue);

   /**
    * Sets the full-color LEDs specified by the given <code>mask</code> to the given {@link Color colors}. Returns the
    * current colors as an array of {@link Color colors} if the command succeeded, <code>null</code> otherwise.
    */
   Color[] setFullColorLEDs(boolean[] mask, Color[] colors);

   /** Plays a tone having the given <code>frequency</code>, <code>amplitude</code>, and <code>duration</code>. */
   void playTone(int frequency, int amplitude, int duration);

   /** Plays the sound clip contained in the given <code>byte</code> array. */
   void playClip(byte[] data);

   /**
    * Turns off all motors, vibrations motors, LEDs, and full-color LEDs. Returns <code>true</code> if the command
    * succeeded, <code>false</code> otherwise.
    */
   boolean emergencyStop();

   /**
    * @author Chris Bartley (bartley@cmu.edu)
    */
   interface HummingbirdState
      {
      Color[] getFullColorLEDs();

      int[] getLedIntensities();

      int[] getServoPositions();

      int[] getMotorVelocities();

      int[] getVibrationMotorSpeeds();

      int[] getAnalogInputValues();
      }
   }
