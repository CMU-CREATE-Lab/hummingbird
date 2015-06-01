package edu.cmu.ri.createlab.hummingbird.applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdFactory;

/**
 * <p>
 * <code>BinaryCounter</code> is a simple demo that counts in binary from 0 to 15 using the 4 LEDs.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class BinaryCounter
   {
   private static final int[] ON = new int[]{255, 255, 255, 255};
   private static final int[] OFF = new int[]{0, 0, 0, 0};
   private static final boolean[] ALL_LEDS = new boolean[]{true, true, true, true};

   // yeah, yeah...there are more elegant ways of doing this, but for only 16 states this was trivially easy...
   private static final boolean[][] MASKS = new boolean[][]{
         {false, false, false, false},
         {false, false, false, true},
         {false, false, true, false},
         {false, false, true, true},
         {false, true, false, false},
         {false, true, false, true},
         {false, true, true, false},
         {false, true, true, true},
         {true, false, false, false},
         {true, false, false, true},
         {true, false, true, false},
         {true, false, true, true},
         {true, true, false, false},
         {true, true, false, true},
         {true, true, true, false},
         {true, true, true, true},
   };

   public static void main(final String[] args) throws IOException
      {
      final Hummingbird hummingbird = HummingbirdFactory.create();

      System.out.println("");
      System.out.println("Press ENTER to quit.");

      int i = 0;
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         hummingbird.setLEDs(ALL_LEDS, OFF);
         hummingbird.setLEDs(MASKS[i], ON);
         i++;
         if (i >= MASKS.length)
            {
            i = 0;
            }

         sleep();
         }

      hummingbird.disconnect();
      }

   private static void sleep()
      {
      try
         {
         Thread.sleep(1000);
         }
      catch (InterruptedException e)
         {
         System.err.println("InterruptedException while sleeping!");
         }
      }

   private BinaryCounter()
      {
      // private to prevent instantiation
      }
   }
