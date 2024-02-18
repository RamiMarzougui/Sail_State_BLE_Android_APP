package com.example.bletutorial.presentation

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.bletutorial.R
import com.example.bletutorial.data.ConnectionState
import com.example.bletutorial.data.SensorReceiveManager
import com.example.bletutorial.data.ble.SensorBLEReceiveManager
import com.example.bletutorial.data.permissions.PermissionUtils
import com.example.bletutorial.data.permissions.SystemBroadcastReceiver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

//A screen for displaying DATA

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DataScreen(
    //Définit rapidement la fonction (sans argument ni return)
    navController: NavController,
    viewModel: SensorViewModel,
) {
    // Conteneur qui occupe tout l'écran
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        // Organise le contenu de manière vertical
        Column(
            /*modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(1f)
                .aspectRatio(1f)
                .border(
                    BorderStroke(
                        5.dp, Color.Blue
                    ),
                    RoundedCornerShape(10.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally */
        ){
            DataUi(navController = navController, viewModel = viewModel)
        }
    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DataUi(
    navController: NavController,
    viewModel: SensorViewModel, // = hiltViewModel()
) {

    // Var
    val options_state = listOf("Off", "Active", "Flapping")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options_state[0]) }

    // Conteneur qui occupe tout l'écran
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        // Organise le contenu de manière vertical
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ){


            //Sens2Sail logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(1.dp)
            ) {
                AddImage(R.drawable.sens2sail, 270.dp, 100.dp) //Display the image
            }

            /* Accéléro */
            OutlinedTextField(
                value = "Acceleration Z: ${viewModel.accXL}", //Linear acceleration sensor Z-axis
                onValueChange = {  },
                textStyle =  TextStyle(color = Color.Black, fontSize = 20.sp,  textAlign = TextAlign.Center),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                readOnly = true,
                maxLines = 1,
                label = { Text("XL" , fontSize = 20.sp) },
                modifier = Modifier
                    .height(70.dp),

                )


            /* MLC */
            var fsmSTATUS = ""
            when(viewModel.fsmSTATUS) { //Check the received FSM state
                0 -> {
                    fsmSTATUS = "No interrupt"
                }
                1 -> {
                    fsmSTATUS = "OFF"
                }
                2 -> {
                    fsmSTATUS = "ON"
                }
                4 -> {
                    fsmSTATUS = "F1"
                }
            }

            OutlinedTextField(
                value = fsmSTATUS,
                onValueChange = {  },
                textStyle =  TextStyle(color = Color.Black, fontSize = 20.sp,  textAlign = TextAlign.Center),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                readOnly = true,
                maxLines = 1,
                label = { Text("MLC" , fontSize = 20.sp) },
                modifier = Modifier
                    .height(70.dp),
            )


            //Counters
            OutlinedTextField(
                value = "ON : ${viewModel.counterON}    |    F1 : ${viewModel.counterF1}",
                onValueChange = {  },
                textStyle =  TextStyle(color = Color.Black, fontSize = 18.sp,  textAlign = TextAlign.Center),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                readOnly = true,
                maxLines = 1,
                label = { Text("Counters" , fontSize = 20.sp) },
                modifier = Modifier
                    .height(70.dp),
            )




            /* Threshold */
            OutlinedTextField(
                value = "${viewModel.threshold}",
                onValueChange = {  },
                textStyle =  TextStyle(color = Color.Black, fontSize = 20.sp,  textAlign = TextAlign.Center),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                readOnly = true,
                maxLines = 1,
                label = { Text("Threshold" , fontSize = 20.sp) },
                modifier = Modifier
                    .height(70.dp),
            )

            /* Real state*/
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    readOnly = true,
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    value = selectedOptionText,
                    onValueChange = { },
                    label = { Text("Sail state", fontSize = 20.sp) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    options_state.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            }
                        ) {
                            Text(text = selectionOption)
                        }
                    }
                }
            }


            // Add Spacer to create vertical space
            Spacer(modifier = Modifier.height(16.dp))

            /* Send Sail State */
            Button(
                onClick = {
                    when(selectedOptionText) //"Off", "Active", "Flapping"
                    {
                        "Off" -> {
                            viewModel.sendSaiState(0) //Off : 0
                        }
                        "Active" -> {
                            viewModel.sendSaiState(2) //Active : 2
                        }
                        "Flapping" -> {
                            viewModel.sendSaiState(4) //Flapping : 4
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.65f),
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(50),
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Back" ,
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize),
                    tint = Color.White
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Send State",fontSize = 20.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                /* Reset Counters */
                Button(
                    onClick = {
                        viewModel.sendSaiState(5) //RESET COUNTERS : 5
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red, // Set your desired background color here
                        contentColor = Color.White // Set the text and icon color
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Back" ,
                        modifier = Modifier
                            .size(ButtonDefaults.IconSize),
                        tint = Color.White
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("RESET",fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(16.dp)) // Add some space between

                //ULP-LP Counter
                OutlinedTextField(
                    value = "${viewModel.counterWU}",
                    onValueChange = {  },
                    textStyle =  TextStyle(color = Color.Black, fontSize = 18.sp,  textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                        unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                        focusedLabelColor = MaterialTheme.colors.primaryVariant,
                        unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                    readOnly = true,
                    maxLines = 1,
                    label = { Text("WU" , fontSize = 20.sp) },
                    modifier = Modifier
                        .height(60.dp)
                        .weight(1f),
                )


            }



            /* Retour au SCAN */
            OutlinedButton(
                onClick = {
                    viewModel.deselectDevice() //Deselect device (name) before disconnecting
                    viewModel.disconnect() //Disconnect from the current selected device
                    navController.navigate(Screen.StartScreen.route){}
                },
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth(0.65f),
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Back to SCAN",fontSize = 20.sp)

            }

        }
    }
}