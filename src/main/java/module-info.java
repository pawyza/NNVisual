module NNVisual {
    requires javafx.fxml;
    requires javafx.controls;
    requires kotlin.stdlib;
    requires java.desktop;
    requires javafx.swing;

    opens menuPackage;
    opens neuralNetworkModelsPackage.imageNNPackage;
}