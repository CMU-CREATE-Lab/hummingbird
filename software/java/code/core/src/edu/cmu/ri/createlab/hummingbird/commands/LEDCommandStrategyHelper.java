package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LEDCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to turn on an LED. */
   private static final byte COMMAND_PREFIX = 'L';

   private static final int BYTES_PER_COMMAND = 3;

   private final byte[] command;

   public LEDCommandStrategyHelper(final int ledId, final int intensity, final HummingbirdProperties hummingbirdProperties)
      {

      if (ledId < 0 || ledId >= hummingbirdProperties.getSimpleLedDeviceCount())
         {
         throw new IllegalArgumentException("Invalid LED index");
         }

      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex(ledId),
                                cleanIntensity(intensity,
                                               hummingbirdProperties.getFullColorLedDeviceMinIntensity(),
                                               hummingbirdProperties.getFullColorLedDeviceMaxIntensity())};
      }

   public LEDCommandStrategyHelper(final boolean[] mask, final int[] intensities, final HummingbirdProperties hummingbirdProperties)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(mask.length, intensities.length), hummingbirdProperties.getSimpleLedDeviceCount());
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
      final int minIntensity = hummingbirdProperties.getFullColorLedDeviceMinIntensity();
      final int maxIntensity = hummingbirdProperties.getFullColorLedDeviceMaxIntensity();
      for (final int index : maskedIndeces)
         {
         this.command[i * BYTES_PER_COMMAND] = COMMAND_PREFIX;
         this.command[i * BYTES_PER_COMMAND + 1] = convertDeviceIndex(index);
         this.command[i * BYTES_PER_COMMAND + 2] = cleanIntensity(Math.abs(intensities[index]), minIntensity, maxIntensity);
         i++;
         }
      }

   private byte cleanIntensity(final int rawIntensity, final int minIntensity, final int maxIntensity)
      {
      // clamp the intensity to the allowed range
      int intensity = Math.min(Math.max(rawIntensity, minIntensity), maxIntensity);

      // now ensure that the intensity is an even number, due to a firmware timing issue
      intensity = intensity >> 1 << 1;

      // finally, convert it to an unsigned byte
      return ByteUtils.intToUnsignedByte(intensity);
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }