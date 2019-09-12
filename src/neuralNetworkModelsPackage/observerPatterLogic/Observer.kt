package neuralNetworkModelsPackage.observerPatterLogic

interface Observer {

    val observed : Observable

    fun update()

}