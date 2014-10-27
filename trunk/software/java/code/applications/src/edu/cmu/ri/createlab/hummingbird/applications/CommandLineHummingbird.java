package edu.cmu.ri.createlab.hummingbird.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.SortedMap;
import edu.cmu.ri.createlab.CreateLabConstants;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdFactory;
import edu.cmu.ri.createlab.hummingbird.services.HummingbirdService;
import edu.cmu.ri.createlab.hummingbird.services.HummingbirdServiceFactoryHelper;
import edu.cmu.ri.createlab.hummingbird.services.HummingbirdServiceManager;
import edu.cmu.ri.createlab.serial.commandline.SerialDeviceCommandLineApplication;
import edu.cmu.ri.createlab.terk.services.ServiceManager;
import edu.cmu.ri.createlab.terk.services.analog.AnalogInputsService;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;
import edu.cmu.ri.createlab.terk.services.led.SimpleLEDService;
import edu.cmu.ri.createlab.terk.services.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.terk.services.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.terk.services.servo.SimpleServoService;
import edu.cmu.ri.createlab.util.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineHummingbird extends SerialDeviceCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(CommandLineHummingbird.class);
   private static final int THIRTY_SECONDS_IN_MILLIS = 30000;

   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineHummingbird(in).run();
      }

   private Hummingbird hummingbird;
   private ServiceManager serviceManager;
   private final HummingbirdServiceFactoryHelper hummingbirdServiceFactoryHelper =
         new HummingbirdServiceFactoryHelper()
         {
         @Override
         public File getAudioDirectory()
            {
            return CreateLabConstants.FilePaths.AUDIO_DIR;
            }
         };

   private final CreateLabDevicePingFailureEventListener pingFailureEventListener =
         new CreateLabDevicePingFailureEventListener()
         {
         public void handlePingFailureEvent()
            {
            println("Device ping failure detected.  You will need to reconnect.");
            hummingbird = null;
            serviceManager = null;
            }
         };

   private CommandLineHummingbird(final BufferedReader in)
      {
      super(in);

      registerActions();
      }

   private final Runnable enumeratePortsAction =
         new Runnable()
         {
         public void run()
            {
            enumeratePorts();
            }
         };

   private final class ScanAndConnectRunnable implements Runnable
      {
      private final boolean willCheckSerialPorts;

      private ScanAndConnectRunnable(final boolean willCheckSerialPorts)
         {
         this.willCheckSerialPorts = willCheckSerialPorts;
         }

      @Override
      public void run()
         {
         if (isConnected())
            {
            println("You are already connected to a Hummingbird.");
            }
         else
            {
            println(willCheckSerialPorts ? "Scanning USB and serial ports for a hummingbird..." : "Scanning USB ports for a hummingbird...");
            hummingbird = HummingbirdFactory.create(willCheckSerialPorts);
            if (hummingbird == null)
               {
               println("Connection failed.");
               }
            else
               {
               hummingbird.addCreateLabDevicePingFailureEventListener(pingFailureEventListener);
               serviceManager = new HummingbirdServiceManager(hummingbird, hummingbirdServiceFactoryHelper);
               println("Connection successful!");
               }
            }
         }
      }

   private final Runnable scanAndConnectToDeviceAction = new ScanAndConnectRunnable(true);
   private final Runnable scanUsbAndConnectToDeviceAction = new ScanAndConnectRunnable(false);

   private final Runnable connectToSerialDeviceAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println("You are already connected to a Hummingbird.");
               }
            else
               {
               final SortedMap<Integer, String> portMap = enumeratePorts();

               if (!portMap.isEmpty())
                  {
                  final Integer index = readInteger("Connect to port number: ");

                  if (index == null)
                     {
                     println("Invalid port");
                     }
                  else
                     {
                     final String serialPortName = portMap.get(index);

                     if (serialPortName != null)
                        {
                        hummingbird = HummingbirdFactory.createSerialHummingbird(serialPortName);

                        if (hummingbird == null)
                           {
                           println("Connection failed!");
                           }
                        else
                           {
                           hummingbird.addCreateLabDevicePingFailureEventListener(pingFailureEventListener);
                           serviceManager = new HummingbirdServiceManager(hummingbird, hummingbirdServiceFactoryHelper);
                           println("Connection successful!");
                           }
                        }
                     else
                        {
                        println("Invalid port");
                        }
                     }
                  }
               }
            }
         };

   private final Runnable connectToHIDDeviceAction =
         new Runnable()
         {
         public void run()
            {
            if (isConnected())
               {
               println("You are already connected to a Hummingbird.");
               }
            else
               {
               hummingbird = HummingbirdFactory.createHidHummingbird();

               if (hummingbird == null)
                  {
                  println("Connection failed!");
                  }
               else
                  {
                  hummingbird.addCreateLabDevicePingFailureEventListener(pingFailureEventListener);
                  serviceManager = new HummingbirdServiceManager(hummingbird, hummingbirdServiceFactoryHelper);
                  println("Connection successful!");
                  }
               }
            }
         };

   private final Runnable disconnectFromDeviceAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            }
         };

   private final Runnable quitAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            println("Bye!");
            }
         };

   private void registerActions()
      {
      registerAction("?", enumeratePortsAction);
      registerAction("C", scanAndConnectToDeviceAction);
      registerAction("U", scanUsbAndConnectToDeviceAction);
      registerAction("c", connectToSerialDeviceAction);
      registerAction("u", connectToHIDDeviceAction);
      registerAction("d", disconnectFromDeviceAction);

      registerAction("g",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           println(getState());
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("a",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer analogInputId = readInteger("Analog input index [0 - " + (hummingbird.getHummingbirdProperties().getAnalogInputDeviceCount() - 1) + "]: ");

                           if (analogInputId == null || analogInputId < 0 || analogInputId >= hummingbird.getHummingbirdProperties().getAnalogInputDeviceCount())
                              {
                              println("Invalid analog input index");
                              }
                           else
                              {
                              println(getAnalogInputValue(analogInputId));
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("A",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           poll(
                                 new Runnable()
                                 {
                                 public void run()
                                    {
                                    final int[] analogInputValues = getAnalogInputValues();
                                    println("Analog inputs: " + arrayToFormattedString(analogInputValues));
                                    }
                                 });
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("m",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer motorId1 = readInteger("Motor index [0 - " + (hummingbird.getHummingbirdProperties().getMotorDeviceCount() - 1) + "]: ");

                           if (motorId1 == null || motorId1 < 0 || motorId1 >= hummingbird.getHummingbirdProperties().getMotorDeviceCount())
                              {
                              println("Invalid motor index");
                              }
                           else
                              {
                              final int minVelocity = hummingbird.getHummingbirdProperties().getMotorDeviceMinVelocity();
                              final int maxVelocity = hummingbird.getHummingbirdProperties().getMotorDeviceMaxVelocity();
                              final Integer velocity = readInteger("Velocity [" + minVelocity + " to " + maxVelocity + "]: ");
                              if (velocity == null || velocity < minVelocity || velocity > maxVelocity)
                                 {
                                 println("Invalid velocity");
                                 }
                              else
                                 {
                                 setMotorVelocity(motorId1, velocity);
                                 }
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("v",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer motorId = readInteger("Vibration motor index [0 - " + (hummingbird.getHummingbirdProperties().getVibrationMotorDeviceCount() - 1) + "]: ");

                           if (motorId == null || motorId < 0 || motorId >= hummingbird.getHummingbirdProperties().getVibrationMotorDeviceCount())
                              {
                              println("Invalid motor index");
                              }
                           else
                              {
                              final int minSpeed = hummingbird.getHummingbirdProperties().getVibrationMotorDeviceMinSpeed();
                              final int maxSpeed = hummingbird.getHummingbirdProperties().getVibrationMotorDeviceMaxSpeed();
                              final int maxSafeSpeed = hummingbird.getHummingbirdProperties().getVibrationMotorDeviceMaxSafeSpeed();

                              final Integer speed = readInteger("Speed [" + minSpeed + " - " + maxSpeed + "], max safe speed is " + maxSafeSpeed + ": ");
                              if (speed == null || speed < minSpeed || speed > maxSpeed)
                                 {
                                 println("Invalid speed");
                                 }
                              else
                                 {
                                 setVibrationMotorSpeed(motorId, speed);
                                 }
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("s",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer servoId = readInteger("Servo index [0 - " + (hummingbird.getHummingbirdProperties().getSimpleServoDeviceCount() - 1) + "]: ");

                           if (servoId == null || servoId < 0 || servoId >= hummingbird.getHummingbirdProperties().getSimpleServoDeviceCount())
                              {
                              println("Invalid servo index");
                              }
                           else
                              {
                              final int minPosition = hummingbird.getHummingbirdProperties().getSimpleServoDeviceMinPosition();
                              final int maxPosition = hummingbird.getHummingbirdProperties().getSimpleServoDeviceMaxPosition();
                              final int minSafePosition = hummingbird.getHummingbirdProperties().getSimpleServoDeviceMinSafePosition();
                              final int maxSafePosition = hummingbird.getHummingbirdProperties().getSimpleServoDeviceMaxSafePosition();
                              final Integer position = readInteger("Position [" + minPosition + " - " + maxPosition + "], safe range is [" + minSafePosition + " - " + maxSafePosition + "]: ");
                              if (position == null || position < minPosition || position > maxPosition)
                                 {
                                 println("Invalid position");
                                 }
                              else
                                 {
                                 setServoPosition(servoId, position);
                                 }
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("l",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer ledId = readInteger("LED index [0 - " + (hummingbird.getHummingbirdProperties().getSimpleLedDeviceCount() - 1) + "]: ");

                           if (ledId == null || ledId < 0 || ledId >= hummingbird.getHummingbirdProperties().getSimpleLedDeviceCount())
                              {
                              println("Invalid LED index");
                              }
                           else
                              {
                              final int minIntensity = hummingbird.getHummingbirdProperties().getSimpleLedDeviceMinIntensity();
                              final int maxIntensity = hummingbird.getHummingbirdProperties().getSimpleLedDeviceMaxIntensity();
                              final Integer intensity = readInteger("Intensity [" + minIntensity + " - " + maxIntensity + "]: ");
                              if (intensity == null || intensity < minIntensity || intensity > maxIntensity)
                                 {
                                 println("Invalid intensity");
                                 }
                              else
                                 {
                                 setLED(ledId, intensity);
                                 }
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("f",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer ledId = readInteger("Full-color LED index [0 - " + (hummingbird.getHummingbirdProperties().getFullColorLedDeviceCount() - 1) + "]: ");

                           if (ledId == null || ledId < 0 || ledId >= hummingbird.getHummingbirdProperties().getFullColorLedDeviceCount())
                              {
                              println("Invalid full-color LED index");
                              }
                           else
                              {
                              final int minIntensity = hummingbird.getHummingbirdProperties().getFullColorLedDeviceMinIntensity();
                              final int maxIntensity = hummingbird.getHummingbirdProperties().getFullColorLedDeviceMaxIntensity();

                              final Integer r = readInteger("Red Intensity   [" + minIntensity + " - " + maxIntensity + "]: ");
                              if (r == null || r < minIntensity || r > maxIntensity)
                                 {
                                 println("Invalid red intensity");
                                 return;
                                 }
                              final Integer g = readInteger("Green Intensity [" + minIntensity + " - " + maxIntensity + "]: ");
                              if (g == null || g < minIntensity || g > maxIntensity)
                                 {
                                 println("Invalid green intensity");
                                 return;
                                 }
                              final Integer b = readInteger("Blue Intensity  [" + minIntensity + " - " + maxIntensity + "]: ");
                              if (b == null || b < minIntensity || b > maxIntensity)
                                 {
                                 println("Invalid blue intensity");
                                 return;
                                 }

                              setFullColorLED(ledId, r, g, b);
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("t",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer freq = readInteger("Frequency: ");
                           if (freq == null || freq < hummingbird.getHummingbirdProperties().getAudioDeviceMinFrequency() || freq > hummingbird.getHummingbirdProperties().getAudioDeviceMaxFrequency())
                              {
                              println("Invalid frequency");
                              return;
                              }
                           final Integer amp = readInteger("Amplitude: ");
                           if (amp == null || amp < hummingbird.getHummingbirdProperties().getAudioDeviceMinAmplitude() || amp > hummingbird.getHummingbirdProperties().getAudioDeviceMaxAmplitude())
                              {
                              println("Invalid amplitude");
                              return;
                              }
                           final Integer dur = readInteger("Duration (ms): ");
                           if (dur == null || dur < hummingbird.getHummingbirdProperties().getAudioDeviceMinDuration() || dur > hummingbird.getHummingbirdProperties().getAudioDeviceMaxDuration())
                              {
                              println("Invalid duration");
                              return;
                              }

                           playTone(freq, amp, dur);
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("p",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final String filePath = readString("Absolute path to sound file: ");
                           if (filePath == null || filePath.length() == 0)
                              {
                              println("Invalid path");
                              return;
                              }

                           final File file = new File(filePath);
                           if (file.exists() && file.isFile())
                              {
                              try
                                 {
                                 playClip(FileUtils.getFileAsBytes(file));
                                 }
                              catch (IOException e)
                                 {
                                 final String msg = "Error reading sound file (" + e.getMessage() + ")";
                                 println(msg);
                                 }
                              }
                           else
                              {
                              println("Invalid path");
                              }
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("k",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final String whatToSay = readString("Text to speak: ");
                           if (whatToSay == null || whatToSay.length() == 0)
                              {
                              println("Text to speak cannot be empty");
                              return;
                              }

                           speak(whatToSay);
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("z",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           println("Hardware common name:  " + hummingbird.getHummingbirdProperties().getDeviceCommonName());
                           println("Hardware type:         " + hummingbird.getHummingbirdProperties().getHardwareType());
                           println("Hardware version:      " + hummingbird.getHardwareVersion().getMajorMinorRevision());
                           println("Firmware version:      " + hummingbird.getFirmwareVersion().getMajorMinorRevision());
                           println("Device counts:");
                           println("   Analog Inputs:      " + hummingbird.getHummingbirdProperties().getAnalogInputDeviceCount());
                           println("   Audio Outputs:      " + hummingbird.getHummingbirdProperties().getAudioDeviceCount());
                           println("   LEDs:               " + hummingbird.getHummingbirdProperties().getSimpleLedDeviceCount());
                           println("   Full-color LEDs:    " + hummingbird.getHummingbirdProperties().getFullColorLedDeviceCount());
                           println("   Motors:             " + hummingbird.getHummingbirdProperties().getMotorDeviceCount());
                           println("   Servos:             " + hummingbird.getHummingbirdProperties().getSimpleServoDeviceCount());
                           println("   Vibration Motors:   " + hummingbird.getHummingbirdProperties().getVibrationMotorDeviceCount());
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("w",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           println("Motor/Servo power enabled: " + hummingbird.isMotorPowerEnabled());
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction("x",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           emergencyStop();
                           }
                        else
                           {
                           println("You must be connected to a hummingbird first.");
                           }
                        }
                     });

      registerAction(QUIT_COMMAND, quitAction);
      }

   protected final void menu()
      {
      println("COMMANDS -----------------------------------");
      println("");
      println("?         List all available serial ports");
      println("");
      println("C         Repeatedly scan all USB and serial ports until a connection to a hummingbird is established");
      println("U         Repeatedly scan only USB ports until a connection to a hummingbird is established");
      println("c         Connect to a serial hummingbird on the given serial port");
      println("u         Connect to a USB HID hummingbird");
      println("d         Disconnect from the hummingbird");
      println("");
      println("g         Get the hummingbird's current state");
      println("a         Get the value of one of the analog inputs");
      println("A         Continuously poll the analog inputs for 30 seconds");
      println("m         Control a motor");
      println("v         Control a vibration motor");
      println("s         Control a servo motor");
      println("l         Control an LED");
      println("f         Control a full-color LED");
      println("t         Play a tone");
      println("p         Play a sound clip");
      println("k         Convert text to speech and then speak it");
      println("z         Display Hummingbird hardware and firmware info");
      println("w         Returns whether motor/servo power is enabled");
      println("");
      println("x         Turn all motors and LEDs off");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   @SuppressWarnings({"BusyWait"})
   private void poll(final Runnable strategy)
      {
      final long startTime = System.currentTimeMillis();
      while (isConnected() && System.currentTimeMillis() - startTime < THIRTY_SECONDS_IN_MILLIS)
         {
         strategy.run();
         try
            {
            Thread.sleep(30);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }
      }

   private static String arrayToFormattedString(final int[] a)
      {
      if (a != null && a.length > 0)
         {
         final StringBuilder s = new StringBuilder();
         for (final int i : a)
            {
            s.append(String.format("%5d", i));
            }
         return s.toString();
         }
      return "";
      }

   private boolean isConnected()
      {
      return hummingbird != null;
      }

   protected final void disconnect()
      {
      if (isConnected())
         {
         hummingbird.disconnect();
         hummingbird = null;
         serviceManager = null;
         }
      }

   private Hummingbird.HummingbirdState getState()
      {
      return ((HummingbirdService)serviceManager.getServiceByTypeId(HummingbirdService.TYPE_ID)).getHummingbirdState();
      }

   private int getAnalogInputValue(final int analogInputId)
      {
      return ((AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID)).getAnalogInputValue(analogInputId);
      }

   private int[] getAnalogInputValues()
      {
      return ((AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID)).getAnalogInputValues();
      }

   private void setMotorVelocity(final int motorId, final int velocity)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocity(motorId, velocity);
      }

   private void setVibrationMotorSpeed(final int motorId, final int speed)
      {
      ((SpeedControllableMotorService)serviceManager.getServiceByTypeId(SpeedControllableMotorService.TYPE_ID)).setSpeed(motorId, speed);
      }

   private void setServoPosition(final int servoId, final int position)
      {
      ((SimpleServoService)serviceManager.getServiceByTypeId(SimpleServoService.TYPE_ID)).setPosition(servoId, position);
      }

   private void setLED(final int ledId, final int intensity)
      {
      ((SimpleLEDService)serviceManager.getServiceByTypeId(SimpleLEDService.TYPE_ID)).set(ledId, intensity);
      }

   private void setFullColorLED(final int ledId, final int r, final int g, final int b)
      {
      ((FullColorLEDService)serviceManager.getServiceByTypeId(FullColorLEDService.TYPE_ID)).set(ledId, new Color(r, g, b));
      }

   private void playTone(final int frequency, final int amplitude, final int duration)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playTone(frequency, amplitude, duration);
      }

   private void playClip(final byte[] data)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playSound(data);
      }

   private void speak(final String whatToSay)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).speak(whatToSay);
      }

   private void emergencyStop()
      {
      ((HummingbirdService)serviceManager.getServiceByTypeId(HummingbirdService.TYPE_ID)).emergencyStop();
      }
   }
