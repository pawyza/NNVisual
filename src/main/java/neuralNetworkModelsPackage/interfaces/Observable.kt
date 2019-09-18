package neuralNetworkModelsPackage.interfaces

import neuralNetworkModelsPackage.dataClasses.NetworkState

interface Observable {

    val observers : MutableList<Observer>
    var state : NetworkState?

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }
    fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }
    fun notifyObservers(){
        observers.map { it.update() }
    }
}