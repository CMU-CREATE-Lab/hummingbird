package edu.cmu.ri.createlab.hummingbird.commands.hid;

import java.awt.Color;
import edu.cmu.ri.createlab.hummingbird.commands.FullColorLEDCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

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
   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }