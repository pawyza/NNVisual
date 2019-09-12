package menuPackage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private MenuItem saveModel_barBtn;

    @FXML
    private Button newModel_btn;

    @FXML
    private Button loadModel_btn;

    @FXML
    private Button deleteModel_btn;

    @FXML
    void createNewModel(ActionEvent event) {
        Stage initializationStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menuPackage/newNNModel.fxml"));
            loader.setControllerFactory(controller -> new NewNNModelController((Stage)(((Node)event.getSource()).getScene().getWindow())));
            initializationStage.setResizable(false);
            initializationStage.setTitle("Model initialization");
            initializationStage.setScene(new Scene(loader.load()));
            initializationStage.show();
        } catch (IOException e) {
            System.out.println("Cannot find resources.");
            e.printStackTrace();
        }
    }

    @FXML
    void createNewModelFromBar(ActionEvent event) {
    }

    @FXML
    void deleteModel(ActionEvent event) {

    }

    @FXML
    void loadModel(ActionEvent event) {

    }

    @FXML
    void loadModelFromBar(ActionEvent event) {

    }

    @FXML
    void saveModel(ActionEvent event) {

    }

    @FXML
    void showInfo(ActionEvent event) {

    }

}
