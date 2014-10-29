package edu.cmu.ri.createlab.hummingbird;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdProperties
   {
   /** Returns the number of hummingbirds. */
   int getHummingbirdDeviceCount();

   String getDeviceCommonName();

   HummingbirdHardwareType getHardwareType();

   /** Returns the number of analog inputs. */
   int getAnalogInputDeviceCount();

   /** Returns the minimum value returned by analog inputs. */
   int getAnalogInputMinValue();

   /** Returns the minimum value returned by analog inputs. */
   int getAnalogInputMaxValue();

   /** Returns the number of audio outputs */
   int getAudioDeviceCount();

   /** Returns the minimum supported audio device amplitude */
   int getAudioDeviceMinAmplitude();

   /** Returns the maximum supported audio device amplitude */
   int getAudioDeviceMaxAmplitude();

   /** Returns the minimum supported tone duration */
   int getAudioDeviceMinDuration();

   /** Returns the maximum supported tone duration */
   int getAudioDeviceMaxDuration();

   /** Returns the minimum supported tone frequency */
   int getAudioDeviceMinFrequency();

   /** Returns the maximum supported tone frequency */
   int getAudioDeviceMaxFrequency();

   /** Returns the number of LEDs */
   int getSimpleLedDeviceCount();

   /** Returns the minimum supported LED intensity */
   int getSimpleLedDeviceMinIntensity();

   /** Returns the maximum supported LED intensity */
   int getSimpleLedDeviceMaxIntensity();

   /** Returns the number of full-color LEDS */
   int getFullColorLedDeviceCount();

   /** Returns the minimum supported full-color LED intensity */
   int getFullColorLedDeviceMinIntensity();

   /** Returns the maximum supported full-color LED intensity */
   int getFullColorLedDeviceMaxIntensity();

   /** Returns the voltage detected on the motor power port */
   double getMaxMotorPowerPortVoltage();

   /** Returns the number of motors */
   int getMotorDeviceCount();

   /** Returns the minimum supported velocity */
   int getMotorDeviceMinVelocity();

   /** Returns the maximum supported velocity */
   int getMotorDeviceMaxVelocity();

   /** Returns the number of vibration motors */
   int getVibrationMotorDeviceCount();

   /** Returns the minimum supported speed */
   int getVibrationMotorDeviceMinSpeed();

   /** Returns the maximum supported speed */
   int getVibrationMotorDeviceMaxSpeed();

   /** Returns the maximum supported safe speed */
   int getVibrationMotorDeviceMaxSafeSpeed();

   /** Returns the number of servos */
   int getSimpleServoDeviceCount();

   /** Returns the minimum supported servo position */
   int getSimpleServoDeviceMinPosition();

   /** Returns the maximum supported servo position */
   int getSimpleServoDeviceMaxPosition();

   /** Returns the minimum supported safe servo position */
   int getSimpleServoDeviceMinSafePosition();

   /** Returns the maximum supported safe servo position */
   int getSimpleServoDeviceMaxSafePosition();
   }

