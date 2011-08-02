package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.commands.DeviceIndexConversionStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class SerialDeviceIndexConversionStrategy implements DeviceIndexConversionStrategy
   {
   private static final DeviceIndexConversionStrategy INSTANCE = new SerialDeviceIndexConversionStrategy();

   public static DeviceIndexConversionStrategy getInstance()
      {
      return INSTANCE;
      }

   private SerialDeviceIndexConversionStrategy()
      {
      // private to prevent instantiation
      }

   @Override
   public byte convertDeviceIndex(final int index)
      {
      return (byte)String.valueOf(index).charAt(0);
      }
   }
