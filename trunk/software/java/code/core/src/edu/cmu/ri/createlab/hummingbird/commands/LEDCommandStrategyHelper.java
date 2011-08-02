package edu.cmu.ri.createlab.hummingbird.commands;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LEDCommandStrategyHelper
   {
   /** The command character used to turn on an LED. */
   private static final byte COMMAND_PREFIX = 'L';

   private static final int BYTES_PER_COMMAND = 3;

   private final byte[] command;

   public LEDCommandStrategyHelper(final int ledId, final int intensity, final DeviceIndexConversionStrategy indexConversionStrategy)
      {
      if (ledId < 0 || ledId >= HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT)
         {
         throw new IllegalArgumentException("Invalid LED index");
         }

      this.command = new byte[]{COMMAND_PREFIX,
                                indexConversionStrategy.convertDeviceIndex(ledId),
                                ByteUtils.intToUnsignedByte(intensity)};
      }

   public LEDCommandStrategyHelper(final boolean[] mask, final int[] intensities, final DeviceIndexConversionStrategy indexConversionStrategy)
      {
      // figure out which ids are masked on
      final Set<Integer> maskedIndeces = new HashSet<Integer>();
      final int numIndecesToCheck = Math.min(Math.min(mask.length, intensities.length), HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
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
         this.command[i * BYTES_PER_COMMAND + 1] = indexConversionStrategy.convertDeviceIndex(index);
         this.command[i * BYTES_PER_COMMAND + 2] = ByteUtils.intToUnsignedByte(Math.abs(intensities[index]));
         i++;
         }
      }

   public byte[] getCommand()
      {
      return command.clone();
      }
   }