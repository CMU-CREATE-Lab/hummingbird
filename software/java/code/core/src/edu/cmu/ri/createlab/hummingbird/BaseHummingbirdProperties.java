package edu.cmu.ri.createlab.hummingbird;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseHummingbirdProperties implements HummingbirdProperties
   {
   /** The number of hummingbirds */
   private static final int HUMMINGBIRD_DEVICE_COUNT = 1;

   /** The minimum value returned by analog inputs */
   private static final int ANALOG_INPUT_MIN_VALUE = 0;

   /** The maximum value returned by analog inputs */
   private static final int ANALOG_INPUT_MAX_VALUE = 255;

   /** The number of audio outputs */
   private static final int AUDIO_DEVICE_COUNT = 1;

   /** The minimum supported tone frequency */
   private static final int AUDIO_DEVICE_MIN_AMPLITUDE = 0;

   /** The maximum supported tone frequency */
   private static final int AUDIO_DEVICE_MAX_AMPLITUDE = 10;

   /** The minimum supported tone duration */
   private static final int AUDIO_DEVICE_MIN_DURATION = 0;

   /** The maximum supported tone duration */
   private static final int AUDIO_DEVICE_MAX_DURATION = Integer.MAX_VALUE;

   /** The minimum supported tone frequency */
   private static final int AUDIO_DEVICE_MIN_FREQUENCY = 0;

   /** The maximum supported tone frequency */
   private static final int AUDIO_DEVICE_MAX_FREQUENCY = Integer.MAX_VALUE;

   /** The number of LEDs */
   private static final int SIMPLE_LED_DEVICE_COUNT = 4;

   /** The minimum supported LED intensity */
   private static final int SIMPLE_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported LED intensity */
   private static final int SIMPLE_LED_DEVICE_MAX_INTENSITY = 255;

   /** The number of full-color LEDS */
   private static final int FULL_COLOR_LED_DEVICE_COUNT = 2;

   /** The minimum supported full-color LED intensity */
   private static final int FULL_COLOR_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported full-color LED intensity */
   private static final int FULL_COLOR_LED_DEVICE_MAX_INTENSITY = 255;

   /** The number of motors */
   private static final int MOTOR_DEVICE_COUNT = 2;

   /** The minimum supported velocity */
   private static final int MOTOR_DEVICE_MIN_VELOCITY = -255;

   /** The maximum supported velocity */
   private static final int MOTOR_DEVICE_MAX_VELOCITY = 255;

   /** The number of vibration motors */
   private static final int VIBRATION_MOTOR_DEVICE_COUNT = 2;

   /** The minimum supported speed */
   private static final int VIBRATION_MOTOR_DEVICE_MIN_SPEED = 0;

   /** The maximum supported speed */
   private static final int VIBRATION_MOTOR_DEVICE_MAX_SPEED = 255;

   /** The maximum supported safe speed */
   private static final int VIBRATION_MOTOR_DEVICE_MAX_SAFE_SPEED = 100;

   /** The number of servos */
   private static final int SIMPLE_SERVO_DEVICE_COUNT = 4;

   /** The minimum supported servo position */
   private static final int SIMPLE_SERVO_DEVICE_MIN_POSITION = 0;

   /** The maximum supported servo position */
   private static final int SIMPLE_SERVO_DEVICE_MAX_POSITION = 255;

   @Override
   public final int getHummingbirdDeviceCount()
      {
      return HUMMINGBIRD_DEVICE_COUNT;
      }

   @Override
   public final int getAnalogInputMinValue()
      {
      return ANALOG_INPUT_MIN_VALUE;
      }

   @Override
   public final int getAnalogInputMaxValue()
      {
      return ANALOG_INPUT_MAX_VALUE;
      }

   @Override
   public final int getAudioDeviceCount()
      {
      return AUDIO_DEVICE_COUNT;
      }

   @Override
   public final int getAudioDeviceMinAmplitude()
      {
      return AUDIO_DEVICE_MIN_AMPLITUDE;
      }

   @Override
   public final int getAudioDeviceMaxAmplitude()
      {
      return AUDIO_DEVICE_MAX_AMPLITUDE;
      }

   @Override
   public final int getAudioDeviceMinDuration()
      {
      return AUDIO_DEVICE_MIN_DURATION;
      }

   @Override
   public final int getAudioDeviceMaxDuration()
      {
      return AUDIO_DEVICE_MAX_DURATION;
      }

   @Override
   public final int getAudioDeviceMinFrequency()
      {
      return AUDIO_DEVICE_MIN_FREQUENCY;
      }

   @Override
   public final int getAudioDeviceMaxFrequency()
      {
      return AUDIO_DEVICE_MAX_FREQUENCY;
      }

   @Override
   public final int getSimpleLedDeviceCount()
      {
      return SIMPLE_LED_DEVICE_COUNT;
      }

   @Override
   public final int getSimpleLedDeviceMinIntensity()
      {
      return SIMPLE_LED_DEVICE_MIN_INTENSITY;
      }

   @Override
   public final int getSimpleLedDeviceMaxIntensity()
      {
      return SIMPLE_LED_DEVICE_MAX_INTENSITY;
      }

   @Override
   public final int getFullColorLedDeviceCount()
      {
      return FULL_COLOR_LED_DEVICE_COUNT;
      }

   @Override
   public final int getFullColorLedDeviceMinIntensity()
      {
      return FULL_COLOR_LED_DEVICE_MIN_INTENSITY;
      }

   @Override
   public final int getFullColorLedDeviceMaxIntensity()
      {
      return FULL_COLOR_LED_DEVICE_MAX_INTENSITY;
      }

   @Override
   public final int getMotorDeviceCount()
      {
      return MOTOR_DEVICE_COUNT;
      }

   @Override
   public final int getMotorDeviceMinVelocity()
      {
      return MOTOR_DEVICE_MIN_VELOCITY;
      }

   @Override
   public final int getMotorDeviceMaxVelocity()
      {
      return MOTOR_DEVICE_MAX_VELOCITY;
      }

   @Override
   public final int getVibrationMotorDeviceCount()
      {
      return VIBRATION_MOTOR_DEVICE_COUNT;
      }

   @Override
   public final int getVibrationMotorDeviceMinSpeed()
      {
      return VIBRATION_MOTOR_DEVICE_MIN_SPEED;
      }

   @Override
   public final int getVibrationMotorDeviceMaxSpeed()
      {
      return VIBRATION_MOTOR_DEVICE_MAX_SPEED;
      }

   @Override
   // TODO: start using this--see the "Known Bugs" section of the Hummingbird HID Protocol document
   public final int getVibrationMotorDeviceMaxSafeSpeed()
      {
      return VIBRATION_MOTOR_DEVICE_MAX_SAFE_SPEED;
      }

   @Override
   public final int getSimpleServoDeviceCount()
      {
      return SIMPLE_SERVO_DEVICE_COUNT;
      }

   @Override
   public final int getSimpleServoDeviceMinPosition()
      {
      return SIMPLE_SERVO_DEVICE_MIN_POSITION;
      }

   @Override
   public final int getSimpleServoDeviceMaxPosition()
      {
      return SIMPLE_SERVO_DEVICE_MAX_POSITION;
      }
   }
