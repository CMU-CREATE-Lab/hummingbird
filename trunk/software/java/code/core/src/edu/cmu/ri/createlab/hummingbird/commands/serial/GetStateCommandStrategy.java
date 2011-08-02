package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.commands.GetStateCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetStateCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Hummingbird.HummingbirdState>
   {
   private final GetStateCommandStrategyHelper helper = new GetStateCommandStrategyHelper();

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return helper.getSizeOfExpectedResponse();
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }

   @Override
   public Hummingbird.HummingbirdState convertResponse(final SerialDeviceCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }