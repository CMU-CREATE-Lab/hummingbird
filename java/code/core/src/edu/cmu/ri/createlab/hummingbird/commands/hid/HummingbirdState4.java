package edu.cmu.ri.createlab.hummingbird.commands.hid;

import edu.cmu.ri.createlab.hummingbird.HummingbirdVersionNumber;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdState4
   {
   HummingbirdVersionNumber getHardwareVersion();

   HummingbirdVersionNumber getFirmwareVersion();
   }