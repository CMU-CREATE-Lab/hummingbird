package edu.cmu.ri.createlab.hummingbird.commands;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FullColorLEDCommandStrategyHelper
   {
   /** The command character used to turn on a full-color LED. */
   private static final byte COMMAND_PREFIX = 'O';

   private static final int BYTES_PER_COMMAND = 5;

   private final byte[] command;

   public FullColorLEDCommandStrategyHelper(final int ledId, final int red, final int green, final int blue, final DeviceIndexConversionStrategy indexConversionStrategy)
      {
      if (ledId < 0 || ledId >= HummingbirdConstants.FULL_COLOR_LED_DEVICE_COUNT)
         {
         throw new IllegalArgumentException("Invalid full-color LED index");
         }

      // construct the command
      this.command = new byte[]{COMMAND_PREFIX,
                                indexConversionStrategy.convertDeviceIndex(ledId),
                                ByteUtils.intToUnsignedByte(red),
                                ByteUtils.intToUnsignedByte(green),
                                ByteUtils.intToUnsignedByte(blue)};
      }

   public FullColorLEDCommandStrategyHelper(final boolean[] mask, final Color[] colors, final DeviceIndexConversionStrategy indexConversionStrategy)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(mask.length, colors.length), HummingbirdConstants.FULL_COLOR_LED_DEVICE_COUNT);
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
         this.command[i * BYTES_PER_COMMAND + 1] = indexConversionStrategy.convertDeviceIndex(index);
         this.command[i * BYTES_PER_COMMAND + 2] = ByteUtils.intToUnsignedByte(Math.abs(color.getRed()));
         this.command[i * BYTES_PER_COMMAND + 3] = ByteUtils.intToUnsignedByte(Math.abs(color.getGreen()));
         this.command[i * BYTES_PER_COMMAND + 4] = ByteUtils.intToUnsignedByte(Math.abs(color.getBlue()));
         i++;
         }
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }