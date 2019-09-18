package dataLoadersPackage.csvLoader

class Data(filePath : String){

    private var dataset : Array<Pair<Double,Array<Double>>> = arrayOf()

    init{
        val extension = Regex("\\.\\w+").find(filePath)?.value
        for(pair in SupportedFiles.supportedFiles){
            if(pair.first == extension){
                dataset = pair.second.open(filePath)
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