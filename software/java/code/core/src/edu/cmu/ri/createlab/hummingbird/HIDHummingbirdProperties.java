package edu.cmu.ri.createlab.hummingbird;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDHummingbirdProperties extends BaseHIDHummingbirdProperties
   {
   private static final HummingbirdProperties INSTANCE = new HIDHummingbirdProperties();

   static HummingbirdProperties getInstance()
      {
      return INSTANCE;
      }

   // private to prevent instantiation
   private HIDHummingbirdProperties()
      {
      super("HID Hummingbird", HummingbirdHardwareType.HID);
      }
   }
