package data.loaders

interface OpeningStrategy{
    fun open(filePath : String) : Array<Array<Double>>
}