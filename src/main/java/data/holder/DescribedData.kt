package data.holder

import data.loaders.SupportedFiles

class DescribedData(filePath : String){

    private var dataset : Array<Pair<Double,Array<Double>>> = arrayOf();

    init{
        val extension = Regex("\\.\\w+").find(filePath)?.value
        for(pair in SupportedFiles.supportedFiles){
            if(pair.first == extension){
                val data = pair.second.open(filePath)
                dataset = Array(data.size){i -> Pair(data[i][0],data[i].copyOfRange(1,data[i].size).map{it/255}.toTypedArray())}
            }
        }
        if(dataset.isEmpty()){
            throw ClassNotFoundException("Opening strategy not found.")
        }
    }

    fun getData(i : Int) : Pair<Double,Array<Double>> = dataset[i]
    fun getDataSize() : Int = dataset.size
    fun getInputSize() : Int = dataset[0].second.size
}