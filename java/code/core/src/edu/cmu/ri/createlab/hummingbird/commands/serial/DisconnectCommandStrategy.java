package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.commands.DisconnectCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DisconnectCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final DisconnectCommandStrategyHelper helper = new DisconnectCommandStrategyHelper();

   @Override
   public byte[] getCommand()
      {
      return helper.getCommand();
      }
   }
