package com.siva.multicastdnsresolutionandroid.ui.main

import android.app.Application
import android.content.Context
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.siva.multicastdnsresolutionandroid.notification.ToastMessage
import com.siva.multicastdnsresolutionandroid.utils.WifiState
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = getApplication<Application>().applicationContext

    /**
     * Get the current state of the WiFi network
     * @return : WifiState
     * */
    private fun getWifiState(): WifiState {

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return if (wifiManager.isWifiEnabled) {
            return if (wifiManager.connectionInfo.bssid != null) {
                WifiState.Connected
            } else {
                ToastMessage.show(context, "Wifi not connected")
                WifiState.EnabledNotConnected
            }
        } else {
            ToastMessage.show(context, "Wifi disabled")
            WifiState.Disabled
        }
    }

    /**
     * Returns true if we are connected to a WiFi network
     */
    fun isWifiConnected(): Boolean {
        return (getWifiState() == WifiState.Connected)
    }
}
