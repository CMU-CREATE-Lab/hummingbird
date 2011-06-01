package edu.cmu.ri.createlab.hummingbird.applications;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.SortedMap;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.hummingbird.HummingbirdFactory;
import edu.cmu.ri.createlab.hummingbird.services.HummingbirdService;
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

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class CommandLineHummingbird extends SerialDeviceCommandLineApplication
   {

   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineHummingbird(in).run();
      }

   private Hummingbird hummingbird;
   private ServiceManager serviceManager;

   private CreateLabDevicePingFailureEventListener pingFailureEventListener =
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

   private final Runnable scanAndConnectToDeviceAction =
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
               println("Scanning for a hummingbird...");
               hummingbird = HummingbirdFactory.create();
               hummingbird.addCreateLabDevicePingFailureEventListener(pingFailureEventListener);
               serviceManager = new HummingbirdServiceManager(hummingbird);
               println("Connection successful!");
               }
            }
         };

   private final Runnable connectToDeviceAction =
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
                        hummingbird = HummingbirdFactory.create(serialPortName);

                        if (hummingbird == null)
                           {
                           println("Connection failed!");
                           }
                        else
                           {
                           hummingbird.addCreateLabDevicePingFailureEventListener(pingFailureEventListener);
                           serviceManager = new HummingbirdServiceManager(hummingbird);
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
      registerAction("c", connectToDeviceAction);
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
                           final Integer analogInputId = readInteger("Analog input index [0 or 1]: ");

                           if (analogInputId == null || analogInputId < 0 || analogInputId > 1)
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

      registerAction("m",
                     new Runnable()
                     {
                     public void run()
                        {
                        if (isConnected())
                           {
                           final Integer motorId1 = readInteger("Motor index [0 or 1]: ");

                           if (motorId1 == null || motorId1 < 0 || motorId1 > 1)
                              {
                              println("Invalid motor index");
                              }
                           else
                              {
                              final Integer velocity = readInteger("Velocity [" + HummingbirdConstants.MOTOR_DEVICE_MIN_VELOCITY + " to " + HummingbirdConstants.MOTOR_DEVICE_MAX_VELOCITY + "]: ");
                              if (velocity == null || velocity < HummingbirdConstants.MOTOR_DEVICE_MIN_VELOCITY || velocity > HummingbirdConstants.MOTOR_DEVICE_MAX_VELOCITY)
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
                           final Integer motorId = readInteger("Vibration motor index [0 or 1]: ");

                           if (motorId == null || motorId < 0 || motorId > 1)
                              {
                              println("Invalid motor index");
                              }
                           else
                              {
                              final Integer speed = readInteger("Speed [" + HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MIN_SPEED + " - " + HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MAX_SPEED + "]: ");
                              if (speed == null || speed < HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MIN_SPEED || speed > HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MAX_SPEED)
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
                           final Integer servoId = readInteger("Servo index [0 - 3]: ");

                           if (servoId == null || servoId < 0 || servoId > 3)
                              {
                              println("Invalid servo index");
                              }
                           else
                              {
                              final Integer position = readInteger("Position [" + HummingbirdConstants.SIMPLE_SERVO_DEVICE_MIN_POSITION + " - " + HummingbirdConstants.SIMPLE_SERVO_DEVICE_MAX_POSITION + "]: ");
                              if (position == null || position < HummingbirdConstants.SIMPLE_SERVO_DEVICE_MIN_POSITION || position > HummingbirdConstants.SIMPLE_SERVO_DEVICE_MAX_POSITION)
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
                           final Integer ledId1 = readInteger("LED index [0 - 3]: ");

                           if (ledId1 == null || ledId1 < 0 || ledId1 > 3)
                              {
                              println("Invalid LED index");
                              }
                           else
                              {
                              final Integer intensity = readInteger("Intensity [" + HummingbirdConstants.SIMPLE_LED_DEVICE_MIN_INTENSITY + " - " + HummingbirdConstants.SIMPLE_LED_DEVICE_MAX_INTENSITY + "]: ");
                              if (intensity == null || intensity < HummingbirdConstants.SIMPLE_LED_DEVICE_MIN_INTENSITY || intensity > HummingbirdConstants.SIMPLE_LED_DEVICE_MAX_INTENSITY)
                                 {
                                 println("Invalid intensity");
                                 }
                              else
                                 {
                                 setLED(ledId1, intensity);
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
                           final Integer ledId = readInteger("Full-color LED index [0 or 1]: ");

                           if (ledId == null || ledId < 0 || ledId > 1)
                              {
                              println("Invalid full-color LED index");
                              }
                           else
                              {

                              final Integer r = readInteger("Red Intensity   [" + HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + " - " + HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
                              if (r == null || r < HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || r > HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                                 {
                                 println("Invalid red intensity");
                                 return;
                                 }
                              final Integer g = readInteger("Green Intensity [" + HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + " - " + HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
                              if (g == null || g < HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || g > HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                                 {
                                 println("Invalid green intensity");
                                 return;
                                 }
                              final Integer b = readInteger("Blue Intensity  [" + HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + " - " + HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
                              if (b == null || b < HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || b > HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
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
                           if (freq == null || freq < HummingbirdConstants.AUDIO_DEVICE_MIN_FREQUENCY || freq > HummingbirdConstants.AUDIO_DEVICE_MAX_FREQUENCY)
                              {
                              println("Invalid frequency");
                              return;
                              }
                           final Integer amp = readInteger("Amplitude: ");
                           if (amp == null || amp < HummingbirdConstants.AUDIO_DEVICE_MIN_AMPLITUDE || amp > HummingbirdConstants.AUDIO_DEVICE_MAX_AMPLITUDE)
                              {
                              println("Invalid amplitude");
                              return;
                              }
                           final Integer dur = readInteger("Duration (ms): ");
                           if (dur == null || dur < HummingbirdConstants.AUDIO_DEVICE_MIN_DURATION || dur > HummingbirdConstants.AUDIO_DEVICE_MAX_DURATION)
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
      println("C         Scan all serial ports and connect to the first hummingbird found");
      println("c         Connect to the hummingbird on the given serial port");
      println("d         Disconnect from the hummingbird");
      println("");
      println("g         Get the hummingbird's current state");
      println("a         Get the value of one of the analog inputs");
      println("m         Control a motor");
      println("v         Control a vibration motor");
      println("s         Control a servo motor");
      println("l         Control an LED");
      println("f         Control a full-color LED");
      println("t         Play a tone");
      println("p         Play a sound clip");
      println("");
      println("x         Turn all motors and LEDs off");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   protected final boolean isConnected()
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

   protected Hummingbird.HummingbirdState getState()
      {
      return ((HummingbirdService)serviceManager.getServiceByTypeId(HummingbirdService.TYPE_ID)).getHummingbirdState();
      }

   protected int getAnalogInputValue(final int analogInputId)
      {
      return ((AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID)).getAnalogInputValue(analogInputId);
      }

   protected void setMotorVelocity(final int motorId, final int velocity)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocity(motorId, velocity);
      }

   protected void setVibrationMotorSpeed(final int motorId, final int speed)
      {
      ((SpeedControllableMotorService)serviceManager.getServiceByTypeId(SpeedControllableMotorService.TYPE_ID)).setSpeed(motorId, speed);
      }

   protected void setServoPosition(final int servoId, final int position)
      {
      ((SimpleServoService)serviceManager.getServiceByTypeId(SimpleServoService.TYPE_ID)).setPosition(servoId, position);
      }

   protected void setLED(final int ledId, final int intensity)
      {
      ((SimpleLEDService)serviceManager.getServiceByTypeId(SimpleLEDService.TYPE_ID)).set(ledId, intensity);
      }

   protected void setFullColorLED(final int ledId, final int r, final int g, final int b)
      {
      ((FullColorLEDService)serviceManager.getServiceByTypeId(FullColorLEDService.TYPE_ID)).set(ledId, new Color(r, g, b));
      }

   protected void playTone(final int frequency, final int amplitude, final int duration)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playTone(frequency, amplitude, duration);
      }

   protected void playClip(final byte[] data)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playSound(data);
      }

   protected void emergencyStop()
      {
      ((HummingbirdService)serviceManager.getServiceByTypeId(HummingbirdService.TYPE_ID)).emergencyStop();
      }
   }
