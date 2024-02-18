package com.example.bletutorial.data.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bletutorial.data.*
import com.example.bletutorial.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import javax.inject.Inject

@SuppressLint("MissingPermission")
class SensorBLEReceiveManager @Inject constructor(
    // Récupère le context et ble bluetooth adaptater à travers une injection (définit dans AppModule)
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : SensorReceiveManager {

    //Définition des différents paramètres du device Bluetooth
    var DEVICE_NAME = "NO_NAME"//"BLE Device" Name given the value NO_NAME initially

    private val SENSOR_SERVICE_UIID = "0000fff0-0000-1000-8000-00805f9b34fb" //S2S : "0000fff0-0000-1000-8000-00805f9b34fb"      // Blood pressure "00001810-0000-1000-8000-00805f9b34fb"

    private val SC_CHARACTERISTICS_UUID = "0000fff4-0000-1000-8000-00805f9b34fb" //Self Check Test Flags + SW Version And Serial Number Characteristic (4) //S2S : "0000fff4-0000-1000-8000-00805f9b34fb"    // Blood pressure "00002a35-0000-1000-8000-00805f9b34fb"
    private val SAIL_S_CHARACTERISTICS_UUID = "0000fff1-0000-1000-8000-00805f9b34fb"  //Send Sail State Characteristic (1)
    private val XL_CHARACTERISTICS_UUID = "0000fff3-0000-1000-8000-00805f9b34fb" //Sensor Data Characteristic + FSM STATUS + Threshold (3)

    //private val FSM_CHARACTERISTICS_UUID = "0000fff2-0000-1000-8000-00805f9b34fb" //FSM Characteristic (2)

    private val WINDOWS_SERVICE_UIID = "34b1cf4d-1069-4ad6-89b6-e161d79be4d0" //"00001010-0000-1000-8000-00805f9b34fb"                 // Windows service
    private val WINDOWS_WRITE_CHARACTERISTICS_UUID = "34b1cf4d-1069-4ad6-89b6-e161d79be4d2"     // Windows service
    private val WINDOWS_READ_CHARACTERISTICS_UUID = "34b1cf4d-1069-4ad6-89b6-e161d79be4d3" //"00001111-0000-1000-8000-00805f9b34fb"    // Windows service
    //private val MLC_CHARACTERISTICS_UUID = "00002a35-0000-1000-8000-00805f9b34fb"
    //private val TRESHOLD_CHARACTERISTICS_UUID = "00002a35-0000-1000-8000-00805f9b34fb"
    //private val SAIL_STATE_CHARACTERISTICS_UUID = "00002a35-0000-1000-8000-00805f9b34fb"

    //Créer un flux qui est utilisé pour partager les données
    override val data: MutableSharedFlow<Resource<SensorResult>> = MutableSharedFlow()

    // Définit le scanner et l'utilise que quand c'est nécessaire
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    // Definit les paramètres du SCAN
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    // Handler pour la connexion Gatt
    /*private*/ var gatt: BluetoothGatt? = null

    private var isScanning = false

    // Handler pour toutes les Coroutines
    // Coroutine ~= Thread
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //List that contains the scanned devices found
    val detectedDevicesList = mutableListOf<String>()

    // Scan callback
    private val scanCallback = object : ScanCallback(){
        //Scan for all BLE devices
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            val deviceName = result.device?.name ?: "Unknown Device"

            if (!detectedDevicesList.contains(deviceName)) {
                detectedDevicesList.add(deviceName)
                coroutineScope.launch {
                    data.emit(Resource.Scan(ScanDeviceName = deviceName, ScanDeviceAddress = result.device.address))
                }

            }

            //Device is selected
            if(result.device.name == DEVICE_NAME){
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Connecting to device..."))
                }
                if(isScanning){
                    //Go to gattCallBack
                    //result.device.connectGatt(context,false, gattCallback)
                    result.device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
                    isScanning = false
                    bleScanner.stopScan(this)
                }


            }
        }
    }

    private var currentConnectionAttempt = 1
    private var MAXIMUM_CONNECTION_ATTEMPTS = 5

    // Créer le Call back pour la couche GATT
    private val gattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            //Check if the connexion with our sensor works
            if(status == BluetoothGatt.GATT_SUCCESS){
                // newState reprssente le statu de la conenxion GATT
                if(newState == BluetoothProfile.STATE_CONNECTED){
                    coroutineScope.launch {
                        data.emit(Resource.Loading(message = "Discovering Services..."))
                    }
                    /*
                        Envoie une commande au périphérique BLE pour qu'il commence à rechercher et
                        à fournir la liste de ses services disponibles.
                        Une fois que la découverte est terminée,
                        un autre callback (BluetoothGattCallback.onServicesDiscovered()) sera déclenché pour
                        informer l'application que la liste des services a été récupérée.
                     */
                    gatt.discoverServices()
                    // Donne les paramètres de la var locale à la var global gatt
                    this@SensorBLEReceiveManager.gatt = gatt
                } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                    // En cas de déconnexion, affiche 0 dans data et ferme le client GATT
                    coroutineScope.launch {
                        data.emit(Resource.Success(data = SensorResult(0f, 0, 0, 0, 0.0f,0x00, "0.0.0", "0.0", 0.0f, 0, 0, ConnectionState.Disconnected, 0)))
                    }

                    gatt.close()
                }
            }else{
                /*
                 Ferme le client GATT et le relance le SCAN si,
                 le nombre s'essaie est inférieur à son max
                 */
                gatt.close()
                currentConnectionAttempt+=1
                coroutineScope.launch {
                    data.emit(
                        Resource.Loading(
                            message = "Attempting to connect $currentConnectionAttempt/$MAXIMUM_CONNECTION_ATTEMPTS"
                        )
                    )
                }
                if(currentConnectionAttempt<=MAXIMUM_CONNECTION_ATTEMPTS){
                    startReceiving()
                }else{
                    coroutineScope.launch {
                        data.emit(Resource.Error(errorMessage = "Could not connect to ble device"))
                    }
                }
            }
        }

        // Appelé par gatt.discoverServices
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt){
                // Display toutes les infos GATT pour le debug
                printGattTable()
                coroutineScope.launch {
                    data.emit(Resource.Loading(message = "Adjusting MTU space..."))
                }
                /*
                 Set max data échangé via BLE (517 = max for android)
                 C'est le capteur BLE qui impose le max.
                 Ici on le fixe juste pour Android
                 */
                gatt.requestMtu(517)
            }
        }

        // Appelée par gatt.requestMtu
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {

            //Sensor Data Characteristic
            val imuDataCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, XL_CHARACTERISTICS_UUID)

            //Start Main Program characteristic
            //val startProgramCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, MP_CHARACTERISTICS_UUID)

            //Self Check Test characteristic
            //val selfCheckTestCharacteristic  = findCharacteristics(SENSOR_SERVICE_UIID, SC_CHARACTERISTICS_UUID)

            //Send Sail State characteristic
            //val sendSailStateCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, SAIL_S_CHARACTERISTICS_UUID)

            if(imuDataCharacteristic == null){
                coroutineScope.launch {
                    data.emit(Resource.Error(errorMessage = "Could not find the necessary publisher"))
                }
                return
            }

            //Subscribe to S2S Data
            enableNotification(imuDataCharacteristic)

        }



        // Récupère les datas
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {

            with(characteristic){
                when(uuid){

                    //If the Self Check Characteristic has been changed
//                    UUID.fromString(SC_CHARACTERISTICS_UUID) -> {
//                        val test_flags = value[0].toInt() //Byte 0 -> Self-Check Test Flags
//                        //Create and Build Software Version String
//                        val softVersion = StringBuilder()
//                        softVersion.append(value[1])
//                        softVersion.append('.')
//                        softVersion.append(value[2])
//                        softVersion.append('.')
//                        softVersion.append(value[3])
//                        //Create and Build Serial Number String
//                        val serialNumber = StringBuilder()
//                        serialNumber.append(value[4])
//                        serialNumber.append('.')
//                        serialNumber.append(value[5])
//
//                        //Fuel Gauge Data
//                        val cellVoltage = ((value[6].toInt() and 0xFF) shl 8) or (value[7].toInt() and 0xFF) //in mV
//                        val cellCurrent = ((value[8].toInt() and 0xFF) shl 8) or (value[9].toInt() and 0xFF)
//                        val soc = ((value[10].toInt() and 0xFF) shl 8) or (value[11].toInt() and 0xFF)
//
//                        val selfCheckResult = SelfCheckResult(
//                            test_flags,
//                            softVersion.toString(),
//                            serialNumber.toString(),
//                            cellVoltage * 0.001f,
//                            cellCurrent - 3000,
//                            soc,
//                            ConnectionState.Connected,
//                        )
//
//                        coroutineScope.launch {
//                            data.emit(
//                                Resource.Test(testBytes = selfCheckResult)
//                            )
//                        }
//
//                    }

                    //If the Sensor Data Characteristic and/or FSM STATUS has been changed
                    UUID.fromString(XL_CHARACTERISTICS_UUID) -> {

                        val accXL = value[0].toInt() //Raw value
                        val threshold = value[2].toInt()

                        //Counter ON
                        val counterON = ((value[3].toLong() and 0xFF) shl 24) or ((value[4].toLong() and 0xFF) shl 16) or ((value[5].toLong() and 0xFF) shl 8) or (value[6].toLong() and 0xFF)

                        //Counter F1
                        val counterF1 = ((value[7].toLong() and 0xFF) shl 24) or ((value[8].toLong() and 0xFF) shl 16) or ((value[9].toLong() and 0xFF) shl 8) or (value[10].toLong() and 0xFF)

                        //Self-Check Test
                        val test_flags = value[11].toInt() //Byte 11 -> Self-Check Test Flags
                        //Create and Build Software Version String
                        val softVersion = StringBuilder()
                        softVersion.append(value[12])
                        softVersion.append('.')
                        softVersion.append(value[13])
                        softVersion.append('.')
                        softVersion.append(value[14])
                        //Create and Build Serial Number String
                        val serialNumber = StringBuilder()
                        serialNumber.append(value[15])
                        serialNumber.append('.')
                        serialNumber.append(value[16])

                        //Fuel Gauge Data
                        val cellVoltage = ((value[17].toInt() and 0xFF) shl 8) or (value[18].toInt() and 0xFF) //in mV
                        val cellCurrent = ((value[19].toInt() and 0xFF) shl 8) or (value[20].toInt() and 0xFF)
                        val soc = ((value[21].toInt() and 0xFF) shl 8) or (value[22].toInt() and 0xFF)
                        val thresholdOnF1 = ((value[23].toInt() and 0xFF) shl 8) or (value[24].toInt() and 0xFF) //uint16 needs to ne converted to half precision

                        //Counter WU
                        val counterWU = ((value[25].toLong() and 0xFF) shl 24) or ((value[26].toLong() and 0xFF) shl 16) or ((value[27].toLong() and 0xFF) shl 8) or (value[28].toLong() and 0xFF)

                        val sensorResult = SensorResult(
                            accXL * 0.01f, //Factor = 0.01
                            value[1].toInt(), //FSM STATUS
                            counterON,
                            counterF1 * 1L,
                            intToHalfPrecision(thresholdOnF1),
                            test_flags,
                            softVersion.toString(),
                            serialNumber.toString(),
                            cellVoltage * 0.001f,
                            cellCurrent - 3000,
                            soc,
                            ConnectionState.Connected,
                            counterWU,
                        )

                        coroutineScope.launch {
                            data.emit(
                                Resource.Success(data = sensorResult)
                            )
                        }


                    }

                    else -> Unit
                }
            }
        }

//        override fun onDescriptorWrite(
//            gatt: BluetoothGatt?,
//            descriptor: BluetoothGattDescriptor?,
//            status: Int
//        ) {
//            val imuDataCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, XL_CHARACTERISTICS_UUID)
//            if(imuDataCharacteristic != null)
//            {
//                //Subscribe to Sensor Data + FSM STATUS
//                enableNotification(imuDataCharacteristic)
//            }
//        }

    }

    /*
     Active les notifications GATT et paramètres le client
     En gros, on se subscribe aux services sélectionés
     */
    private fun enableNotification(characteristic: BluetoothGattCharacteristic){
        // Cet UUID déinit l'appli dans le rôle de client (norme BLE)
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        // Enable indication et/ou notification en fonction du device
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }
        // Récupère le descriptor
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            // Enable les notifications
            if(gatt?.setCharacteristicNotification(characteristic, true) == false){
                Log.d("BLEReceiveManager","set characteristics notification failed")
                return
            }
            Log.d("BLEReceiveManager","set characteristics notification success")
            // Ecrit le descriptor de la characteristic
            writeDescription(cccdDescriptor, payload)
        }


    }


    private fun disableNotification(characteristic: BluetoothGattCharacteristic) {
        // UUID for the Client Characteristic Configuration Descriptor (CCCD)
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        // Disable notifications/indications by sending the appropriate payload
        val payload = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE

        // Retrieve the CCCD descriptor for the characteristic
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            // Disable notifications/indications
            if (gatt?.setCharacteristicNotification(characteristic, false) == false) {
                Log.d("TryMe", "Disable characteristics notification failed")
                return
            }
            Log.d("TryMe", "Disable characteristics notification success")
            // Write the CCCD descriptor with the payload to disable notifications
            writeDescription(cccdDescriptor, payload)
        }
    }

    fun intToHalfPrecision(value: Int): Float {
        val sign = (value ushr 15) and 0x1
        val exponent = (value ushr 10) and 0x1F
        val fraction = value and 0x3FF

        return when {
            exponent == 0x00 && fraction == 0x00 -> if (sign == 1) -0f else 0f
            exponent == 0x1F -> if (fraction == 0x00) {
                if (sign == 1) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
            } else {
                Float.NaN
            }
            else -> {
                val biasedExponent = exponent - 15 + 127
                val shiftedValue = (sign shl 31) or (biasedExponent shl 23) or (fraction shl 13)
                Float.fromBits(shiftedValue)
            }
        }
    }



    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        gatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        } ?: error("Not connected to a BLE device!")
    }

    // Définit les characteristics qu'on veut trouver
    private fun findCharacteristics(serviceUUID: String, characteristicsUUID:String):BluetoothGattCharacteristic?{
        return gatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }
    }

    // Démarre le Scan
    override fun startReceiving() {
        //Créer et lance la tâche asynchrone
        coroutineScope.launch {
            data.emit(Resource.Loading(message = "BLE device selection"))
        }
        isScanning = true
        // Lance le SCAN
        bleScanner.startScan(null,scanSettings,scanCallback)
    }

    override fun reconnect() {
        gatt?.connect()
    }

    override fun disconnect() {
        gatt?.disconnect()
    }



    override fun send_SaiState(state : Int)
    {
        //Send Sail State Characteristic
        val sendSailSCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, SAIL_S_CHARACTERISTICS_UUID)
        //Set Characteristic Value
        sendSailSCharacteristic?.setValue(state, FORMAT_UINT8 , 0)
        gatt?.writeCharacteristic(sendSailSCharacteristic)

    }

    override fun selectDevice(deviceName : String)
    {
        DEVICE_NAME = deviceName //Selection is done by name for now (MAJ possible: name + address in the case where we're having many products with the same name)
    }

    override fun deselectDevice()
    {
        DEVICE_NAME = "NO_NAME"
    }

    override fun closeConnection() {
        bleScanner.stopScan(scanCallback)

        val imuDataCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, XL_CHARACTERISTICS_UUID)
        //val startProgramCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, MP_CHARACTERISTICS_UUID)
        val selfCheckTestCharacteristic  = findCharacteristics(SENSOR_SERVICE_UIID, SC_CHARACTERISTICS_UUID)
        //val sendSailStateCharacteristic = findCharacteristics(SENSOR_SERVICE_UIID, SAIL_S_CHARACTERISTICS_UUID)

        //Disconnect all characteristics before closing
        if(imuDataCharacteristic != null){
            disconnectCharacteristic(imuDataCharacteristic)
        }

//        if(startProgramCharacteristic != null) {
//            disconnectCharacteristic(startProgramCharacteristic)
//        }

//        if(selfCheckTestCharacteristic != null) {
//            disconnectCharacteristic(selfCheckTestCharacteristic)
//        }

//        if(sendSailStateCharacteristic != null) {
//            disconnectCharacteristic(sendSailStateCharacteristic)
//        }

        gatt?.close()
    }

    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(gatt?.setCharacteristicNotification(characteristic,false) == false){
                Log.d("BLEReceiveManager","set charateristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }


}