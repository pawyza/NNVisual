package neuralNetworkModelsPackage.imageNNPackage;

import dataLoadersPackage.csvLoader.Data;
import dataLoadersPackage.csvLoader.ImageCreator;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.text.Text;
import neuralNetworkModelsPackage.interfaces.Logic;
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
import neuralNetworkModelsPackage.interfaces.Observable;
import neuralNetworkModelsPackage.interfaces.Observer;
import neuralNetworkModelsPackage.services.DataService;
import neuralNetworkModelsPackage.services.LogicService;

import java.io.File;
import java.net.URL;
import java.util.*;

//TODO rozwiazac problem bledow dla zbyt szybkiej pracy nn i kontrolek z observera
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
            messageText_txt.setText("Loading data");
            testNN_btn.setDisable(true);
            Service dataService = new DataService(testDataPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                messageText_txt.setText("Testing network");
                Data data = ((DataService) dataService).getValue();
                setViewsIndicators(false, testCompleted_txt,testEfficiency_txt,testsLeft_txt,testTimeLeft_txt);

                ImageNNTestingLogic testingLogic = new ImageNNTestingLogic(network, data, layers);

                Observer testingObserver = prepareObserver(testingLogic,testCompleted_txt,testEfficiency_txt,testsLeft_txt,testTimeLeft_txt);
                testingLogic.addObserver(testingObserver);

                //testingLogic.run();

                Service testingService = new LogicService(testingLogic);
                testingService.start();


                testingService.setOnSucceeded(logicEvent -> {
                    //System.out.println(logicEvent.getEventType().getName());
                    setViewsIndicators(true, testCompleted_txt, testEfficiency_txt, testsLeft_txt ,testTimeLeft_txt);
                    messageText_txt.setText("Testing done successfully");
                    testNN_btn.setDisable(false);
                });

                testingService.setOnFailed(logicEvent -> {
                    //System.out.println(logicEvent.getEventType().getName());
                    setViewsIndicators(true, testCompleted_txt, testEfficiency_txt, testsLeft_txt ,testTimeLeft_txt);
                    messageText_txt.setText("Testing failed");
                    testNN_btn.setDisable(false);
                    });
                });
            dataService.setOnFailed(dataEvent -> {
                setViewsIndicators(true, testCompleted_txt, testEfficiency_txt, testsLeft_txt ,testTimeLeft_txt);
                messageText_txt.setText("Error while loading data");
                testNN_btn.setDisable(false);
                });

        } catch (IllegalArgumentException e){
            e.printStackTrace();
            testNN_btn.setDisable(false);
            messageText_txt.setText("Error");
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
            int packageNumber = Integer.valueOf(packagesNumber_txt.getText());
            int packageSize = Integer.valueOf(packageSize_txt.getText());
            int packageRepetitions = Integer.valueOf(packageRepetitions_txt.getText());
            double learningRate = Double.valueOf(learningRate_txt.getText());
            messageText_txt.setText("Loading data");
            trainNN_btn.setDisable(true);

            Service dataService = new DataService(trainDataPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                messageText_txt.setText("Training network");
                Data data = ((DataService) dataService).getValue();
                setViewsIndicators(false, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt);

                ImageNNTrainingLogic trainingLogic = new ImageNNTrainingLogic(network, data, layers, packageNumber, packageSize, packageRepetitions, learningRate);
                Observer trainingObserver = prepareObserver(trainingLogic ,trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt);
                trainingLogic.addObserver(trainingObserver);

                Service logicService = new LogicService(trainingLogic);
                logicService.start();

                logicService.setOnSucceeded(logicEvent ->{
                    setViewsIndicators(true, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt);
                    messageText_txt.setText("Training done successfully");
                    trainNN_btn.setDisable(false);
                });
                logicService.setOnFailed(logicEvent -> {
                    setViewsIndicators(true, trainingCompleted_txt, trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt);
                    messageText_txt.setText("Training failed");
                    trainNN_btn.setDisable(false);
                });
            });
            dataService.setOnFailed(dataEvent -> {
                setViewsIndicators(true, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt);
                messageText_txt.setText("Error while loading data");
                trainNN_btn.setDisable(false);
            });
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            trainNN_btn.setDisable(false);
            messageText_txt.setText("Error");
        }
    }

    private void setViewsIndicators(boolean state, TextField completed, TextField efficiency, TextField left, TextField timeLeft){
        completed.setDisable(state);
        efficiency.setDisable(state);
        left.setDisable(state);
        timeLeft.setDisable(state);
    }

    private Observer prepareObserver(Observable logic ,TextField completed, TextField efficiency, TextField left, TextField timeLeft){

        class ObserverObject implements Observer {

            private final Observable observed;

            private ObserverObject(Observable observed){
                this.observed = observed;
            }

            @Override
            public Observable getObserved() {
                return observed;
            }

            @Override
            public void update() {
                completed.setText(observed.getState().getPercentageOfCompleted() + " %");
                efficiency.setText(observed.getState().getEfficiencyInPercentage() + " %");
                left.setText(observed.getState().getLeft());
                timeLeft.setText(observed.getState().getTimeLeft());
            }
        }

        ObserverObject observerObject = new ObserverObject(logic);

        return observerObject;
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
