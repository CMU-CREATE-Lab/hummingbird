package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.hummingbird.commands.ServoCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final ServoCommandStrategyHelper helper;

   public ServoCommandStrategy(final int servoId, final int position)
      {
      if (servoId < 0 || servoId >= HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT)
         {
         throw new IllegalArgumentException("Invalid servo index");
         }

      helper = new ServoCommandStrategyHelper(servoId,
                                              position
      );
      }

   public ServoCommandStrategy(final boolean[] mask, final int[] positions)
      {
      helper = new ServoCommandStrategyHelper(mask, positions);
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