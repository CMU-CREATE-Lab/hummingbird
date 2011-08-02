package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.commands.DeviceIndexConversionStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class HIDDeviceIndexConversionStrategy implements DeviceIndexConversionStrategy
   {
   private static final DeviceIndexConversionStrategy INSTANCE = new HIDDeviceIndexConversionStrategy();

   public static DeviceIndexConversionStrategy getInstance()
      {
      return INSTANCE;
      }

   private HIDDeviceIndexConversionStrategy()
      {
      // private to prevent instantiation
      }

   @Override
   public byte convertDeviceIndex(final int index)
      {
      return ByteUtils.intToUnsignedByte(index);
      }
   }
