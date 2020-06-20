package com.siva.multicastdnsresolutionandroid.model

import java.net.InetAddress

data class NSDServiceInfo(
    val serviceName: String?=null,
    val serviceType: String?=null,
    val host: InetAddress?=null,
    val port: Int?=null
)