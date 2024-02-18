/*
Expose des propriétés qui peuvent être observées depuis l'interface utilisateur et
des fonctions pour gérer la connexion et la déconnexion avec le périphérique.
Les données reçues sont mises à jour dans les propriétés étatiques,
ce qui permet de mettre à jour dynamiquement l'UI en fonction des nouvelles données.
 */
package com.example.bletutorial.presentation

import android.provider.Settings.Global.DEVICE_NAME
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bletutorial.data.ConnectionState
import com.example.bletutorial.data.SensorReceiveManager
import com.example.bletutorial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val sensorReceiveManager: SensorReceiveManager
) : ViewModel(){

    var initializingMessage by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    //Linear acceleration (Z)
    var accXL by mutableStateOf(0f)
        private set

    //SelfCheck Test Flags variable
    var testFlags by mutableStateOf(0)
        private set

    //FSM STATUS
    var fsmSTATUS by mutableStateOf(0)
        private set

    //Threshold (test)
    var threshold by mutableStateOf(0f)
        private set

    //Counter ON
    var counterON by mutableStateOf(0L)
        private set

    //Counter F1
    var counterF1 by mutableStateOf(0L)
        private set

    //Counter F1
    var counterWU by mutableStateOf(0L)
        private set

    var SWversion by mutableStateOf("")
        private set

    var serialNumber by mutableStateOf("")
        private set

    //Fuel Gauge Data
    var cellVoltage by mutableStateOf(0f)
        private set

    var cellCurrent by mutableStateOf(0)
        private set

    var cellSoC by mutableStateOf(0)
        private set

    val deviceList = mutableStateListOf<DeviceData>()

    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Uninitialized)

    private fun subscribeToChanges(){
        viewModelScope.launch {
            sensorReceiveManager.data.collect{ result ->
                when(result){
                    is Resource.Success -> {
                        accXL = result.data.ACC_XL
                        fsmSTATUS = result.data.FSM_State
                        counterON = result.data.Counter_ON
                        counterF1 = result.data.Counter_F1
                        threshold = result.data.Threshold
                        testFlags = result.data.test_flags //Collect SelfCheck Test Flags Byte
                        SWversion = result.data.software_version //Software version
                        serialNumber = result.data.serial_number //Product Serial Number
                        cellVoltage = result.data.Voltage
                        cellCurrent = result.data.Current
                        cellSoC = result.data.SoC
                        connectionState = result.data.connectionState
                        counterWU = result.data.Counter_WU //ULP-LP counter

                    }

//                    is Resource.Test -> {
//
//                        connectionState = result.testBytes.connectionState
//                    }

                    is Resource.Loading -> {
                        initializingMessage = result.message
                        connectionState = ConnectionState.CurrentlyInitializing
                    }

                    is Resource.Error -> {
                        errorMessage = result.errorMessage
                        connectionState = ConnectionState.Uninitialized
                    }

                    is Resource.Scan ->
                    {
                        addDevice(result.ScanDeviceName, result.ScanDeviceAddress)
                    }
                }
            }
        }
    }

    fun disconnect(){
        sensorReceiveManager.disconnect()
    }

    fun reconnect(){
        sensorReceiveManager.reconnect()
    }

    fun initializeConnection(){
        errorMessage = null
        subscribeToChanges()
        sensorReceiveManager.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        sensorReceiveManager.closeConnection()
    }

    //Add Device Object to the list (name + address)
    fun addDevice(name: String, address: String) {
        val deviceData = DeviceData(name, address)
        deviceList.add(deviceData)
    }

    //Select BLE device to connect to
    fun selectDevice(deviceName : String)
    {
        sensorReceiveManager.selectDevice(deviceName)
    }

    //Deselect BLE device before disconnection
    fun deselectDevice()
    {
        sensorReceiveManager.deselectDevice()
    }

//    //Release SEM to execute main program
//    fun startMainProgram(){
//        sensorReceiveManager.start_MainProgram()
//    }

    //Send user defined Sail State
    fun sendSaiState(state : Int) {
        sensorReceiveManager.send_SaiState(state)
    }


}