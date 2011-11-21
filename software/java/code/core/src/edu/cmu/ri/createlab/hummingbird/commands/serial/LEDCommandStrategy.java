package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.LEDCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final LEDCommandStrategyHelper helper;

   public LEDCommandStrategy(final int ledId, final int intensity, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new LEDCommandStrategyHelper(ledId, intensity, hummingbirdProperties);
      }

   public LEDCommandStrategy(final boolean[] mask, final int[] intensities, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new LEDCommandStrategyHelper(mask, intensities, hummingbirdProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }