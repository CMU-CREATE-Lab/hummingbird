package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProxy;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.led.BaseSimpleLEDServiceImpl;
import edu.cmu.ri.createlab.terk.services.led.SimpleLEDService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SimpleLEDServiceImpl extends BaseSimpleLEDServiceImpl
   {
   static SimpleLEDServiceImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MIN_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MIN_INTENSITY);
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MAX_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MAX_INTENSITY);

      return new SimpleLEDServiceImpl(hummingbirdProxy,
                                      basicPropertyManager,
                                      HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private SimpleLEDServiceImpl(final HummingbirdProxy hummingbirdProxy,
                                final PropertyManager propertyManager,
                                final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] intensities)
      {
      return hummingbirdProxy.setLEDs(mask, intensities);
      }
   }
