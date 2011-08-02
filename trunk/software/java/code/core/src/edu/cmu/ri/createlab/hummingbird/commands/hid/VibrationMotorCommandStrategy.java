package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.commands.VibrationMotorCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VibrationMotorCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final VibrationMotorCommandStrategyHelper helper;

   public VibrationMotorCommandStrategy(final int motorId, final int speed)
      {
      helper = new VibrationMotorCommandStrategyHelper(motorId, speed, HIDDeviceIndexConversionStrategy.getInstance());
      }

   public VibrationMotorCommandStrategy(final boolean[] motorMask, final int[] speeds)
      {
      helper = new VibrationMotorCommandStrategyHelper(motorMask, speeds, HIDDeviceIndexConversionStrategy.getInstance());
      }

   @Override
   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   @Override
   protected byte[] getCommand()
      {
      return helper.getCommand();
      }
   }