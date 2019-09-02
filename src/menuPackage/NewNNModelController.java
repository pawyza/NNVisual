package menuPackage;

import imageNNPackage.ImageNNController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NewNNModelController implements Initializable {

    private ObservableList<String> nnModels = FXCollections.observableArrayList("Image GrayScale");
    private Stage stageReference;

    NewNNModelController(Stage stageReference){
        this.stageReference = stageReference;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nnType_chbox.setItems(nnModels);
        nnType_chbox.setValue(nnModels.get(0));
    }

    @FXML
    private ChoiceBox<String> nnType_chbox;

    @FXML
    private Button next_btn;

    @FXML
    void onNext(ActionEvent event) {
        Stage modelCreationStage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        try {
            FXMLLoader loader;
            switch(nnType_chbox.getValue()) {
                case "Image GrayScale":
                    loader = new FXMLLoader(getClass().getResource("/menuPackage/imageNNInitializer.fxml"));
                    loader.setControllerFactory(controller -> new ImageNNInitializerController(stageReference));
                    break;
                default:
                    throw new IllegalStateException();
            }
            modelCreationStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println("Cannot find resources.");
            e.printStackTrace();
        }
    }

}
