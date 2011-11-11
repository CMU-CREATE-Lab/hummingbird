package edu.cmu.ri.createlab.hummingbird.commands;

/**
 * <p>
 * <code>BaseCommandStrategyHelper</code> provides common functionality for all command strategy helper classes.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseCommandStrategyHelper
   {
   protected final byte convertDeviceIndex(final int index)
      {
      return (byte)String.valueOf(index).charAt(0);
      }
   }
