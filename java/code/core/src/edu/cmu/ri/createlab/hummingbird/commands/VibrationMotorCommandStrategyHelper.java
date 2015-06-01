package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.HummingbirdUtils;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VibrationMotorCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to turn on a vibration motor. */
   private static final byte COMMAND_PREFIX = 'V';

   private static final int BYTES_PER_COMMAND = 3;

   private final byte[] command;

   public VibrationMotorCommandStrategyHelper(final int motorId, final int speed, final HummingbirdProperties hummingbirdProperties)
      {
      if (motorId < 0 || motorId >= hummingbirdProperties.getVibrationMotorDeviceCount())
         {
         throw new IllegalArgumentException("Invalid vibration motor index");
         }

      final int absoluteSpeed = Math.abs(speed);
      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex(motorId),
                                ByteUtils.intToUnsignedByte(absoluteSpeed)};
      }

   public VibrationMotorCommandStrategyHelper(final boolean[] mask, final int[] speeds, final HummingbirdProperties hummingbirdProperties)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(speeds.length, hummingbirdProperties.getVibrationMotorDeviceCount()));

      // construct the command
      this.command = new byte[maskedIndeces.size() * BYTES_PER_COMMAND];
      int i = 0;
      for (final int index : maskedIndeces)
         {
         this.command[i * BYTES_PER_COMMAND] = COMMAND_PREFIX;
         this.command[i * BYTES_PER_COMMAND + 1] = convertDeviceIndex(index);
         this.command[i * BYTES_PER_COMMAND + 2] = ByteUtils.intToUnsignedByte(Math.abs(speeds[index]));
         i++;
         }
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }