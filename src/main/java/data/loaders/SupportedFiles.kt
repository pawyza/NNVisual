package data.loaders

import data.loaders.describedLoader.StrategyCSV

object SupportedFiles{
    val supportedFiles : List<Pair<String, OpeningStrategy>> = listOf(Pair(".csv", StrategyCSV()))
    val extensionsList = supportedFiles.map{"*" + it.first}.toList();
}