package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.servo.BaseSimpleServoServiceImpl;
import edu.cmu.ri.createlab.terk.services.servo.SimpleServoService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SimpleServoServiceImpl extends BaseSimpleServoServiceImpl
   {
   static SimpleServoServiceImpl create(final Hummingbird hummingbird)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SimpleServoService.PROPERTY_NAME_MIN_POSITION, HummingbirdConstants.SIMPLE_SERVO_DEVICE_MIN_POSITION);
      basicPropertyManager.setReadOnlyProperty(SimpleServoService.PROPERTY_NAME_MAX_POSITION, HummingbirdConstants.SIMPLE_SERVO_DEVICE_MAX_POSITION);

      return new SimpleServoServiceImpl(hummingbird,
                                        basicPropertyManager,
                                        HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT);
      }

   private final Hummingbird hummingbird;

   private SimpleServoServiceImpl(final Hummingbird hummingbird,
                                  final PropertyManager propertyManager,
                                  final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   protected int[] execute(final boolean[] mask, final int[] positions)
      {
      return hummingbird.setServoPositions(mask, positions);
      }
   }