package edu.cmu.ri.createlab.hummingbird.services;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.terk.services.Service;
import edu.cmu.ri.createlab.terk.services.ServiceCreator;
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

   private final Map<String, ServiceCreator<Hummingbird>> typeIdToServiceCreatorsMap = new HashMap<String, ServiceCreator<Hummingbird>>();

   public HummingbirdServiceFactory(final HummingbirdServiceFactoryHelper hummingbirdServiceFactoryHelper)
      {
      typeIdToServiceCreatorsMap.put(AnalogInputsService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return AnalogInputsServiceImpl.create(hummingbird);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return AudioServiceImpl.create(hummingbird, hummingbirdServiceFactoryHelper.getAudioDirectory());
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return FullColorLEDServiceImpl.create(hummingbird);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(HummingbirdService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return HummingbirdServiceImpl.create(hummingbird);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleLEDService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return SimpleLEDServiceImpl.create(hummingbird);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleServoService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return SimpleServoServiceImpl.create(hummingbird);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SpeedControllableMotorService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return SpeedControllableMotorServiceImpl.create(hummingbird);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(VelocityControllableMotorService.TYPE_ID,
                                     new ServiceCreator<Hummingbird>()
                                     {
                                     public Service createService(final Hummingbird hummingbird)
                                        {
                                        return VelocityControllableMotorServiceImpl.create(hummingbird);
                                        }
                                     });
      }

   public Service createService(final String serviceTypeId, final Hummingbird hummingbird)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HummingbirdServiceFactory.createService(" + serviceTypeId + ")");
            }
         return typeIdToServiceCreatorsMap.get(serviceTypeId).createService(hummingbird);
         }
      return null;
      }
   }
