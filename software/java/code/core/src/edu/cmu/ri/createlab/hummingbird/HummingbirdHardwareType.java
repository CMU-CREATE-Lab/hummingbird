package edu.cmu.ri.createlab.hummingbird;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>HummingbirdHardwareType</code> defines the various types of Hummingbird hardware.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum HummingbirdHardwareType
   {
      SERIAL("Serial"),
      HID("HID"),
      DUO("DUO");

   private static final Logger LOG = Logger.getLogger(HummingbirdHardwareType.class);

   /**
    * Returns the <code>HummingbirdHardwareType</code> associated with the given <code>name</code> (case insensitive),
    * or <code>null</code> if no such type exists.
    */
   public static HummingbirdHardwareType findByName(final String name)
      {
      if (name != null)
         {
         try
            {
            return HummingbirdHardwareType.valueOf(name.toUpperCase());
            }
         catch (IllegalArgumentException e)
            {
            LOG.debug("IllegalArgumentException while trying to find a HummingbirdHardwareType with name [" + name + "]", e);
            }
         }
      return null;
      }

   private final String name;

   HummingbirdHardwareType(final String name)
      {
      this.name = name;
      }

   public String getName()
      {
      return name;
      }

   @Override
   public String toString()
      {
      return getName();
      }
   }
