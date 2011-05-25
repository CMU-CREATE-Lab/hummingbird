package edu.cmu.ri.createlab.hummingbird.services;

import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.terk.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdService extends Service
   {
   String TYPE_ID = "::TeRK::hummingbird::HummingbirdService";

   /** Returns the hummingbird's current state. */
   Hummingbird.HummingbirdState getHummingbirdState();

   /** Sets all motors, vibration motors, and LEDs to off. */
   void emergencyStop();
   }