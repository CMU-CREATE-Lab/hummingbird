package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.commands.EmergencyStopCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EmergencyStopCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   private final EmergencyStopCommandStrategyHelper helper = new EmergencyStopCommandStrategyHelper();

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }