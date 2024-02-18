package com.example.bletutorial.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bletutorial.R
import com.example.bletutorial.data.ble.SensorBLEReceiveManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlin.math.pow

import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight




//A screen for displaying the Self-Check Test Flags + Validate and start the main application

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SelfCheckScreen(
    navController: NavController,
    viewModel: SensorViewModel,
) {

    var batteryState by remember { mutableStateOf(50f) }

    //Sensor Detection LED
    var isSensorPresentLedOn = remember { mutableStateOf(false) }
    //Fuel Gauge Detection LED
    var isFuelGauegPresentLedOn = remember { mutableStateOf(false) }
    //EEPROM Detection LED
    var isEEPROMPresentLedOn = remember { mutableStateOf(false) }
    //Magnetic Field Detection LED
    var isMagFieldLedOn = remember { mutableStateOf(false) }
    //SelfCheck Test state
    var isSelfCheckTestOn = remember { mutableStateOf(false) }

    //Observe changes to TestFlags byte (BLE) and update the Led state
    LaunchedEffect(viewModel.testFlags) {
        //checkBit function : checks the bit at bitPos position and returns (1) if the bit is set
        isSensorPresentLedOn.value = checkBit(viewModel.testFlags, 0)
        isEEPROMPresentLedOn.value = checkBit(viewModel.testFlags, 1)
        isFuelGauegPresentLedOn.value = checkBit(viewModel.testFlags, 2)
        isMagFieldLedOn.value = checkBit(viewModel.testFlags, 3)
        isSelfCheckTestOn.value = checkBit(viewModel.testFlags, 4)
    }

    //Get SoC Value
    batteryState = viewModel.cellSoC/1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Sens2Sail logo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            AddImage(R.drawable.sens2sail, 270.dp, 100.dp) //Display the image
        }

        //SelfCheck test state
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(0.1.dp) // Adjust the overall padding
                .fillMaxWidth() // Make the row take the full width
                .height(25.dp) // Set a specific height for the row
        ) {
            BlueLED(modifier = Modifier
                .size(25.dp) // Set the size of the GreenLED
                .padding(1.dp)
                .align(Alignment.CenterVertically), // Center vertically
                isOn = isSelfCheckTestOn.value)

        }

//        //PreStartupCheck
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(2.dp)
//        ) {
//            AddImage(R.drawable.prestartupcheck, 180.dp, 30.dp) //Display the image
//        }


        //Sensor Presence Detection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            GreenLED(modifier = Modifier.padding(2.dp), isOn = isSensorPresentLedOn.value)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "Sensor Detected", modifier = Modifier.weight(1f))
        }

        //Fuel Gauge Presence Detection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            GreenLED(modifier = Modifier.padding(2.dp), isOn = isFuelGauegPresentLedOn.value)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "Fuel Gauge Detected", modifier = Modifier.weight(1f))

        }

        //EEPROM Presence Detection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            GreenLED(modifier = Modifier.padding(2.dp), isOn = isEEPROMPresentLedOn.value)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "EEPROM Detected", modifier = Modifier.weight(1f))

        }

        //Hall Effect Sensor : Magnetic Field Detection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            GreenLED(modifier = Modifier.padding(2.dp), isOn = isMagFieldLedOn.value)
            Spacer(modifier = Modifier.width(2.dp))
            Text(text = "Magnetic Field \uD83E\uDDF2 Detected", modifier = Modifier.weight(1f))
        }

        //Software Version Display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(3.dp)
        )  {
            Spacer(modifier = Modifier.width(3.dp)) // Add some spacing between elements
            Text(
                text = "Software Version : ${viewModel.SWversion}",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }


        //Serial Number Display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(3.dp)
        )  {
            Spacer(modifier = Modifier.width(3.dp)) // Add some spacing between elements
            Text(
                text = "Serial Number : ${viewModel.serialNumber}",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
        }


        //Voltage
        OutlinedTextField(
            value = "Voltage : ${viewModel.cellVoltage}",
            onValueChange = {  },
            textStyle =  TextStyle(color = Color.Black, fontSize = 15.sp,  textAlign = TextAlign.Center),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                focusedLabelColor = MaterialTheme.colors.primaryVariant,
                unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
            readOnly = true,
            maxLines = 1,
            label = { Text("V" , fontSize = 15.sp) },
            modifier = Modifier
                .height(60.dp),
            )

        //Current
        OutlinedTextField(
            value = "Current : ${viewModel.cellCurrent}",
            onValueChange = {  },
            textStyle =  TextStyle(color = Color.Black, fontSize = 15.sp,  textAlign = TextAlign.Center),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor =  MaterialTheme.colors.primaryVariant,
                unfocusedBorderColor = MaterialTheme.colors.primaryVariant,
                focusedLabelColor = MaterialTheme.colors.primaryVariant,
                unfocusedLabelColor = MaterialTheme.colors.primaryVariant),
            readOnly = true,
            maxLines = 1,
            label = { Text("mA" , fontSize = 15.sp) },
            modifier = Modifier
                .height(60.dp),
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        )  {
            BatteryDisplay(stateOfCharge = batteryState, 250.dp)
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }


        //Start Main Application Button (if the test is OK)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .height(70.dp)
                    .weight(1f) // This makes the text field take available horizontal space
                    .clip(CircleShape)
                    .clickable {
                        //viewModel.startMainProgram() //Writes a specific byte to the device's characteristic to launch the main program of the system
                        navController.navigate(Screen.DataScreen.route){ //Navigate to Data Screen
                            popUpTo(Screen.SelfCheckScreen.route){
                                inclusive = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ){
                PulsatingCircles_2("START")
            }

            Spacer(modifier = Modifier.width(2.dp)) // Add some space between

            Box(
                modifier = Modifier
                    .size(230.dp)
                    .height(70.dp)
                    .weight(1f) // This makes the text field take available horizontal space
                    .clip(CircleShape)
                    .clickable {
                        viewModel.sendSaiState(6) //Re-Activate SelfCheck Test
                    },
                contentAlignment = Alignment.Center
            ){
                PulsatingCircles_3("TEST")
            }

        }

    }

}

//Creates a green LED indicator
@Composable
fun GreenLED(modifier: Modifier = Modifier, size: Dp = 50.dp, isOn: Boolean = false) {
    Box(
        modifier = modifier
            .size(size)
            .background(if (isOn) Color.Green else Color.White, shape = CircleShape)
            .border(3.dp, MaterialTheme.colors.primary.copy(alpha = 0.25f), CircleShape)
    )
}

//Creates a blue LED indicator
@Composable
fun BlueLED(modifier: Modifier = Modifier, size: Dp = 50.dp, isOn: Boolean = false) {
    Box(
        modifier = modifier
            .size(size)
            .background(if (isOn) MaterialTheme.colors.secondary.copy(alpha = 0.5f) else Color.Red, shape = CircleShape)
            .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
    )
}



//Display an image from Drawable Resources
@Composable
fun AddImage(@DrawableRes id: Int, width: Dp, height: Dp) {
    Image(
        painter = painterResource(id = id),
        contentDescription = null,
        modifier = Modifier
            .width(width)
            .height(height)
    )
}

//Checks the bit at bitPos position and returns (1) if the bit is set
fun checkBit(value: Int, bitPos : Int): Boolean {

    val bitMask = 2.0.pow(bitPos.toDouble()).toInt()
    // Check if the specified bit is set (1)
    return (value and bitMask) != 0
}


@Composable
fun BatteryDisplay(stateOfCharge: Float, batteryWidth: Dp) {

    //Change Battery color according to SoC value
    val batteryColor = when {
        stateOfCharge >= 80f -> Color.Green
        stateOfCharge >= 50f -> Color.Yellow
        else -> Color.Red
    }

    Column(
        modifier = Modifier
            .width(batteryWidth)
            //.fillMaxWidth()
            .height(32.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.background)
                    .border(1.dp, MaterialTheme.colors.primary, CircleShape)
            ) {
                // Battery container
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(stateOfCharge / 100f)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(batteryColor, batteryColor)
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$stateOfCharge%",
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body2
            )
        }
    }
}