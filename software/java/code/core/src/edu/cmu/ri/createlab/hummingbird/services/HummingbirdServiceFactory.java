package edu.cmu.ri.createlab.hummingbird.services;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.hummingbird.HummingbirdProxy;
import edu.cmu.ri.createlab.terk.services.Service;
import edu.cmu.ri.createlab.terk.services.analog.AnalogInputsService;
import edu.cmu.ri.createlab.terk.services.audio.AudioService;
import edu.cmu.ri.createlab.terk.services.led.FullColorLEDService;
import edu.cmu.ri.createlab.terk.services.led.SimpleLEDService;
import edu.cmu.ri.createlab.terk.services.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.terk.services.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.terk.services.servo.SimpleServoService;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdServiceFactory
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdServiceFactory.class);

   private final Map<String, HummingbirdServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, HummingbirdServiceCreator>();

   public HummingbirdServiceFactory()
      {
      typeIdToServiceCreatorsMap.put(AnalogInputsService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return AnalogInputsServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return AudioServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return FullColorLEDServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(HummingbirdService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return HummingbirdServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleLEDService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return SimpleLEDServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleServoService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return SimpleServoServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SpeedControllableMotorService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return SpeedControllableMotorServiceImpl.create(proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(VelocityControllableMotorService.TYPE_ID,
                                     new HummingbirdServiceCreator()
                                     {
                                     public Service create(final HummingbirdProxy proxy)
                                        {
                                        return VelocityControllableMotorServiceImpl.create(proxy);
                                        }
                                     });
      }

   public Service createService(final String serviceTypeId, final HummingbirdProxy proxy)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HummingbirdServiceFactory.createService(" + serviceTypeId + ")");
            }
         return typeIdToServiceCreatorsMap.get(serviceTypeId).create(proxy);
         }
      return null;
      }
   }
