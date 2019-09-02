package menuPackage;

import imageNNPackage.ImageNNController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import kotlin.text.Regex;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ImageNNInitializerController implements Initializable {

    private Stage stageReference;

    ImageNNInitializerController(Stage stageReference){
        this.stageReference = stageReference;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageHeight_spin.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue <= 0){
                imageHeight_spin.cancelEdit();
            }
            if(imageWidth_spin.getValue() > 0){
                inputLayer_txtField.setText(String.valueOf(imageHeight_spin.getValue()*imageHeight_spin.getValue()));
            }
        });

        imageWidth_spin.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue <= 0){
                imageWidth_spin.cancelEdit();
            }
            if(imageHeight_spin.getValue() > 0){
                inputLayer_txtField.setText(String.valueOf(imageHeight_spin.getValue()*imageHeight_spin.getValue()));
            }
        });

        hiddenLayersNumber_spin.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(newValue <= 0){
                hiddenLayersNumber_spin.cancelEdit();
            } else {
                hiddenLayer_txtField.setText("0,".repeat(newValue).substring(0,2*newValue-1));
            }
        });

        hiddenLayer_txtField.textProperty().addListener((obs, oldValue, newValue) ->{{
            if(!new Regex("[^0-9,]+").containsMatchIn(newValue)){
                outputLayer_txtField.cancelEdit();
            }
        }});

        outputLayer_txtField.textProperty().addListener((obs, oldValue, newValue) ->{
            if(!new Regex("D+").containsMatchIn(newValue)){
                outputLayer_txtField.cancelEdit();
            }
        });
    }

    @FXML
    private Spinner<Integer> imageHeight_spin;

    @FXML
    private Spinner<Integer> imageWidth_spin;

    @FXML
    private Spinner<Integer> hiddenLayersNumber_spin;

    @FXML
    private TextField inputLayer_txtField;

    @FXML
    private TextField hiddenLayer_txtField;

    @FXML
    private TextField outputLayer_txtField;

    @FXML
    private Spinner<Double> learningRate_spin;

    @FXML
    private Button create_btn;


    @FXML
    void onCreate(ActionEvent event) {
        Stage initializationStage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/imageNNPackage/imageNN.fxml"));
            loader.setControllerFactory(controller -> new ImageNNController(imageWidth_spin.getValue(),createLayers(),learningRate_spin.getValue()));
            stageReference.setScene(new Scene(loader.load()));
            initializationStage.close();
        } catch (IOException e) {
            System.out.println("Cannot find resources.");
            e.printStackTrace();
        }
    }

    private Integer[] createLayers(){
        List<Integer> layerList = new ArrayList<>(Integer.valueOf(inputLayer_txtField.getText()));
        for(String value : hiddenLayer_txtField.getText().split(",")){
            layerList.add(Integer.valueOf(value));
        }
        layerList.add(Integer.valueOf(outputLayer_txtField.getText()));
        return layerList.toArray(new Integer[layerList.size()]);
    }
}
