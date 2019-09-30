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

    exports modelsLogic.neuralNetworkLogic;
    exports modelsLogic.persistence;

    opens menu;
    opens modelsLogic.neuralNetworkLogic;
    opens modelsLogic.models.imageNN;
}