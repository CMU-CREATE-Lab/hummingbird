package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.commands.LEDCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final LEDCommandStrategyHelper helper;

   public LEDCommandStrategy(final int ledId, final int intensity)
      {
      helper = new LEDCommandStrategyHelper(ledId, intensity);
      }

   public LEDCommandStrategy(final boolean[] mask, final int[] intensities)
      {
      helper = new LEDCommandStrategyHelper(mask, intensities);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }