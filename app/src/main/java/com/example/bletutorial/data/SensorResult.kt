package com.example.bletutorial.data

// Structure pour les datas des capteurs
data class SensorResult(
    val ACC_XL:Float, //Factor : 0.01 | Offset : 0
    val FSM_State:Int, //0 : No interrupt events | 1 : IS_FSM1 | 2 : IS_FSM2 | 4 : IS_FSM3
    val Counter_ON: Long,
    val Counter_F1: Long,
    val Threshold:Float,
    val test_flags: Int,
    val software_version: String,
    val serial_number: String,
    //Fuel Gauge Data
    val Voltage:Float,
    val Current:Int,
    val SoC:Int,
    val connectionState: ConnectionState,
    val Counter_WU: Long,
)
