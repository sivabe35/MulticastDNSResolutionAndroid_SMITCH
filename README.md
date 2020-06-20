# MulticastDNSResolutionAndroid_SMITCH
Using Android’s Network Service Discovery to connect to a device on the local area network.

Basic steps involved in this project are
1. Registering listener
2. Discovering services
3. Resolving services

# Registering service
 NsdManager class is used for this and NsdServiceInfo object needs to be created and passed in register service method for registration. It holds the service information to be published. Service name can be any name here i have used SMITCH_mDNS_SERVICE, Service type is _http._tcp and Port is 80.
 
  NsdServiceInfo().apply {
            serviceName = _serviceName
            serviceType =
                SERVICE_TYPE
            port = _port
            nsdManager.registerService(this, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }

 Then registration listener passed as the last parameter for getting callback. It contains 4 listener’s callbacks 
 registration if is successful, or failed, or un-registration.
 
 
 # Discovering services

        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

Discovery listener passed as the last parameter for identify service is Lost or Found.
in onServiceFound we will get the Service and It needs to be resolved  using ResolveListener.

# Resolving discovered services
 ResolveListener has two methods. on service resolved and on resolve failed. Once service resolved callback is received, we can use the service info object in its parameter to retrieve IP and port information.
 And this ServiceInfo has been passed through NsdServiceDiscoveryInterface to showing service information in the RecyclerView.
 



 
