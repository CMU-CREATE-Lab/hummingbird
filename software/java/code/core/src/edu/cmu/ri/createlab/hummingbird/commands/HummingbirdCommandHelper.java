package edu.cmu.ri.createlab.hummingbird.commands;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdCommandHelper
   {
   /** Converts the given number to an ASCII character byte (note that this implies that the greatest index possible is 9). */
   static byte convertDeviceIndexToASCII(final int index)
      {
      return (byte)String.valueOf(index).charAt(0);
      }

   private HummingbirdCommandHelper()
      {
      // private to prevent instantiation
      }
   }
