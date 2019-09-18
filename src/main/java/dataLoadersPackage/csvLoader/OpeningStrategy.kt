package dataLoadersPackage.csvLoader

interface OpeningStrategy{
    fun open(filePath : String) : Array<Pair<Double,Array<Double>>>
}