package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.hummingbird.commands.ServoCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final ServoCommandStrategyHelper helper;

   public ServoCommandStrategy(final int servoId, final int position)
      {
      if (servoId < 0 || servoId >= HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT)
         {
         throw new IllegalArgumentException("Invalid servo index");
         }

      helper = new ServoCommandStrategyHelper(servoId, position);
      }

   public ServoCommandStrategy(final boolean[] mask, final int[] positions)
      {
      helper = new ServoCommandStrategyHelper(mask, positions);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }