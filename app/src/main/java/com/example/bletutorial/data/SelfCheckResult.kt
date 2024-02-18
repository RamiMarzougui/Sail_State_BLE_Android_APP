package com.example.bletutorial.data

//Data class containing the Self Check Test results
data class SelfCheckResult(
    val test_flags: Int,
    val software_version: String,
    val serial_number: String,
    //Fuel Gauge Data
    val Voltage:Float,
    val Current:Int,
    val SoC:Int,
    val connectionState: ConnectionState,
)
