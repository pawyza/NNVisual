package neuralNetworkModelsPackage.imageNNPackage

import dataLoadersPackage.csvLoader.Data
import neuralNetworkModelsPackage.observerPatterLogic.NetworkState
import neuralNetworkModelsPackage.observerPatterLogic.Observable
import neuralNetworkModelsPackage.observerPatterLogic.Observer
import neuralNetworkModelsPackage.neuralNetworkLogic.NeuralNetwork
import java.text.DecimalFormat

class ImageNNTestingLogic : Observable{

    override val observers: MutableList<Observer> = mutableListOf()
    override var state: NetworkState = NetworkState("","","","")

    fun test(network : NeuralNetwork, data : Data, layers : Array<Int>){

        if (layers[0] != data.getInputSize()) throw IllegalArgumentException()
        val examplesNumber : Int
        when {
            data.getDataSize() < 10 -> examplesNumber = 1
            data.getDataSize() < 100 -> examplesNumber = 10
            data.getDataSize() < 1000 -> examplesNumber = 100
            data.getDataSize() < 10000 -> examplesNumber = 1000
            else -> examplesNumber = 10000
        }
        val percentFormatter = DecimalFormat("#0.00")
        val minutesFormatter = DecimalFormat("###0")
        val secondsFormatter = DecimalFormat("#0")

        var tik: Long = 0
        var tok: Long = 0
        var result : List<Double>
        var correct = 0
        for(i in 0 until data.getDataSize()){
            tik = System.currentTimeMillis()
            val result = network.predict(data.getData(i).second)
            when (result.indexOf(result.max()) == data.getData(i).first.toInt()) {
                true -> correct++
            }
            tok = System.currentTimeMillis();

            //Observer implementation
            val testsLeft = data.getDataSize()-i

            state = NetworkState(
                    percentageOfCompleted = percentFormatter.format((correct/data.getDataSize().toDouble()) * 100),
                    left = "$testsLeft",
                    efficiencyInPercentage = percentFormatter.format(correct/(i+1)),
                    timeLeft = minutesFormatter.format(testsLeft * -(tik - tok) / 60000) + " m " + secondsFormatter.format((testsLeft * -(tik - tok) % 60000) / 1000) + " s")
            notifyObservers()


            /* TODO updateowanie obrazkow
            if(i % examplesNumber == 0){
                updateExample(i,data.getData(i),data.getInputSize());
            }
            */
        }
        println(percentFormatter.format((correct/data.getDataSize().toDouble()) * 100) + " %")
    }
}