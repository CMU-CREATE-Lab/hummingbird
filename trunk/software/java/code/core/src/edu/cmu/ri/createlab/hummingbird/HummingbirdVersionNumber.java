package edu.cmu.ri.createlab.hummingbird;

import edu.cmu.ri.createlab.util.StandardVersionNumber;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HummingbirdVersionNumber extends StandardVersionNumber
   {
   public HummingbirdVersionNumber(final int majorVersion, final int minorVersion)
      {
      this(majorVersion, minorVersion, "");
      }

   public HummingbirdVersionNumber(final int majorVersion, final int minorVersion, final String revision)
      {
      super(String.valueOf(majorVersion), String.valueOf(minorVersion), (revision == null) ? "" : revision.trim());
      }

   public String getMajorMinorRevision()
      {
      return getMajorVersion() + "." + getMinorVersion() + getRevision();
      }

   @Override
   public String toString()
      {
      return getMajorMinorRevision();
      }
   }
