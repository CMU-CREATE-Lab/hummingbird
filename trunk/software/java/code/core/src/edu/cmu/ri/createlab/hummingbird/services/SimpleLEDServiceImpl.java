package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
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
      final HummingbirdProperties hummingbirdProperties = hummingbird.getHummingbirdProperties();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, hummingbirdProperties.getSimpleLedDeviceCount());
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MIN_INTENSITY, hummingbirdProperties.getSimpleLedDeviceMinIntensity());
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MAX_INTENSITY, hummingbirdProperties.getSimpleLedDeviceMaxIntensity());

      return new SimpleLEDServiceImpl(hummingbird,
                                      basicPropertyManager,
                                      hummingbirdProperties.getSimpleLedDeviceCount());
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
