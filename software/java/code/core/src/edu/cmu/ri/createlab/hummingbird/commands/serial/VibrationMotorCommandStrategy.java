package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.commands.VibrationMotorCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VibrationMotorCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final VibrationMotorCommandStrategyHelper helper;

   public VibrationMotorCommandStrategy(final int motorId, final int speed)
      {
      helper = new VibrationMotorCommandStrategyHelper(motorId, speed, SerialDeviceIndexConversionStrategy.getInstance());
      }

   public VibrationMotorCommandStrategy(final boolean[] motorMask, final int[] speeds)
      {
      helper = new VibrationMotorCommandStrategyHelper(motorMask, speeds, SerialDeviceIndexConversionStrategy.getInstance());
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }