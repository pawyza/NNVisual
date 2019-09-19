module NNVisual {
    requires javafx.fxml;
    requires javafx.controls;
    requires kotlin.stdlib;
    requires java.desktop;
    requires javafx.swing;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.module.kotlin;

    exports neuralNetworkModelsPackage.neuralNetworkLogic;

    opens menuPackage;
    opens neuralNetworkModelsPackage.neuralNetworkLogic;
    opens neuralNetworkModelsPackage.imageNNPackage;
}