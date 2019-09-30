package data.holder

import data.loaders.SupportedFiles

class NotDescribedData(filePath : String){

    private var dataset : Array<Array<Double>> = arrayOf();

    init{
        val extension = Regex("\\.\\w+").find(filePath)?.value
        for(pair in SupportedFiles.supportedFiles){
            if(pair.first == extension){
                val data = pair.second.open(filePath)
                dataset = Array(data.size){i -> data[i].map{it/255}.toTypedArray()}
            }
        }
        if(dataset.isEmpty()){
            throw ClassNotFoundException("Opening strategy not found.")
        }
    }

    fun getData(i : Int) : Array<Double> = dataset[i]
    fun getDataSize() : Int = dataset.size
    fun getInputSize() : Int = dataset[0].size
}