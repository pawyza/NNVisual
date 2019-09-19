module NNVisual {
    requires javafx.fxml;
    requires javafx.controls;
    requires kotlin.stdlib;
    requires java.desktop;
    requires javafx.swing;
    requires kotlinx.serialization.runtime;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    opens menuPackage;
    opens neuralNetworkModelsPackage.imageNNPackage;
}