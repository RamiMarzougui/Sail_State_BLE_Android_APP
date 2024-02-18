/*
Ce module Dagger (AppModule) fournit des dépendances clés à l'application,
notamment l'adaptateur Bluetooth et un gestionnaire pour recevoir des données de température et d'humidité via Bluetooth.
 Ces dépendances sont annotées comme étant des singletons (@Singleton),
 ce qui signifie qu'une seule instance de chaque dépendance est créée et partagée dans toute l'application.
 Ce module est utilisé en conjonction avec Hilt pour gérer l'injection de dépendances dans l'application Android.
 */
package com.example.bletutorial.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.bletutorial.data.SensorReceiveManager
import com.example.bletutorial.data.ble.SensorBLEReceiveManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context):BluetoothAdapter{
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideTempHumidityReceiveManager(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter
    ):SensorReceiveManager{
        return SensorBLEReceiveManager(bluetoothAdapter,context)
    }

}