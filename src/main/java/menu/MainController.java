package menu;

import data.representation.ImageCreator;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController{

    @FXML
    private MenuItem saveModel_barBtn;

    @FXML
    private Button newModel_btn;

    @FXML
    private Button loadModel_btn;

    @FXML
    private Button deleteModel_btn;

    @FXML
    private TableView<ModelRepresentation> modelsTable_tab;

    @FXML
    private TableColumn<ModelRepresentation, String> modelName_col;

    @FXML
    private TableColumn<ModelRepresentation, String> modelType_col;

    @FXML
    void createNewModel(ActionEvent event) {
        Stage initializationStage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/newNNModel.fxml"));
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
    void deleteModel(ActionEvent event) {
        ModelRepresentation model= modelsTable_tab.getSelectionModel().getSelectedItem();
    }

    @FXML
    void loadModel(ActionEvent event) {
        ModelRepresentation model= modelsTable_tab.getSelectionModel().getSelectedItem();
    }

    @FXML
    void showInfo(ActionEvent event) {

    }

    private class ModelRepresentation{
        private String name;
        private String type;

        public ModelRepresentation(String name, String type) {
            this.name = name;
            this.type = type;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
