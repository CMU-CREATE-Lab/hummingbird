package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.analog.AnalogInputsService;
import edu.cmu.ri.createlab.terk.services.analog.BaseAnalogInputsServiceImpl;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AnalogInputsServiceImpl extends BaseAnalogInputsServiceImpl
   {
   static AnalogInputsServiceImpl create(final Hummingbird hummingbird)
      {
      final HummingbirdProperties hummingbirdProperties = hummingbird.getHummingbirdProperties();
      final int deviceCount = hummingbirdProperties.getAnalogInputDeviceCount();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);
      basicPropertyManager.setReadOnlyProperty(AnalogInputsService.PROPERTY_NAME_MIN_VALUE, hummingbirdProperties.getAnalogInputMinValue());
      basicPropertyManager.setReadOnlyProperty(AnalogInputsService.PROPERTY_NAME_MAX_VALUE, hummingbirdProperties.getAnalogInputMaxValue());

      return new AnalogInputsServiceImpl(hummingbird,
                                         basicPropertyManager,
                                         deviceCount);
      }

   private final Hummingbird hummingbird;

   private AnalogInputsServiceImpl(final Hummingbird hummingbird,
                                   final PropertyManager propertyManager,
                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbird = hummingbird;
      }

   @Override
   public Integer getAnalogInputValue(final int analogInputPortId)
      {
      // check id
      if (analogInputPortId < 0 || analogInputPortId >= getDeviceCount())
         {
         throw new IllegalArgumentException("The analog input port id " + analogInputPortId + " is not valid.  Ids must be within the range [0," + getDeviceCount() + ")");
         }

      return hummingbird.getAnalogInputValue(analogInputPortId);
      }

   @Override
   public int[] getAnalogInputValues()
      {
      final Hummingbird.HummingbirdState hummingbirdState = hummingbird.getState();
      if (hummingbirdState != null)
         {
         return hummingbirdState.getAnalogInputValues();
         }

      return null;
      }
   }