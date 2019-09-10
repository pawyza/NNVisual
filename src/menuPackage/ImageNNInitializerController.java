package menuPackage;

import imageNNPackage.ImageNNController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import kotlin.text.Regex;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class ImageNNInitializerController implements Initializable {

    private Stage stageReference;

    ImageNNInitializerController(Stage stageReference){
        this.stageReference = stageReference;
    }

    private String heightLastValue = "1";
    private String widthLastValue = "1";
    private String learningRateLastValue = "0.000";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageHeight_spin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE));
        imageWidth_spin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE));

        SpinnerValueFactory spvDouble = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0,1.0,0.0,0.001);
        spvDouble.setConverter(new DoubleStringConverter());
        learningRate_spin.setValueFactory(spvDouble);

        EventHandler<KeyEvent> keyEventHandler_LR = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(new Regex("[^0-9.]").matches(keyEvent.getText()))
                    learningRate_spin.getEditor().textProperty().set(learningRateLastValue);
                learningRateLastValue = learningRate_spin.getEditor().textProperty().get();
            }
        };

        learningRate_spin.getEditor().addEventHandler(KeyEvent.KEY_RELEASED,keyEventHandler_LR);

        EventHandler<KeyEvent> keyEventHandler_IH = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(new Regex("/D").matches(keyEvent.getText()))
                    imageHeight_spin.getEditor().textProperty().set(heightLastValue);
                try {
                    if(Integer.parseInt(imageHeight_spin.getEditor().textProperty().get()) < 1) throw new NumberFormatException();;
                    heightLastValue = imageHeight_spin.getEditor().textProperty().get();
                } catch (NumberFormatException e){
                    imageHeight_spin.getEditor().textProperty().set(heightLastValue);
                }
            }
        };

        imageHeight_spin.getEditor().addEventHandler(KeyEvent.KEY_RELEASED,keyEventHandler_IH);

        EventHandler<KeyEvent> keyEventHandler_IW = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if(new Regex("/D").matches(keyEvent.getText()))
                    imageWidth_spin.getEditor().textProperty().set(widthLastValue);
                try {
                    if(Integer.parseInt(imageWidth_spin.getEditor().textProperty().get()) < 1) throw new NumberFormatException();
                    widthLastValue = imageWidth_spin.getEditor().textProperty().get();
                } catch (NumberFormatException e){
                    imageWidth_spin.getEditor().textProperty().set(widthLastValue);
                }
            }
        };

        imageWidth_spin.getEditor().addEventHandler(KeyEvent.KEY_RELEASED,keyEventHandler_IW);
        
        learningRate_spin.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!new Regex("\\d.\\d+").matches(newValue.toString())){
                learningRate_spin.getValueFactory().setValue(oldValue);
            }
            checkSetup();
        });

        inputLayer_txtField.setText("1");
        outputLayer_txtField.setText("0");

        imageHeight_spin.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(imageWidth_spin.getValue() > 0){
                inputLayer_txtField.setText(String.valueOf(imageHeight_spin.getValue()*imageWidth_spin.getValue()));
            }
            checkSetup();
        });

        imageWidth_spin.valueProperty().addListener((obs, oldValue, newValue) ->{
            if(imageHeight_spin.getValue() > 0){
                inputLayer_txtField.setText(String.valueOf(imageHeight_spin.getValue()*imageWidth_spin.getValue()));
            }
            checkSetup();
        });

        hiddenLayer_txtField.textProperty().addListener((obs, oldValue, newValue) ->{{
            if(new Regex("[^0-9,]").containsMatchIn(newValue)){
                hiddenLayer_txtField.setText(oldValue);
            }
            checkSetup();
        }});

        outputLayer_txtField.textProperty().addListener((obs, oldValue, newValue) ->{
            if(new Regex("\\D").containsMatchIn(newValue)){
                outputLayer_txtField.setText(oldValue);
            }
            checkSetup();
        });

        testPurposeInit();
    }

    private void checkSetup() {
        Integer[] layers = getLayers();

        create_btn.setDisable(true);

        if(layers.length < 3)
            return;

        for (Integer i: layers) {
            if(i == 0){ return; }
        }

        if(learningRate_spin.getValueFactory().getValue() == 0.0)
            return;

        //TODO usunac po zakonczeniu testow
        create_btn.setDisable(false);
    }

    private Integer[] getLayers(){
        List<Integer> hiddenLayer = new LinkedList<>();

        String input = inputLayer_txtField.getText();
        String output = outputLayer_txtField.getText();

        if(!input.isEmpty()) hiddenLayer.add(Integer.valueOf(input));
        for(String layer : hiddenLayer_txtField.getText().split(",")){
            if(!layer.isEmpty())
                hiddenLayer.add(Integer.valueOf(layer));
        }
        if(!output.isEmpty()) hiddenLayer.add(Integer.valueOf(output));

        return hiddenLayer.toArray(new Integer[hiddenLayer.size()]);
    }

    @FXML
    private Spinner<Integer> imageHeight_spin;

    @FXML
    private Spinner<Integer> imageWidth_spin;

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
            loader.setControllerFactory(controller -> new ImageNNController(imageWidth_spin.getValue(),getLayers(),learningRate_spin.getValue()));
            stageReference.setScene(new Scene(loader.load()));
            initializationStage.close();
        } catch (IOException e) {
            System.out.println("Cannot find resources.");
            e.printStackTrace();
        }
    }

    private void testPurposeInit() {
        inputLayer_txtField.setText("784");
        hiddenLayer_txtField.setText("15");
        outputLayer_txtField.setText("10");
        learningRate_spin.getValueFactory().setValue(0.4);
        checkSetup();
    }
}
