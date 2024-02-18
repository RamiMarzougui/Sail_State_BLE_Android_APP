/*
Renvoi des objets de Resource à partir des opérations asynchrones,
ce qui permet à l'interface utilisateur de réagir en conséquence.
Par exemple,  afficher un indicateur de chargement lorsque l'état est Loading,
afficher des données lorsque l'état est Success, et afficher un message d'erreur lorsque l'état est Error.
Cela facilite également la gestion des erreurs et la communication avec l'utilisateur.
 */

package com.example.bletutorial.util

import com.example.bletutorial.data.SelfCheckResult

sealed class Resource<out T:Any>{
    data class Success<out T:Any> (val data:T):Resource<T>()
    data class Test<out T:Any> (val testBytes: SelfCheckResult):Resource<T>()
    data class Error(val errorMessage:String):Resource<Nothing>()
    data class Loading<out T:Any>(val data:T? = null, val message:String? = null):Resource<T>()
    data class Scan(val ScanDeviceName:String, val ScanDeviceAddress:String):Resource<Nothing>()

}
