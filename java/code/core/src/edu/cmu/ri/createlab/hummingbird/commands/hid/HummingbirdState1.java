package edu.cmu.ri.createlab.hummingbird.commands.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdState1
   {
   /** Intensities of LEDs 1-3 only. For the intensity of LED 0, use {@link HummingbirdState0#getLed0Intensity()}. */
   int[] getLedIntensities();

   int[] getServoPositions();
   }