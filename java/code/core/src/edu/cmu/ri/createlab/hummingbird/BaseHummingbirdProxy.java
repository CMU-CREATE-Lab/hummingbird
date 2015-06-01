package edu.cmu.ri.createlab.hummingbird;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.audio.AudioHelper;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.speech.Mouth;
import edu.cmu.ri.createlab.usb.hid.HIDDeviceNotConnectedException;
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseHummingbirdProxy implements Hummingbird, CommandExecutionFailureHandler
   {
   private static final Logger LOG = Logger.getLogger(BaseHummingbirdProxy.class);

   private static final int DELAY_IN_MILLISECONDS_BETWEEN_PEER_PINGS = 2000;  // 2 seconds

   private final Pinger pinger = new Pinger();
   private final ScheduledExecutorService pingExecutorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BaseHummingbirdProxy.pingExecutorService"));
   private final ScheduledFuture<?> pingScheduledFuture;
   private final Collection<CreateLabDevicePingFailureEventListener> createLabDevicePingFailureEventListeners = new HashSet<CreateLabDevicePingFailureEventListener>();

   BaseHummingbirdProxy()
      {
      this(DELAY_IN_MILLISECONDS_BETWEEN_PEER_PINGS);
      }

   BaseHummingbirdProxy(final int delayInMillisecondsBetweenPeerPings)
      {
      // schedule periodic pings
      pingScheduledFuture = pingExecutorService.scheduleAtFixedRate(pinger,
                                                                    delayInMillisecondsBetweenPeerPings, // delay before first ping
                                                                    delayInMillisecondsBetweenPeerPings, // delay between pings
                                                                    TimeUnit.MILLISECONDS);
      }

   @Override
   public final void handleExecutionFailure()
      {
      pinger.forceFailure();
      }

   @Override
   public final void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.add(listener);
         }
      }

   @Override
   public final void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         createLabDevicePingFailureEventListeners.remove(listener);
         }
      }

   @Override
   public final void playTone(final int frequency, final int amplitude, final int duration)
      {
      AudioHelper.playTone(frequency, amplitude, duration);
      }

   @Override
   public final void playClip(final byte[] data)
      {
      AudioHelper.playClip(data);
      }

   @Override
   public final byte[] getSpeech(final String whatToSay)
      {
      if (whatToSay != null && whatToSay.length() > 0)
         {
         final Mouth mouth = Mouth.getInstance();

         if (mouth != null)
            {
            return mouth.getSpeech(whatToSay);
            }
         }
      return null;
      }

   @Override
   public final void speak(final String whatToSay)
      {
      final byte[] speechAudio = getSpeech(whatToSay);
      if (speechAudio != null)
         {
         // play it this way since Mouth.speak() is deprecated
         AudioHelper.playClip(speechAudio);
         }
      }

   @Override
   public final void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("BaseHummingbirdProxy.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // turn off the pinger
      try
         {
         pingScheduledFuture.cancel(false);
         pingExecutorService.shutdownNow();
         LOG.debug("BaseHummingbirdProxy.disconnect(): Successfully shut down the Hummingbird pinger.");
         }
      catch (Exception e)
         {
         LOG.error("BaseHummingbirdProxy.disconnect(): Exception caught while trying to shut down pinger", e);
         }

      // optionally send goodbye command to the Hummingbird
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("BaseHummingbirdProxy.disconnect(): Now attempting to send the disconnect command to the Hummingbird");
         try
            {
            if (disconnectAndReturnStatus())
               {
               LOG.debug("BaseHummingbirdProxy.disconnect(): Successfully disconnected from the Hummingbird.");
               }
            else
               {
               LOG.error("BaseHummingbirdProxy.disconnect(): Failed to disconnect from the Hummingbird.");
               }
            }
         catch (Exception e)
            {
            LOG.error("Exception caught while trying to execute the disconnect", e);
            }
         }

      // shut down the command queue, which closes the connection to the device
      try
         {
         LOG.debug("BaseHummingbirdProxy.disconnect(): shutting down the CommandExecutionQueue...");
         shutdownCommandQueue();
         LOG.debug("BaseHummingbirdProxy.disconnect(): done shutting down the CommandExecutionQueue");
         }
      catch (Exception e)
         {
         LOG.error("BaseHummingbirdProxy.disconnect(): Exception while trying to shut down the CommandExecutionQueue", e);
         }
      }

   protected abstract boolean disconnectAndReturnStatus() throws Exception;

   protected abstract void shutdownCommandQueue();

   private class Pinger implements Runnable
      {
      public void run()
         {
         try
            {
            // ping the device
            final CommandResponse response = executePingCommand();
            final boolean pingSuccessful = (response != null) && response.wasSuccessful();

            // if the ping failed, then we know we have a problem, so disconnect (which
            // probably won't work) and then notify the listeners
            if (!pingSuccessful)
               {
               handlePingFailure();
               }
            }
         catch (HIDDeviceNotConnectedException e)
            {
            LOG.error("BaseHummingbirdProxy$Pinger.run(): HIDDeviceNotConnectedException caught while executing the pinger", e);
            handlePingFailure();
            }
         catch (Exception e)
            {
            LOG.error("BaseHummingbirdProxy$Pinger.run(): Exception caught while executing the pinger", e);
            }
         }

      private void handlePingFailure()
         {
         try
            {
            LOG.debug("BaseHummingbirdProxy$Pinger.handlePingFailure(): Peer ping failed.  Attempting to disconnect...");
            disconnect(false);
            LOG.debug("BaseHummingbirdProxy$Pinger.handlePingFailure(): Done disconnecting from the Hummingbird");
            }
         catch (Exception e)
            {
            LOG.error("BaseHummingbirdProxy$Pinger.handlePingFailure(): Exeption caught while trying to disconnect from the Hummingbird", e);
            }

         if (LOG.isDebugEnabled())
            {
            LOG.debug("BaseHummingbirdProxy$Pinger.handlePingFailure(): Notifying " + createLabDevicePingFailureEventListeners.size() + " listeners of ping failure...");
            }
         for (final CreateLabDevicePingFailureEventListener listener : createLabDevicePingFailureEventListeners)
            {
            try
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("   BaseHummingbirdProxy$Pinger.handlePingFailure(): Notifying " + listener);
                  }
               listener.handlePingFailureEvent();
               }
            catch (Exception e)
               {
               LOG.error("BaseHummingbirdProxy$Pinger.handlePingFailure(): Exeption caught while notifying CreateLabDevicePingFailureEventListener", e);
               }
            }
         }

      private void forceFailure()
         {
         handlePingFailure();
         }
      }

   protected abstract CommandResponse executePingCommand() throws Exception;
   }
