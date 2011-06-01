package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.motor.BaseSpeedControllableMotorServiceImpl;
import edu.cmu.ri.createlab.terk.services.motor.SpeedControllableMotorService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SpeedControllableMotorServiceImpl extends BaseSpeedControllableMotorServiceImpl
   {
   static SpeedControllableMotorServiceImpl create(final Hummingbird hummingbird)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MIN_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MIN_SPEED);
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MAX_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MAX_SPEED);

      return new SpeedControllableMotorServiceImpl(hummingbird,
                                                   basicPropertyManager,
                                                   HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      }

   private final Hummingbird hummingbird;

   private SpeedControllableMotorServiceImpl(final Hummingbird hummingbird,
                                             final PropertyManager propertyManager,
                                             final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   protected int[] execute(final boolean[] mask, final int[] speeds)
      {
      return hummingbird.setVibrationMotorSpeeds(mask, speeds);
      }
   }