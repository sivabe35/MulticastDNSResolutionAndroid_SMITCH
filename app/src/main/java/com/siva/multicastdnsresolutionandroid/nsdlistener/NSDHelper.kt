package com.siva.multicastdnsresolutionandroid.nsdlistener

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.siva.multicastdnsresolutionandroid.interfaces.NsdServiceDiscoveryInterface
import com.siva.multicastdnsresolutionandroid.notification.ToastMessage
import java.net.InetAddress

const val SERVICE_TYPE = "_http._tcp"
const val SERVICE_TYPE_WITH_DOT = "$SERVICE_TYPE."

class NSDHelper(val context: Context?, val nsdServiceInterface: NsdServiceDiscoveryInterface) {

    private val TAG = NSDHelper::class.java.simpleName

    private val nsdManager by lazy { context?.getSystemService(Context.NSD_SERVICE) as NsdManager }
    private var mService: NsdServiceInfo? = null
    private var mServiceName: String? = null
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null


    /**
     * Register mDNS Service
     *
     *  Cancel any previous registration request
     * */
    fun registerService(_port: Int, _serviceName: String) {
        unRegister()
        initializeRegistrationListener()

        NsdServiceInfo().apply {
            serviceName = _serviceName
            serviceType =
                SERVICE_TYPE
            port = _port
            nsdManager.registerService(this, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }

    }

    private fun initializeRegistrationListener() {
        registrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {

                mServiceName = NsdServiceInfo.serviceName
                Log.d(TAG, "onServiceRegistered : $mServiceName")
                ToastMessage.show(context, "Service registered")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                ToastMessage.show(context, "Service registration failed")
                Log.d(TAG, "onRegistrationFailed with error code : $errorCode")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                Log.d(TAG, "onServiceUnregistered : ${arg0.serviceName}")
                ToastMessage.show(context, "Service unregistered")

            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d(TAG, "onUnregistrationFailed with error code : $errorCode")
                ToastMessage.show(context, "Service un-registration failed")

            }
        }
    }

    /**
     *  Start Discover mDNS Service listener
     *
     *  Cancel any previous Discover listener registered
     * */
    fun discoverServices() {
        stopDiscovery()
        initializeDiscoveryListener()

        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    /**
     * Discovery Listener to check Service status
     * */
    private fun initializeDiscoveryListener() {
        discoveryListener = object : NsdManager.DiscoveryListener {

            override fun onDiscoveryStarted(regType: String) {
                Log.d(TAG, "Service discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.i(TAG, "Service discovery success$service")


                when {
                    service.serviceType != SERVICE_TYPE && service.serviceType != SERVICE_TYPE_WITH_DOT ->
                        Log.d(TAG, "Unknown Service Type: ${service.serviceType}")

                    service.serviceName.contains("SMITCH_mDNS_SERVICE",true) ->{

                        nsdManager.resolveService(
                            service,
                            object : NsdManager.ResolveListener {

                                override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                                    Log.e(TAG, "onResolveFailed: $errorCode")
                                }

                                override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                                    Log.e(TAG, "onServiceResolved: $serviceInfo")

                                    //if (serviceInfo.serviceName == mServiceName) {
                                    //    Log.d(TAG, "Same IP.")
                                    //    return
                                    //}
                                    mService = serviceInfo
                                    nsdServiceInterface.onNsdServiceInfoFound(mService)

                                }
                            })

                    }
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e(TAG, "service lost: $service")
                if (mService == service) {
                    mService = null
                }
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i(TAG, "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                nsdManager.stopServiceDiscovery(this)
            }
        }
    }


    fun tearDown() {
        unRegister()
        stopDiscovery()
    }

    private fun unRegister() {
        registrationListener?.let {
            nsdManager.unregisterService(it)
            registrationListener = null
        }

    }

    private fun stopDiscovery() {
        discoveryListener?.let {
            nsdManager.stopServiceDiscovery(it)
            discoveryListener = null
        }
    }

    fun getChosenServiceInfo(): NsdServiceInfo? {
        return mService
    }
}