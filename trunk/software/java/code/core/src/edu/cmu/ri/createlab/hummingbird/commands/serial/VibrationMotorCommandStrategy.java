package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.VibrationMotorCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VibrationMotorCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final VibrationMotorCommandStrategyHelper helper;

   public VibrationMotorCommandStrategy(final int motorId, final int speed, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new VibrationMotorCommandStrategyHelper(motorId, speed, hummingbirdProperties);
      }

   public VibrationMotorCommandStrategy(final boolean[] motorMask, final int[] speeds, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new VibrationMotorCommandStrategyHelper(motorMask, speeds, hummingbirdProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }