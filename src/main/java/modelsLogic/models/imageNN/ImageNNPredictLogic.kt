package modelsLogic.models.imageNN

import data.holder.NotDescribedData
import modelsLogic.dataClasses.NetworkState
import modelsLogic.interfaces.Logic
import modelsLogic.interfaces.Observable
import modelsLogic.interfaces.Observer
import modelsLogic.neuralNetworkLogic.NeuralNetwork
import java.text.DecimalFormat

class ImageNNPredictLogic(private val network : NeuralNetwork, private val data : NotDescribedData) : Logic, Observable {

    override val observers: MutableList<Observer> = mutableListOf()
    override var state: NetworkState? = null
    private val answersList = mutableListOf<Int>()

    override fun run() {
        require(network.layers[0] == data.getInputSize())
        val examplesNumber : Int = when {
            data.getDataSize() <= 2000 -> data.getDataSize()
            else -> 2000
        }
        val percentFormatter = DecimalFormat("#0.00")
        val minutesFormatter = DecimalFormat("###0")
        val secondsFormatter = DecimalFormat("#0")

        for(i in 0 until data.getDataSize()){
            val tik = System.currentTimeMillis()
            val result = network.predict(data.getData(i))
            answersList.add(result.indexOf(result.max()))
            val tok = System.currentTimeMillis();

            //Observer implementation
            val testsLeft = data.getDataSize()-i

            if(i % examplesNumber == 0) {
                state = NetworkState(
                        currentInputData = i,
                        prediction = result.indexOf(result.max()),
                        percentageOfCompleted = percentFormatter.format((i / data.getDataSize().toDouble()) * 100),
                        left = "$testsLeft",
                        efficiencyInPercentage = "n/a",
                        timeLeft = minutesFormatter.format(testsLeft * -(tik - tok) / 60000) + " m " + secondsFormatter.format((testsLeft * -(tik - tok) % 60000) / 1000) + " s")
                notifyObservers()
            }
        }
    }

    fun getPredictions() : List<Int>{
        return answersList
    }
}
