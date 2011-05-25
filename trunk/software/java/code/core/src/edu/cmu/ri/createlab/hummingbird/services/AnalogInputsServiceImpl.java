package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProxy;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.analog.BaseAnalogInputsServiceImpl;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AnalogInputsServiceImpl extends BaseAnalogInputsServiceImpl
   {
   static AnalogInputsServiceImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      final int deviceCount = HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT;

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);

      return new AnalogInputsServiceImpl(hummingbirdProxy,
                                         basicPropertyManager,
                                         deviceCount);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private AnalogInputsServiceImpl(final HummingbirdProxy hummingbirdProxy,
                                   final PropertyManager propertyManager,
                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   @Override
   public Integer getAnalogInputValue(final int analogInputPortId)
      {
      // check id
      if (analogInputPortId < 0 || analogInputPortId >= getDeviceCount())
         {
         throw new IllegalArgumentException("The analog input port id " + analogInputPortId + " is not valid.  Ids must be within the range [0," + getDeviceCount() + ")");
         }

      return hummingbirdProxy.getAnalogInputValue(analogInputPortId);
      }

   @Override
   public int[] getAnalogInputValues()
      {
      final Hummingbird.HummingbirdState hummingbirdState = hummingbirdProxy.getState();
      if (hummingbirdState != null)
         {
         return hummingbirdState.getAnalogInputValues();
         }

      return null;
      }
   }