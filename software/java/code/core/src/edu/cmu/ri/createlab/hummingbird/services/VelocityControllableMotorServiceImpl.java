package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.motor.BaseVelocityControllableMotorServiceImpl;
import edu.cmu.ri.createlab.terk.services.motor.VelocityControllableMotorService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class VelocityControllableMotorServiceImpl extends BaseVelocityControllableMotorServiceImpl
   {
   static VelocityControllableMotorServiceImpl create(final Hummingbird hummingbird)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, HummingbirdConstants.MOTOR_DEVICE_MIN_VELOCITY);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, HummingbirdConstants.MOTOR_DEVICE_MAX_VELOCITY);

      return new VelocityControllableMotorServiceImpl(hummingbird,
                                                      basicPropertyManager,
                                                      HummingbirdConstants.MOTOR_DEVICE_COUNT);
      }

   private final Hummingbird hummingbird;

   private VelocityControllableMotorServiceImpl(final Hummingbird hummingbird,
                                                final PropertyManager propertyManager,
                                                final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   protected int[] execute(final boolean[] mask, final int[] velocities)
      {
      return hummingbird.setMotorVelocities(mask, velocities);
      }
   }