package edu.cmu.ri.createlab.hummingbird.commands;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EmergencyStopCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to trigger the emergency stop. */
   private static final byte COMMAND_PREFIX = 'X';

   private final byte[] command;

   public EmergencyStopCommandStrategy()
      {
      this.command = new byte[]{COMMAND_PREFIX};
      }

   @Override
   protected byte[] getCommand()
      {
      return command.clone();
      }
   }