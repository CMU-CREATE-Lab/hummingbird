package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VibrationMotorCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to turn on a vibration motor. */
   private static final byte COMMAND_PREFIX = 'V';

   private static final int BYTES_PER_COMMAND = 3;

   private final byte[] command;

   public VibrationMotorCommandStrategy(final int motorId, final int speed)
      {
      if (motorId < 0 || motorId >= HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT)
         {
         throw new IllegalArgumentException("Invalid vibration motor index");
         }

      final int absoluteSpeed = Math.abs(speed);
      this.command = new byte[]{COMMAND_PREFIX,
                                HummingbirdCommandHelper.convertDeviceIndexToASCII(motorId),
                                ByteUtils.intToUnsignedByte(absoluteSpeed)};
      }

   public VibrationMotorCommandStrategy(final boolean[] motorMask, final int[] speeds)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(motorMask.length, speeds.length), HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      for (int i = 0; i < numIndecesToCheck; i++)
         {
         if (motorMask[i])
            {
            maskedIndeces.add(i);
            }
         }

      // construct the command
      this.command = new byte[maskedIndeces.size() * BYTES_PER_COMMAND];
      int i = 0;
      for (final int index : maskedIndeces)
         {
         this.command[i * BYTES_PER_COMMAND] = COMMAND_PREFIX;
         this.command[i * BYTES_PER_COMMAND + 1] = HummingbirdCommandHelper.convertDeviceIndexToASCII(index);
         this.command[i * BYTES_PER_COMMAND + 2] = ByteUtils.intToUnsignedByte(Math.abs(speeds[index]));
         i++;
         }
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }