package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProxy;
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
   static SpeedControllableMotorServiceImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MIN_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MIN_SPEED);
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MAX_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MAX_SPEED);

      return new SpeedControllableMotorServiceImpl(hummingbirdProxy,
                                                   basicPropertyManager,
                                                   HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private SpeedControllableMotorServiceImpl(final HummingbirdProxy hummingbirdProxy,
                                             final PropertyManager propertyManager,
                                             final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] speeds)
      {
      return hummingbirdProxy.setVibrationMotorSpeeds(mask, speeds);
      }
   }