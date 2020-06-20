package com.siva.multicastdnsresolutionandroid.model

import java.net.InetAddress

//onServiceResolved: name: MyService002, type: ._http._tcp, host: /192.168.43.8, port: 80, txtRecord:
data class NSDServiceInfo(
    val serviceName: String?=null,
    val serviceType: String?=null,
    val host: InetAddress?=null,
    val port: Int?=null
)