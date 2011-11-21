package edu.cmu.ri.createlab.hummingbird.commands.hid;

import java.awt.Color;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdState0
   {
   Color[] getFullColorLEDs();

   int getLed0Intensity();
   }