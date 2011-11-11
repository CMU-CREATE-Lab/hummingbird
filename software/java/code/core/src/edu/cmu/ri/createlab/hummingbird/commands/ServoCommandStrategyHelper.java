package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to turn on a servo motor. */
   private static final byte COMMAND_PREFIX = 'S';

   private static final int BYTES_PER_COMMAND = 3;

   private final byte[] command;

   public ServoCommandStrategyHelper(final int servoId, final int position)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex((servoId)),
                                ByteUtils.intToUnsignedByte(position)};
      }

   public ServoCommandStrategyHelper(final boolean[] mask, final int[] positions)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(mask.length, positions.length), HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT);
      for (int i = 0; i < numIndecesToCheck; i++)
         {
         if (mask[i])
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
         this.command[i * BYTES_PER_COMMAND + 1] = convertDeviceIndex((index));
         this.command[i * BYTES_PER_COMMAND + 2] = ByteUtils.intToUnsignedByte(Math.abs(positions[index]));
         i++;
         }
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }