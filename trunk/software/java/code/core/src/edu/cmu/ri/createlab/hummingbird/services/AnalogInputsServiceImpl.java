package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.analog.BaseAnalogInputsServiceImpl;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AnalogInputsServiceImpl extends BaseAnalogInputsServiceImpl
   {
   static AnalogInputsServiceImpl create(final Hummingbird hummingbird)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      final int deviceCount = HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT;

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);

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