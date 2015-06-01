package edu.cmu.ri.createlab.hummingbird.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import edu.cmu.ri.createlab.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.terk.services.AbstractServiceManager;
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
public class HummingbirdServiceManager extends AbstractServiceManager
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdServiceManager.class);

   private final Hummingbird hummingbird;
   private final HummingbirdServiceFactory serviceFactory;
   private final Map<String, Service> loadedServices = Collections.synchronizedMap(new HashMap<String, Service>());

   public HummingbirdServiceManager(final Hummingbird hummingbird, final HummingbirdServiceFactoryHelper hummingbirdServiceFactoryHelper)
      {
      if (hummingbird == null)
         {
         throw new IllegalArgumentException("HummingbirdProxy may not be null");
         }
      if (hummingbirdServiceFactoryHelper == null)
         {
         throw new IllegalArgumentException("HummingbirdServiceFactoryHelper may not be null");
         }

      this.hummingbird = hummingbird;
      this.serviceFactory = new HummingbirdServiceFactory(hummingbirdServiceFactoryHelper);

      // get the collection of supported services from the hummingbird
      final Set<String> supportedServices = new HashSet<String>();
      supportedServices.add(AnalogInputsService.TYPE_ID);
      supportedServices.add(AudioService.TYPE_ID);
      supportedServices.add(FullColorLEDService.TYPE_ID);
      supportedServices.add(HummingbirdService.TYPE_ID);
      supportedServices.add(SimpleLEDService.TYPE_ID);
      supportedServices.add(SimpleServoService.TYPE_ID);
      supportedServices.add(SpeedControllableMotorService.TYPE_ID);
      supportedServices.add(VelocityControllableMotorService.TYPE_ID);

      // register the supported services with the superclass
      registerSupportedServices(supportedServices);
      }

   protected final Service loadService(final String typeId)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("HummingbirdServiceManager.loadService(" + typeId + ")");
         }

      if (serviceFactory != null)
         {
         Service service;

         synchronized (loadedServices)
            {
            // see whether we've already loaded this service
            service = loadedServices.get(typeId);

            // load the service
            if (service == null)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("HummingbirdServiceManager.loadService() needs to load the [" + typeId + "] service");
                  }

               service = serviceFactory.createService(typeId, hummingbird);

               // cache this service so future calls won't have to create it
               loadedServices.put(typeId, service);
               }
            }

         return service;
         }

      return null;
      }
   }

