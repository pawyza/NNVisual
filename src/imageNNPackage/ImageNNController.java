package imageNNPackage;

import dataLoadersPackage.csvLoader.Data;
import dataLoadersPackage.csvLoader.ImageCreator;
import imageNNPackage.logic.NeuralNetwork;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import kotlin.Pair;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class ImageNNController implements Initializable {

    @FXML
    private TextField testDataPath_txt;

    @FXML
    private Button testNN_btn;

    @FXML
    private TextField testExampleID_txt;

    @FXML
    private TextField testExamplePrediction_txt;

    @FXML
    private ImageView testsExample_img;

    @FXML
    private ImageView testsExampleNNState_img;

    @FXML
    private TextField testsLeft_txt;

    @FXML
    private TextField testTimeLeft_txt;

    @FXML
    private TextField testCompleted_txt;

    @FXML
    private TextField testEfficiency_txt;

    @FXML
    private TextField trainDataPath_txt;

    @FXML
    private TextField packagesNumber_txt;

    @FXML
    private TextField packageSize_txt;

    @FXML
    private TextField packageRepetitions_txt;

    @FXML
    private TextField learningRate_txt;

    @FXML
    private Button trainNN_btn;

    @FXML
    private TextField packagesLeft_txt;

    @FXML
    private TextField trainingTimeLeft_txt;

    @FXML
    private TextField trainingCompleted_txt;

    @FXML
    private TextField lastPackageEfficiency_txt;

    @FXML
    private TextField saveNNPath_txt;

    @FXML
    private ImageView customData_img;

    @FXML
    private ImageView customDataNNState_img;

    @FXML
    private TextField customDataPrediction_txt;

    @FXML
    private MenuItem saveModel_barBtn;

    private Double learningRate;
    private Integer[] layers;
    private Integer imageWidth;
    private NeuralNetwork network;

    public ImageNNController(Integer imageWidth,Integer[] layers, Double learningRate){
        this.learningRate = learningRate;
        this.layers = layers;
        this.imageWidth = imageWidth;
        network = new NeuralNetwork(layers, learningRate);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        learningRate_txt.setText(String.valueOf(learningRate));
        //TODO sprawdzanie poprawnosci wpisywanych ustawien i wybieranych sciezek, poprawianie/wyswietlanie komuniktow analogiczne do tworzenia modelu
    }


    @FXML
    void chooseSaveFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        saveNNPath_txt.setText(file.getPath());
        saveNNPath_txt.setEditable(false);
    }

    @FXML
    void chooseTestFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        testDataPath_txt.setText(file.getPath());
    }

    @FXML
    void chooseTrainFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        trainDataPath_txt.setText(file.getPath());
    }

    @FXML
    void createNewModelFromBar(ActionEvent event) {

    }

    @FXML
    void loadModelFromBar(ActionEvent event) {

    }

    @FXML
    void testNN(ActionEvent event) {
        try {
            Data data = new Data(testDataPath_txt.getText());
            //TODO to chyba bedzie trzeba zrobic w osobnym watku zeby miec mozliwosc zastosowania observera.
            new ImageNNTestingLogic().test(network, data, layers);
            //TODO ustawianie ponowne przycisku
            testNN_btn.setDisable(true);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void updateExample(int i, Pair<Double, Double[]> dataExample,int inputSize) {
        testExampleID_txt.setText(String.valueOf(i));
        testExamplePrediction_txt.setText(String.valueOf(dataExample.getFirst().intValue()));
        int scale = (int) testsExample_img.getFitHeight()/inputSize;
        testsExample_img.setImage(SwingFXUtils.toFXImage(ImageCreator.INSTANCE.displayData(dataExample.getSecond(),scale),null));
    }

    @FXML
    void save(ActionEvent event) {

    }

    @FXML
    void saveModel(ActionEvent event) {

    }

    @FXML
    void showInfo(ActionEvent event) {

    }

    @FXML
    void trainNN(ActionEvent event) {
        try {
            Data data = new Data(trainDataPath_txt.getText());
            int packageNumber = Integer.valueOf(packagesNumber_txt.getText());
            int packageSize = Integer.valueOf(packageSize_txt.getText());
            int packageRepetitions = Integer.valueOf(packageRepetitions_txt.getText());
            int learningRate = Integer.valueOf(learningRate_txt.getText());
            //TODO to chyba bedzie trzeba zrobic w osobnym watku zeby miec mozliwosc zastosowania observera.
            new ImageNNTrainingLogic().train(network, data, layers, packageNumber, packageSize, packageRepetitions, learningRate);
            //TODO ustawianie ponowne przycisku
            trainNN_btn.setDisable(true);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}
