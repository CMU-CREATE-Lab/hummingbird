package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to turn on a motor. */
   private static final byte COMMAND_PREFIX = 'M';

   private static final int BYTES_PER_COMMAND = 4;

   private final byte[] command;

   public MotorCommandStrategyHelper(final int motorId, final int velocity, final HummingbirdProperties hummingbirdProperties)
      {
      if (motorId < 0 || motorId >= hummingbirdProperties.getMotorDeviceCount())
         {
         throw new IllegalArgumentException("Invalid motor index");
         }

      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex(motorId),
                                (byte)computeDirection(velocity),
                                ByteUtils.intToUnsignedByte(Math.abs(velocity))};
      }

   public MotorCommandStrategyHelper(final boolean[] motorMask, final int[] velocities, final HummingbirdProperties hummingbirdProperties)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(motorMask.length, velocities.length), hummingbirdProperties.getMotorDeviceCount());
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
         this.command[i * BYTES_PER_COMMAND + 1] = convertDeviceIndex(index);
         this.command[i * BYTES_PER_COMMAND + 2] = (byte)computeDirection(velocities[index]);
         this.command[i * BYTES_PER_COMMAND + 3] = ByteUtils.intToUnsignedByte(Math.abs(velocities[index]));
         i++;
         }
      }

   public byte[] getCommand()
      {
      return command.clone();
      }

   // computes the direction by looking at the sign of the velocity
   private char computeDirection(final int velocity)
      {
      return velocity < 0 ? '0' : '1';
      }
   }