package dataLoadersPackage.csvLoader

object SupportedFiles{
    val supportedFiles : List<Pair<String,OpeningStrategy>> = listOf(Pair(".csv",StrategyCSV()))
    val extensionsList = supportedFiles.map{"*" + it.first}.toList();
}