package edu.cmu.ri.createlab.hummingbird.commands.serial;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceHandshakeCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HandshakeCommandStrategy extends CreateLabSerialDeviceHandshakeCommandStrategy
   {
   /** The pattern of characters to look for in the hummingbird's startup mode "song" */
   private static final byte[] STARTUP_MODE_SONG_CHARACTERS = {'H', 'I', 0, 0};

   /** The pattern of characters to send to put the hummingbird into receive mode. */
   private static final byte[] RECEIVE_MODE_CHARACTERS = {'R', 'D'};

   @Override
   protected byte[] getReceiveModeCharacters()
      {
      return RECEIVE_MODE_CHARACTERS.clone();
      }

   @Override
   protected byte[] getStartupModeCharacters()
      {
      return STARTUP_MODE_SONG_CHARACTERS.clone();
      }
   }