/*
 Cette activité gère la demande d'activation du Bluetooth sur l'appareil Android,
 en utilisant Jetpack Compose et Hilt pour l'injection de dépendances.
 Elle affiche un dialogue demandant à l'utilisateur d'activer le Bluetooth et gère la réponse de l'utilisateur.
 */

package com.example.bletutorial

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bletutorial.presentation.Navigation
import com.example.bletutorial.ui.theme.BLETutorialTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.location.LocationManager
import android.provider.Settings
import androidx.core.location.LocationManagerCompat.isLocationEnabled


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //interagit avec les fonctionnalités Bluetooth de l'appareil.
    @Inject lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLETutorialTheme {
                // Appelle le Navigation screen et affiche la page de démarrage
                Navigation(
                    onBluetoothStateChanged = {
                        showBluetoothDialog()

                        // Check if Location services are enabled
                        if (!isLocationEnabled(this)) {
                            showLocationPrompt(this)
                        }
                    }
                )
            }
        }
    }

    // Fonction exéctué automatiquement,
    // au démarrage ou à la réouverture de l'appli
    override fun onStart() {
        super.onStart()
        // Check if Bluetooth services are enabled
        showBluetoothDialog()
       //requestLocationServices(this)
        // Check if Location services are enabled
        if (!isLocationEnabled(this)) {
            showLocationPrompt(this)
        }
    }


    /*********** Enable Bluetooth Services *************/
    // Flag qui permet d'afficher la demande d'activation de BLE une seul fois
    private var isBluetootDialogAlreadyShown = false
    // Demande à l'utilisateur d'activé le BLE
    private fun showBluetoothDialog(){
        if(!bluetoothAdapter.isEnabled){
            if(!isBluetootDialogAlreadyShown){
                // Demande l'autorisationa l'user
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetootDialogAlreadyShown = true
            }
        }
    }

    // Check si l'utilisateur dis oui ou non
    // Si l'utilisateur dis non, alors un enouvelle demande lui est soumise
    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            isBluetootDialogAlreadyShown = false
            if(result.resultCode != Activity.RESULT_OK){
                showBluetoothDialog()
            }
        }
    /**************************************************/

    /*********** Enable Location Services *************/
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager //Access to system Location Services
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun showLocationPrompt(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enable Location Services")
        builder.setMessage("Location services are required for the Sens2Sail app. Please enable them in Settings")
        builder.setPositiveButton("Settings") { dialog, which ->
            //Open the Location settings screen
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        builder.setOnCancelListener { dialog ->
            dialog.dismiss()
        }
        builder.show()
    }
    /******************************************************/

}
