package com.example.bletutorial.data

import com.example.bletutorial.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface SensorReceiveManager {

    val data: MutableSharedFlow<Resource<SensorResult>>

    /*
    Toutes ces fonctions sont oveeride dans
    TemperatureAndHumidityBLEReceiveManager
     */

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

    //Start Main Program
    //fun start_MainProgram()

    //Send Sail State
    fun send_SaiState(state : Int)

    //Select BLE device to connect to
    fun selectDevice(deviceName : String)

    //Deselect device before closing connection
    fun deselectDevice()



}