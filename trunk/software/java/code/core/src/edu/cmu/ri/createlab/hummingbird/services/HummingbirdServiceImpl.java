package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdServiceImpl extends BaseDeviceControllingService implements HummingbirdService
   {
   static HummingbirdServiceImpl create(final Hummingbird hummingbird)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.HUMMINGBIRD_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_TYPE, HummingbirdConstants.HARDWARE_TYPE);
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION, HummingbirdConstants.HARDWARE_VERSION);

      return new HummingbirdServiceImpl(hummingbird,
                                        basicPropertyManager,
                                        HummingbirdConstants.HUMMINGBIRD_DEVICE_COUNT);
      }

   private final Hummingbird hummingbird;

   private HummingbirdServiceImpl(final Hummingbird hummingbird,
                                  final PropertyManager propertyManager,
                                  final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   @Override
   public String getTypeId()
      {
      return HummingbirdService.TYPE_ID;
      }

   public Hummingbird.HummingbirdState getHummingbirdState()
      {
      return hummingbird.getState();
      }

   public void emergencyStop()
      {
      hummingbird.emergencyStop();
      }
   }
