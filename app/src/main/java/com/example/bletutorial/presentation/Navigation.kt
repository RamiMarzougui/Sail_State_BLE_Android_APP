/*
Crée et gère la navigaion entre les screen
 */

package com.example.bletutorial.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@Composable
fun Navigation(
    //Définit rapidement la fonction (sans argument ni return)
    onBluetoothStateChanged:()->Unit,
    viewModel: SensorViewModel = hiltViewModel(),
) {

    val navController = rememberNavController()

    // Définit le NavHost et l'ordre d'affichage des pages
    NavHost(navController = navController, startDestination = Screen.StartScreen.route){

        composable(Screen.StartScreen.route){
            StartScreen(navController = navController)
        }

        composable(Screen.ConnectionScreen.route){
            ConnectionScreen(
                onBluetoothStateChanged,
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(Screen.SelfCheckScreen.route){
            SelfCheckScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(Screen.DataScreen.route){
            DataScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(Screen.Data_V2.route){
           DataV2(
               navController = navController,
               viewModel = viewModel,
               )
        }

    }

}

//définit les screens
sealed class Screen(val route:String){
    object StartScreen:Screen("start_screen")
    object DataScreen:Screen("data_screen")
    object Data_V2:Screen("data_v2_screen")

    //A screen that handles BLE conenction
    object ConnectionScreen:Screen ("connection_screen")

    //A screen for displaying the Self-Check Test Flags + Validate and start the main application
    object SelfCheckScreen:Screen ("test_screen")
}