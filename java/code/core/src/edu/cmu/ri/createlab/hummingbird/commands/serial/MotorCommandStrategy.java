package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.MotorCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final MotorCommandStrategyHelper helper;

   public MotorCommandStrategy(final int motorId, final int velocity, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new MotorCommandStrategyHelper(motorId, velocity, hummingbirdProperties);
      }

   public MotorCommandStrategy(final boolean[] motorMask, final int[] velocities, final HummingbirdProperties hummingbirdProperties)
      {
      helper = new MotorCommandStrategyHelper(motorMask, velocities, hummingbirdProperties);
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }