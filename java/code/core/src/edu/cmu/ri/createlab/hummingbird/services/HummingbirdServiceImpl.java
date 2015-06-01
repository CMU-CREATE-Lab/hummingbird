package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.HummingbirdVersionNumber;
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
      final HummingbirdVersionNumber hardwareVersion = hummingbird.getHardwareVersion();
      final HummingbirdVersionNumber firmwareVersion = hummingbird.getFirmwareVersion();

      final HummingbirdProperties hummingbirdProperties = hummingbird.getHummingbirdProperties();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, hummingbirdProperties.getHummingbirdDeviceCount());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_TYPE, hummingbirdProperties.getHardwareType().getName());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION, hardwareVersion.getMajorMinorRevision());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION_MAJOR, hardwareVersion.getMajorVersion());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION_MINOR, hardwareVersion.getMinorVersion());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION_REVISION, hardwareVersion.getRevision());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.FIRMWARE_VERSION, firmwareVersion.getMajorMinorRevision());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.FIRMWARE_VERSION_MAJOR, firmwareVersion.getMajorVersion());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.FIRMWARE_VERSION_MINOR, firmwareVersion.getMinorVersion());
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.FIRMWARE_VERSION_REVISION, firmwareVersion.getRevision());

      return new HummingbirdServiceImpl(hummingbird,
                                        basicPropertyManager,
                                        hummingbirdProperties.getHummingbirdDeviceCount());
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
