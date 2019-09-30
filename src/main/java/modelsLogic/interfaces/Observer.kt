package modelsLogic.interfaces

interface Observer {

    val observed : Observable?

    fun update()

}