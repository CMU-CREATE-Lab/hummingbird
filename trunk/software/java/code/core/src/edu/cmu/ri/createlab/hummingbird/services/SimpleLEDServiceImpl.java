package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
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
   static SimpleLEDServiceImpl create(final Hummingbird hummingbird)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MIN_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MIN_INTENSITY);
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MAX_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MAX_INTENSITY);

      return new SimpleLEDServiceImpl(hummingbird,
                                      basicPropertyManager,
                                      HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      }

   private final Hummingbird hummingbird;

   private SimpleLEDServiceImpl(final Hummingbird hummingbird,
                                final PropertyManager propertyManager,
                                final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   protected int[] execute(final boolean[] mask, final int[] intensities)
      {
      return hummingbird.setLEDs(mask, intensities);
      }
   }
