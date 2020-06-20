package com.siva.multicastdnsresolutionandroid.interfaces

import android.net.nsd.NsdServiceInfo

interface NsdServiceDiscoveryInterface {
    fun onNsdServiceInfoFound(nsdServiceInfo: NsdServiceInfo?)
    fun onNsdServiceConnectionLost(nsdServiceInfo: NsdServiceInfo?)
 }