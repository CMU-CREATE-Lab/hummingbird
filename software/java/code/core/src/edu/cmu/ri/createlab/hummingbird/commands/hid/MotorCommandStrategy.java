package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.commands.MotorCommandStrategyHelper;
import edu.cmu.ri.createlab.usb.hid.CreateLabHIDCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class MotorCommandStrategy extends CreateLabHIDCommandStrategy
   {
   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 0;

   private final MotorCommandStrategyHelper helper;

   public MotorCommandStrategy(final int motorId, final int velocity)
      {
      helper = new MotorCommandStrategyHelper(motorId, velocity, HIDDeviceIndexConversionStrategy.getInstance());
      }

   public MotorCommandStrategy(final boolean[] motorMask, final int[] velocities)
      {
      helper = new MotorCommandStrategyHelper(motorMask, velocities, HIDDeviceIndexConversionStrategy.getInstance());
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