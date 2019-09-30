package data.loaders.describedLoader

import data.loaders.OpeningStrategy
import java.io.File
import java.util.*

class StrategyCSV : OpeningStrategy {

    override fun open(filePath : String) : Array<Array<Double>>{
        val scanner = Scanner(File(filePath))
        val result : MutableList<Array<Double>> = mutableListOf()
        while (scanner.hasNext()) {
            val data = scanner.next().split(",").toMutableList()
            result.add(data.map { it.toDouble() }.toTypedArray())
            //result.add(Pair(data.removeAt(0).toDouble(),data.map { it.toDouble()/255 }.toTypedArray()))
        }
        return result.toTypedArray()
    }
}