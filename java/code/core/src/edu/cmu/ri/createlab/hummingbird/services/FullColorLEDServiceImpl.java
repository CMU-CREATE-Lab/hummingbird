package edu.cmu.ri.createlab.hummingbird.services;

import java.awt.Color;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.led.BaseFullColorLEDServiceImpl;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FullColorLEDServiceImpl extends BaseFullColorLEDServiceImpl
   {
   static FullColorLEDServiceImpl create(final Hummingbird hummingbird)
      {
      final HummingbirdProperties hummingbirdProperties = hummingbird.getHummingbirdProperties();
      final int deviceCount = hummingbirdProperties.getFullColorLedDeviceCount();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, hummingbirdProperties.getFullColorLedDeviceMinIntensity());
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, hummingbirdProperties.getFullColorLedDeviceMaxIntensity());

      return new FullColorLEDServiceImpl(hummingbird,
                                         basicPropertyManager,
                                         deviceCount);
      }

   private final Hummingbird hummingbird;

   private FullColorLEDServiceImpl(final Hummingbird hummingbird,
                                   final PropertyManager propertyManager,
                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   public Color[] set(final boolean[] mask, final Color[] colors)
      {
      return hummingbird.setFullColorLEDs(mask, colors);
      }
   }