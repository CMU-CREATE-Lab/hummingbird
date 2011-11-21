package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.ServoCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final ServoCommandStrategyHelper helper;

   public ServoCommandStrategy(final int servoId, final int position, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new ServoCommandStrategyHelper(servoId, position, hummingbirdProperties);
      }

   public ServoCommandStrategy(final boolean[] mask, final int[] positions, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new ServoCommandStrategyHelper(mask, positions, hummingbirdProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }