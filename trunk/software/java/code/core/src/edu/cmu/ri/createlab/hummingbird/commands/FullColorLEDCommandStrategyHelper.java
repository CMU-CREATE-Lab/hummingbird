package edu.cmu.ri.createlab.hummingbird.commands;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategyHelper extends BaseCommandStrategyHelper
   {
   /** The command character used to turn on a full-color LED. */
   private static final byte COMMAND_PREFIX = 'O';

   private static final int BYTES_PER_COMMAND = 5;

   private final byte[] command;

   private final int minIntensity;
   private final int maxIntensity;

   public FullColorLEDCommandStrategyHelper(final int ledId, final int red, final int green, final int blue, final HummingbirdProperties hummingbirdProperties)
      {
      this.minIntensity = hummingbirdProperties.getFullColorLedDeviceMinIntensity();
      this.maxIntensity = hummingbirdProperties.getFullColorLedDeviceMaxIntensity();

      if (ledId < 0 || ledId >= hummingbirdProperties.getFullColorLedDeviceCount())
         {
         throw new IllegalArgumentException("Invalid full-color LED index");
         }

      // construct the command
      this.command = new byte[]{COMMAND_PREFIX,
                                convertDeviceIndex(ledId),
                                cleanIntensity(red),
                                cleanIntensity(green),
                                cleanIntensity(blue)};
      }

   public FullColorLEDCommandStrategyHelper(final boolean[] mask, final Color[] colors, final HummingbirdProperties hummingbirdProperties)
      {
      this.minIntensity = hummingbirdProperties.getFullColorLedDeviceMinIntensity();
      this.maxIntensity = hummingbirdProperties.getFullColorLedDeviceMaxIntensity();

      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(mask.length, colors.length), hummingbirdProperties.getFullColorLedDeviceCount());
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
         final Color color = colors[index];
         this.command[i * BYTES_PER_COMMAND] = COMMAND_PREFIX;
         this.command[i * BYTES_PER_COMMAND + 1] = convertDeviceIndex(index);
         this.command[i * BYTES_PER_COMMAND + 2] = cleanIntensity(Math.abs(color.getRed()));
         this.command[i * BYTES_PER_COMMAND + 3] = cleanIntensity(Math.abs(color.getGreen()));
         this.command[i * BYTES_PER_COMMAND + 4] = cleanIntensity(Math.abs(color.getBlue()));
         i++;
         }
      }

   private byte cleanIntensity(final int rawIntensity)
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