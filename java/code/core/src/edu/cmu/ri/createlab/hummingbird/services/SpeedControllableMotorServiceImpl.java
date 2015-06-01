package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
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
      final HummingbirdProperties hummingbirdProperties = hummingbird.getHummingbirdProperties();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, hummingbirdProperties.getVibrationMotorDeviceCount());
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MIN_SPEED, hummingbirdProperties.getVibrationMotorDeviceMinSpeed());
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MAX_SPEED, hummingbirdProperties.getVibrationMotorDeviceMaxSpeed());
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MAX_SAFE_SPEED, hummingbirdProperties.getVibrationMotorDeviceMaxSafeSpeed());

      return new SpeedControllableMotorServiceImpl(hummingbird,
                                                   basicPropertyManager,
                                                   hummingbirdProperties.getVibrationMotorDeviceCount());
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