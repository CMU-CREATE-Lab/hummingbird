package edu.cmu.ri.createlab.hummingbird.commands.serial;

import java.awt.Color;
import edu.cmu.ri.createlab.hummingbird.commands.FullColorLEDCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final FullColorLEDCommandStrategyHelper helper;

   public FullColorLEDCommandStrategy(final int ledId, final int red, final int green, final int blue)
      {
      helper = new FullColorLEDCommandStrategyHelper(ledId, red, green, blue);
      }

   public FullColorLEDCommandStrategy(final boolean[] mask, final Color[] colors)
      {
      helper = new FullColorLEDCommandStrategyHelper(mask, colors);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }