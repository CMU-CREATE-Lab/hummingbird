package edu.cmu.ri.createlab.hummingbird.commands;

import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GetAnalogInputCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy<Integer>
   {
   /** The command character used to request the value of one of the hummingbird's analog inputs. */
   private static final byte COMMAND_PREFIX = 's';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 1;

   private final byte[] command;

   public GetAnalogInputCommandStrategy(final int analogInputPortId)
      {
      if (analogInputPortId < 0 || analogInputPortId >= HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT)
         {
         throw new IllegalArgumentException("Invalid analog input port index");
         }

      // convert the port ID to an ASCII character (note that this implies that the greatest index possible is 9)
      final char analogInputPortIndex = String.valueOf(analogInputPortId).charAt(0);

      this.command = new byte[]{COMMAND_PREFIX,
                                (byte)analogInputPortIndex};
      }

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }

   @Override
   public Integer convertResponse(final SerialPortCommandResponse response)
      {
      if (response != null && response.wasSuccessful())
         {
         return ByteUtils.unsignedByteToInt(response.getData()[0]);
         }
      return null;
      }
   }