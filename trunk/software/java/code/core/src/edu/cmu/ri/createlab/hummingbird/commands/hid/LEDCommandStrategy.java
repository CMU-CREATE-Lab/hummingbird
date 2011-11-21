package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.LEDCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LEDCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

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