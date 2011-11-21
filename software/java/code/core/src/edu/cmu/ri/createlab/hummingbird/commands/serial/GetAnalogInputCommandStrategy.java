package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.hummingbird.commands.AnalogInputCommandStrategyHelper;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialDeviceCommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAnalogInputCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Integer>
   {
   private final AnalogInputCommandStrategyHelper helper;

   public GetAnalogInputCommandStrategy(final int analogInputPortId, final HummingbirdProperties hummingbirdProperties)
      {
      this.helper = new AnalogInputCommandStrategyHelper(analogInputPortId, hummingbirdProperties);
      }

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return AnalogInputCommandStrategyHelper.SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }

   @Override
   public Integer convertResponse(final SerialDeviceCommandResponse response)
      {
      return helper.convertResponse(response);
      }
   }