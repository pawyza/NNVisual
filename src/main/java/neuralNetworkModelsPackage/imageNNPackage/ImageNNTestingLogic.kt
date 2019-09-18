package neuralNetworkModelsPackage.imageNNPackage

import dataLoadersPackage.csvLoader.Data
import neuralNetworkModelsPackage.dataClasses.NetworkState
import neuralNetworkModelsPackage.interfaces.Logic
import neuralNetworkModelsPackage.interfaces.Observable
import neuralNetworkModelsPackage.interfaces.Observer
import neuralNetworkModelsPackage.neuralNetworkLogic.NeuralNetwork
import java.text.DecimalFormat

class ImageNNTestingLogic(private val network : NeuralNetwork,private val data : Data,private val layers : Array<Int>) : Logic ,Observable{

    override val observers: MutableList<Observer> = mutableListOf()
    override var state: NetworkState? = null

    override fun run(){

        if (layers[0] != data.getInputSize()) throw IllegalArgumentException()
        val examplesNumber : Int
        when {
            data.getDataSize() <= 2000 -> examplesNumber = data.getDataSize()
            else -> examplesNumber = 2000
        }
        val percentFormatter = DecimalFormat("#0.00")
        val minutesFormatter = DecimalFormat("###0")
        val secondsFormatter = DecimalFormat("#0")

        var correct = 0
        for(i in 0 until data.getDataSize()){
            val tik = System.currentTimeMillis()
            val result = network.predict(data.getData(i).second)
            when (result.indexOf(result.max()) == data.getData(i).first.toInt()) {
                true -> correct++
            }
            val tok = System.currentTimeMillis();

            //Observer implementation
            val testsLeft = data.getDataSize()-i

            if(i % examplesNumber == 0) {
                state = NetworkState(
                        currentInputData = i,
                        prediction = result.indexOf(result.max()),
                        percentageOfCompleted = percentFormatter.format((correct / data.getDataSize().toDouble()) * 100),
                        left = "$testsLeft",
                        efficiencyInPercentage = percentFormatter.format((correct / (i + 1).toDouble())*100),
                        timeLeft = minutesFormatter.format(testsLeft * -(tik - tok) / 60000) + " m " + secondsFormatter.format((testsLeft * -(tik - tok) % 60000) / 1000) + " s")
                notifyObservers()
            }
        }
    }
}