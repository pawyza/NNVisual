package neuralNetworkModelsPackage.imageNNPackage;

import dataLoadersPackage.csvLoader.Data;
import dataLoadersPackage.csvLoader.ImageCreator;
import javafx.scene.text.Text;
import neuralNetworkModelsPackage.neuralNetworkLogic.NeuralNetwork;
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
import neuralNetworkModelsPackage.observerPatterLogic.Observable;
import neuralNetworkModelsPackage.observerPatterLogic.Observer;
import java.io.File;
import java.net.URL;
import java.util.*;

public class ImageNNController implements Initializable {

    //<editor-fold desc="FXML controls">
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
    private Text messageText_txt;

    @FXML
    private TextField saveNNPath_txt;

    @FXML
    private Button save_btn;

    @FXML
    private TextField trainingPackagesLeft_txt;

    @FXML
    private TextField trainingTimeLeft_txt;

    @FXML
    private TextField trainingCompleted_txt;

    @FXML
    private TextField trainingLastPackageEfficiency_txt;

    @FXML
    private ImageView customData_img;

    @FXML
    private ImageView customDataNNState_img;

    @FXML
    private TextField customDataPrediction_txt;

    @FXML
    private MenuItem saveModel_barBtn;
    //</editor-fold>

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
        //TODO do usuniecia
        testSetup();
        //TODO sprawdzanie poprawnosci wpisywanych ustawien i wybieranych sciezek, poprawianie/wyswietlanie komuniktow analogiczne do tworzenia modelu
    }


    @FXML
    void chooseSaveFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null)
            saveNNPath_txt.setText(file.getPath());
    }

    @FXML
    void chooseTestFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null)
            testDataPath_txt.setText(file.getPath());
    }

    @FXML
    void chooseTrainFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if(file != null)
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
        testNN_btn.setDisable(false);
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
            //TODO dane trzeba bedzie chyba robic w innym watku i zrobic observera(inaczej nie bedzie dzialac komunikat)
            messageText_txt.setText("Loading data");
            Data data = new Data(trainDataPath_txt.getText());
            messageText_txt.setText("");

            int packageNumber = Integer.valueOf(packagesNumber_txt.getText());
            int packageSize = Integer.valueOf(packageSize_txt.getText());
            int packageRepetitions = Integer.valueOf(packageRepetitions_txt.getText());
            double learningRate = Double.valueOf(learningRate_txt.getText());
            TextField[] textControls = {trainingCompleted_txt,trainingPackagesLeft_txt,trainingLastPackageEfficiency_txt,trainingTimeLeft_txt};

            ImageNNTrainingLogic trainingLogic = new ImageNNTrainingLogic(network, data, layers, packageNumber, packageSize, packageRepetitions, learningRate);

            class TrainingObserver implements Observer {

                private final Observable observed;

                private TrainingObserver(Observable observed){
                    this.observed = observed;
                }

                @Override
                public Observable getObserved() {
                    return observed;
                }

                @Override
                public void update() {
                    trainingCompleted_txt.setText(observed.getState().getPercentageOfCompleted() + " %");
                    trainingLastPackageEfficiency_txt.setText(observed.getState().getEfficiencyInPercentage() + " %");
                    trainingPackagesLeft_txt.setText(observed.getState().getLeft());
                    trainingTimeLeft_txt.setText(observed.getState().getTimeLeft());
                }
            }

            trainingCompleted_txt.setDisable(false);
            trainingLastPackageEfficiency_txt.setDisable(false);
            trainingPackagesLeft_txt.setDisable(false);
            trainingTimeLeft_txt.setDisable(false);
            TrainingObserver trainingObserver = new TrainingObserver(trainingLogic);
            trainingLogic.addObserver(trainingObserver);
            trainingLogic.start();

            //TODO ustawianie ponowne przycisku
            trainNN_btn.setDisable(true);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        trainNN_btn.setDisable(false);
    }

    //TODO do usuniecia
    private void testSetup(){
        testDataPath_txt.setText("D:\\nnLearning\\mnist_test.csv");
        trainDataPath_txt.setText("D:\\nnLearning\\mnist_train.csv");
        packagesNumber_txt.setText("100");
        packageSize_txt.setText("10");
        packageRepetitions_txt.setText("10");
    }
}
