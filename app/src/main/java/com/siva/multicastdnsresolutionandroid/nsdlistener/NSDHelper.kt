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
    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "onResolveFailed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.e(TAG, "onServiceResolved: $serviceInfo")

//            if (serviceInfo.serviceName == mServiceName) {
//                Log.d(TAG, "Same IP.")
//                return
//            }
            mService = serviceInfo

            nsdServiceInterface.onNsdServiceInfoFound(mService)

        }
    }

    fun registerService(_port: Int, _serviceName: String) {
        // Cancel any previous registration request
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
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.serviceName

                Log.d(TAG, "onServiceRegistered : $mServiceName")
                ToastMessage.show(context,"Service registered")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                ToastMessage.show(context,"Service registration failed")
                Log.d(TAG, "onRegistrationFailed with error code : $errorCode")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG, "onServiceUnregistered : ${arg0.serviceName}")
                ToastMessage.show(context,"Service unregistered")

            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Unregistration failed. Put debugging code here to determine why.
                Log.d(TAG, "onUnregistrationFailed with error code : $errorCode")
                ToastMessage.show(context,"Service un-registration failed")

            }
        }
    }

    fun discoverServices() {
        // Cancel any existing discovery request
        stopDiscovery()
        initializeDiscoveryListener()

        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun initializeDiscoveryListener() {
        discoveryListener = object : NsdManager.DiscoveryListener {

            // Called as soon as service discovery begins.
            override fun onDiscoveryStarted(regType: String) {
                Log.d(TAG, "Service discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                // A service was found! Do something with it.
                Log.i(TAG, "Service discovery success$service")

                when {
                    service.serviceType != SERVICE_TYPE &&
                            service.serviceType != SERVICE_TYPE_WITH_DOT ->
                        // Service type is the string containing the protocol and
                        // transport layer for this service.
                        Log.d(TAG, "Unknown Service Type: ${service.serviceType}")

                    service.serviceName == mServiceName -> {
                        // The name of the service tells the user what they'd be
                        // connecting to. It could be "Bob's Chat App".
                        Log.d(TAG, "Same machine: $mServiceName")

                        nsdManager.resolveService(service, resolveListener)
                    }
                 }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
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