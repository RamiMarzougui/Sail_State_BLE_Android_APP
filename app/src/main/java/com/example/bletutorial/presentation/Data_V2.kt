package com.example.bletutorial.presentation



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)

@Composable
fun DataV2(
    navController: NavController,
    //Définit rapidement la fonction (sans argument ni return)
    //onBluetoothStateChanged:()->Unit,
    //navController: NavController,
    //viewModel: TempHumidityViewModel = hiltViewModel()
    viewModel: SensorViewModel,
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
                .fillMaxHeight(0.9f)
                /*.border(
                    BorderStroke(
                        2.dp, Color.Black
                    ),
                    RoundedCornerShape(8.dp)
                ) */,
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            /* Accéléro */
           OutlinedTextField(
               value = "Humidity: ${viewModel.accXL}",
               onValueChange = {  },
               textStyle =  TextStyle(color = Color.Black, fontSize = 30.sp,  textAlign = TextAlign.Center),
               colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
               readOnly = true,
               maxLines = 1,
               label = { Text("XL" , fontSize = 20.sp) },
               modifier = Modifier
                   .height(80.dp),

           )

            /* MLC */
            OutlinedTextField(
                value = "10",
                onValueChange = {  },
                textStyle =  TextStyle(color = Color.Black, fontSize = 30.sp,  textAlign = TextAlign.Center),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                readOnly = true,
                maxLines = 1,
                label = { Text("MLC" , fontSize = 20.sp) },
                modifier = Modifier
                    .height(80.dp),
                )

            /* Threshold */
            OutlinedTextField(
                value = "10",
                onValueChange = {  },
                textStyle =  TextStyle(color = Color.Black, fontSize = 30.sp,  textAlign = TextAlign.Center),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                    unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                    focusedLabelColor = MaterialTheme.colors.primaryVariant,
                    unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
                readOnly = true,
                maxLines = 1,
                label = { Text("Threshold" , fontSize = 20.sp) },
                modifier = Modifier
                    .height(80.dp),
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
                    //label = { Text("Threshold" , fontSize = 20.sp) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                DropdownMenu(
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

            Button(
                onClick = {
                //navController.navigate(Screen.StartScreen.route){}
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


            // Retour au SCAN
            OutlinedButton(
                onClick = {
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













