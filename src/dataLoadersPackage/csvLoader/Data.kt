package dataLoadersPackage.csvLoader

class Data(filePath : String){

    //trzeba dodac wykrywanie formatu pliku

    private var dataset : Array<Pair<Double,Array<Double>>> = arrayOf()

    init{
        dataset = CSVopener.loadFile(filePath)
        //dataset = CSVopener().loadFile(filePath).mapIndexed{ index, data -> Pair(data.first,Array(28){row -> Array(28){pixel -> data.second[row*28+pixel]}})}.toTypedArray()
        /*println(dataset.joinToString("\n\n") {"Value: ${it.first}\n" +
                it.second.joinToString("\n") { row -> row.joinToString(" ")}
        })
        println(CSVopener().loadFile(filePath).mapIndexed{ index, data -> Pair(data.first,Array(28){row -> Array(28){pixel -> data.second[row*28+pixel]}})}.toTypedArray().joinToString("\n\n") {"Value: ${it.first}\n" +
                    it.second.joinToString("\n") { row -> row.joinToString(" ")}})*/
    }

    fun getData(i : Int) : Pair<Double,Array<Double>> = dataset[i]
    fun getDataSize() : Int = dataset.size
    fun getInputSize() : Int = dataset[0].second.size
}