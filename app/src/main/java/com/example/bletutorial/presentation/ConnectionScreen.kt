//Screen gérant l'affichage des datas
package com.example.bletutorial.presentation

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection.Companion.In
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.bletutorial.data.ConnectionState
import com.example.bletutorial.data.permissions.PermissionUtils
import com.example.bletutorial.data.permissions.SystemBroadcastReceiver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextField
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bletutorial.R

//Data class that contains the BLE devices name + address (possible data to add : alias, ....)
data class DeviceData(
    val deviceName: String,
    val deviceAddress: String
)

//This Screen handles the BLE connection process to a DEVICE
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConnectionScreen(
    //Définit rapidement la fonction (sans argument ni return)
    onBluetoothStateChanged:()->Unit,
    navController: NavController,
    viewModel: SensorViewModel,
) {

    //Clear out screen and hide list of avaialable devices when a device is selected
    var isDeviceListVisible by remember { mutableStateOf(true) }

    /*
    Si le system indique un changement dans le syteme bluetooth du tel
     alors appelle la fonction onBluetoothStateChanged().
    Qui va redemander a l'user d'autoriser le BLE
    */
    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED){ bluetoothState ->
        val action = bluetoothState?.action ?: return@SystemBroadcastReceiver
        if(action == BluetoothAdapter.ACTION_STATE_CHANGED){
            onBluetoothStateChanged()
        }
    }

    // Stock toutes les demandes de permissions
    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState
    val NavigationState = remember { mutableStateOf(false) }

    // Observe le cycle de vie de l'appli
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{_,event ->
                // Au démarage, demande les aurotisations et se connect ou re-connect au BLE
                if(event == Lifecycle.Event.ON_START){
                    permissionState.launchMultiplePermissionRequest()
                    if(permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected){
                        // Appelle gatt.reconnect()
                        viewModel.reconnect()
                    }
                }
                // Stop, ce déconnecte du BLE
                if(event == Lifecycle.Event.ON_STOP){
                    if (bleConnectionState == ConnectionState.Connected){
                        // Appelle gatt.disconnect()
                        viewModel.disconnect()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    LaunchedEffect(key1 = permissionState.allPermissionsGranted){
        if(permissionState.allPermissionsGranted){
            if(bleConnectionState == ConnectionState.Uninitialized){
                viewModel.initializeConnection()
            }
        }
    }


    // Conteneur qui occupe tout l'écran
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        // Organise le contenu de manière vertical
        Column(

        ){
        // Modifie l'affichage en focntion du résultat
            if(bleConnectionState == ConnectionState.CurrentlyInitializing){

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    if(isDeviceListVisible)
                    {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {

                            Text(
                                text = "Devices Found",
                                modifier = Modifier.padding(10.dp),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue
                            )

                            Spacer(modifier = Modifier.width(1.dp)) // Adjust the spacing

                            Image(
                                painter = painterResource(id = R.drawable.ic_baseline_bluetooth_24),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(35.dp)
                            )

                        }

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = Color.Gray)
                        )

                        LazyColumn {

                            items(viewModel.deviceList) { deviceData ->
                                Text(
                                    text = deviceData.deviceName,
                                    modifier = Modifier.clickable {
                                        isDeviceListVisible = false
                                        viewModel.selectDevice(deviceData.deviceName) //Device is selected : Hide list of available devices + connect to device
                                    }
                                        .padding(horizontal = 16.dp, vertical = 5.dp)
                                        .fillMaxWidth(),
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                Text(
                                    text = deviceData.deviceAddress,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                                        .fillMaxWidth(),
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(color = Color.Gray)
                                )
                            }
                        }
                    }
                    else
                    {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                if (viewModel.initializingMessage != null) {
                                    Text(
                                        text = viewModel.initializingMessage!!
                                    )
                                }
                            }
                    }

                }

            }else if(!permissionState.allPermissionsGranted){
                Text(
                    text = "Go to the app setting and allow the missing permissions",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }else if(viewModel.errorMessage != null){
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = viewModel.errorMessage!!
                    )
                    Button(
                        onClick = {
                            if(permissionState.allPermissionsGranted){
                                viewModel.initializeConnection()
                            }
                        }
                    ) {
                        Text(
                            "Try again"
                        )
                    }
                }
            }else if(bleConnectionState == ConnectionState.Connected){
                if(NavigationState.value == false) {
                    NavigationState.value = true //To make sure that we go to this screen only once
                    /* Navigate to Self Check Test Screen */
                    navController.navigate(Screen.SelfCheckScreen.route){
                        popUpTo(Screen.ConnectionScreen.route){
                            inclusive = true
                        }
                    }

                }


            }else if(bleConnectionState == ConnectionState.Disconnected){
                Button(onClick = {
                    viewModel.initializeConnection()
                }) {
                    Text("Initialize again")
                }
            }
        }
    }
}

