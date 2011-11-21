package edu.cmu.ri.createlab.hummingbird.services;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProperties;
import edu.cmu.ri.createlab.terk.TerkConstants;
import edu.cmu.ri.createlab.terk.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.terk.properties.PropertyManager;
import edu.cmu.ri.createlab.terk.services.ExceptionHandler;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.audio.BaseAudioServiceImpl;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AudioServiceImpl extends BaseAudioServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(AudioServiceImpl.class);

   static AudioServiceImpl create(final Hummingbird hummingbird, final File audioDirectory)
      {
      final HummingbirdProperties hummingbirdProperties = hummingbird.getHummingbirdProperties();

      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, hummingbirdProperties.getAudioDeviceCount());
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_AMPLITUDE, hummingbirdProperties.getAudioDeviceMinAmplitude());
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_AMPLITUDE, hummingbirdProperties.getAudioDeviceMaxAmplitude());
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_DURATION, hummingbirdProperties.getAudioDeviceMinDuration());
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_DURATION, hummingbirdProperties.getAudioDeviceMaxDuration());
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_FREQUENCY, hummingbirdProperties.getAudioDeviceMinFrequency());
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_FREQUENCY, hummingbirdProperties.getAudioDeviceMaxFrequency());

      return new AudioServiceImpl(hummingbird,
                                  basicPropertyManager,
                                  audioDirectory);
      }

   private final Hummingbird hummingbird;
   private final Executor executor = Executors.newCachedThreadPool();

   private AudioServiceImpl(final Hummingbird hummingbird,
                            final PropertyManager propertyManager,
                            final File audioDirectory)
      {
      super(propertyManager, audioDirectory);
      this.hummingbird = hummingbird;
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      hummingbird.playTone(frequency, amplitude, duration);
      }

   public void playSound(final byte[] sound)
      {
      hummingbird.playClip(sound);
      }

   public void playToneAsynchronously(final int frequency, final int amplitude, final int duration, final ExceptionHandler callback)
      {
      try
         {
         executor.execute(
               new Runnable()
               {
               public void run()
                  {
                  hummingbird.playTone(frequency, amplitude, duration);
                  }
               });
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to play the tone asynchronously", e);
         callback.handleException(e);
         }
      }

   public void playSoundAsynchronously(final byte[] sound, final ExceptionHandler callback)
      {
      try
         {
         executor.execute(
               new Runnable()
               {
               public void run()
                  {
                  hummingbird.playClip(sound);
                  }
               });
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to play the clip asynchronously", e);
         callback.handleException(e);
         }
      }
   }