package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.HummingbirdUtils;
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

   private final int minPosition;
   private final int maxPosition;

   public ServoCommandStrategyHelper(final int servoId, final int position, final HummingbirdProperties hummingbirdProperties)
      {
      this.minPosition = hummingbirdProperties.getSimpleServoDeviceMinPosition();
      this.maxPosition = hummingbirdProperties.getSimpleServoDeviceMaxPosition();

      if (servoId < 0 || servoId >= hummingbirdProperties.getSimpleServoDeviceCount())
         {
         throw new IllegalArgumentException("Invalid servo index");
         }

      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex((servoId)),
                                cleanPosition(position)};
      }

   public ServoCommandStrategyHelper(final boolean[] mask, final int[] positions, final HummingbirdProperties hummingbirdProperties)
      {
      this.minPosition = hummingbirdProperties.getSimpleServoDeviceMinPosition();
      this.maxPosition = hummingbirdProperties.getSimpleServoDeviceMaxPosition();

      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = HummingbirdUtils.computeMaskedOnIndeces(mask, Math.min(positions.length, hummingbirdProperties.getSimpleServoDeviceCount()));

      // construct the command
      this.command = new byte[maskedIndeces.size() * BYTES_PER_COMMAND];
      int i = 0;
      for (final int index : maskedIndeces)
         {
         this.command[i * BYTES_PER_COMMAND] = COMMAND_PREFIX;
         this.command[i * BYTES_PER_COMMAND + 1] = convertDeviceIndex((index));
         this.command[i * BYTES_PER_COMMAND + 2] = cleanPosition(Math.abs(positions[index]));
         i++;
         }
      }

   private byte cleanPosition(final int rawPosition)
      {
      // clamp the position to the allowed range
      return ByteUtils.intToUnsignedByte(Math.min(Math.max(rawPosition, minPosition), maxPosition));
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }