package modelsLogic.models.imageNN

import data.holder.DescribedData
import modelsLogic.dataClasses.NetworkState
import modelsLogic.interfaces.Logic
import modelsLogic.interfaces.Observable
import modelsLogic.interfaces.Observer
import modelsLogic.neuralNetworkLogic.NeuralNetwork
import java.text.DecimalFormat
import java.util.*

class ImageNNTrainingLogic(private  val network : NeuralNetwork, private val data : DescribedData, private val packages : Int, private val packageSize : Int, private val packageRepetitions : Int, private val learningRate : Double) : Logic, Observable{

    override val observers: MutableList<Observer> = mutableListOf()
    override var state : NetworkState? = null

    override fun run() {
        require(network.layers[0] == data.getInputSize())
        val percentFormatter = DecimalFormat("#0.00")
        val minutesFormatter = DecimalFormat("###0")
        val secondsFormatter = DecimalFormat("00")

        var result : Array<Double> = arrayOf()
        network.learningRate = learningRate

        for (l in 1.rangeTo(packages)) {
            val dataPack = createDataPack(packageSize, data.getDataSize())
            val tik = System.currentTimeMillis()
            for (rep in 0 until packageRepetitions) {
                for (element in dataPack) {
                    network.predict(data.getData(element).second)
                    network.learnNetwork(data.getData(element).first.toInt())
                }
            }
            var correct = 0
            for (element in dataPack) {
                result = network.predict(data.getData(element).second)
                if (result.indexOf(result.max()) == data.getData(element).first.toInt())
                    correct++
            }
            val tok = System.currentTimeMillis()

            //Observer implementation
            val packagesLeft = packages - l
            state = NetworkState(
                    currentInputData = dataPack.last(),
                    prediction = result.indexOf(result.max()),
                    percentageOfCompleted = percentFormatter.format((l / packages.toDouble()) * 100),
                    left = "$packagesLeft",
                    efficiencyInPercentage = percentFormatter.format((correct / dataPack.size.toDouble()) * 100),
                    timeLeft = "${minutesFormatter.format(packagesLeft.toDouble() * -(tik - tok) / 60000)}:" +
                            secondsFormatter.format(packagesLeft.toDouble() * -(tik - tok) % 60000 / 1000)
            )
            notifyObservers()
        }
    }


    private fun createDataPack(packagesSize: Int, dataSize: Int): IntArray {
        val pack = IntArray(packagesSize)
        val rand = Random()
        for (i in pack.indices) {
            pack[i] = rand.nextInt(dataSize)
        }
        return pack
    }
}