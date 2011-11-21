package edu.cmu.ri.createlab.hummingbird.commands.serial;

import java.awt.Color;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.FullColorLEDCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final FullColorLEDCommandStrategyHelper helper;

   public FullColorLEDCommandStrategy(final int ledId, final int red, final int green, final int blue, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new FullColorLEDCommandStrategyHelper(ledId, red, green, blue, hummingbirdProperties);
      }

   public FullColorLEDCommandStrategy(final boolean[] mask, final Color[] colors, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new FullColorLEDCommandStrategyHelper(mask, colors, hummingbirdProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }