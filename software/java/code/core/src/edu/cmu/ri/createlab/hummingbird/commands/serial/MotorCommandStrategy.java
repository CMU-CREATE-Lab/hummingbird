package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.commands.MotorCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final MotorCommandStrategyHelper helper;

   public MotorCommandStrategy(final int motorId, final int velocity)
      {
      helper = new MotorCommandStrategyHelper(motorId, velocity);
      }

   public MotorCommandStrategy(final boolean[] motorMask, final int[] velocities)
      {
      helper = new MotorCommandStrategyHelper(motorMask, velocities);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }