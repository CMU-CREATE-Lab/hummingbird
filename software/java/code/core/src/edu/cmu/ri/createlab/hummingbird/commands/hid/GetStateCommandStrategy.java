package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.commands.GetStateCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDReturnValueCommandStrategy;
import edu.cmu.ri.createlab.usb.hid.HIDCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetStateCommandStrategy extends CreateLabHIDReturnValueCommandStrategy<Hummingbird.HummingbirdState>
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
   public Hummingbird.HummingbirdState convertResponse(final HIDCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }