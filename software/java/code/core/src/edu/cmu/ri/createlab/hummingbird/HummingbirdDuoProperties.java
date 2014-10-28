package edu.cmu.ri.createlab.hummingbird;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdDuoProperties extends BaseHIDHummingbirdProperties
   {
   private static final HummingbirdProperties INSTANCE = new HummingbirdDuoProperties();

   static HummingbirdProperties getInstance()
      {
      return INSTANCE;
      }

   // private to prevent instantiation
   private HummingbirdDuoProperties()
      {
      super("Hummingbird Duo", HummingbirdHardwareType.DUO);
      }
   }
