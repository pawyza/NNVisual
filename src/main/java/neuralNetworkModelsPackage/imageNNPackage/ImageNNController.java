package neuralNetworkModelsPackage.imageNNPackage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import dataLoadersPackage.csvLoader.Data;
import dataLoadersPackage.csvLoader.ImageCreator;
import dataLoadersPackage.csvLoader.SupportedFiles;
import javafx.concurrent.Service;
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
    private Text messageTest_txt;

    @FXML
    private Text messageTrain_txt;

    @FXML
    private ImageView trainingExample_img;

    @FXML
    private ImageView trainingExampleNNState_img;

    @FXML
    private TextField trainingExamplePrediction_txt;

    @FXML
    private TextField trainingExampleID_txt;

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

    private FileChooser.ExtensionFilter supportedExtension;

    private Integer imageWidth;
    private NeuralNetwork network;

    public ImageNNController(Integer imageWidth,Integer[] layers, Double learningRate){
        this.imageWidth = imageWidth;
        supportedExtension = new FileChooser.ExtensionFilter("Data" , SupportedFiles.INSTANCE.getExtensionsList());
        network = new NeuralNetwork(layers, learningRate);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        learningRate_txt.setText(String.valueOf(network.getLearningRate()));
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
        File file = getFile();
        if(file != null)
            testDataPath_txt.setText(file.getPath());
    }

    @FXML
    void chooseTrainFilePath(ActionEvent event) {
        File file = getFile();
        if(file != null)
            trainDataPath_txt.setText(file.getPath());
    }

    private File getFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(supportedExtension);
        return fileChooser.showOpenDialog(null);
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
            messageTest_txt.setText("Loading data");
            testNN_btn.setDisable(true);
            Service dataService = new DataService(testDataPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                messageTest_txt.setText("Testing network");
                Data data = ((DataService) dataService).getValue();
                setViewsIndicators(false, testCompleted_txt,testEfficiency_txt,testsLeft_txt,testTimeLeft_txt,testExampleID_txt,testExamplePrediction_txt);

                ImageNNTestingLogic testingLogic = new ImageNNTestingLogic(network, data, network.getLayers());

                Observer testingObserver = prepareObserver(testingLogic,data,testCompleted_txt,testEfficiency_txt,testsLeft_txt,testTimeLeft_txt,testsExample_img,testExampleID_txt,testExamplePrediction_txt);
                testingLogic.addObserver(testingObserver);

                Service testingService = new LogicService(testingLogic);
                testingService.start();


                testingService.setOnSucceeded(logicEvent -> {
                    //System.out.println(logicEvent.getEventType().getName());
                    setViewsIndicators(true, testCompleted_txt, testEfficiency_txt, testsLeft_txt ,testTimeLeft_txt,testExampleID_txt,testExamplePrediction_txt);
                    messageTest_txt.setText("Testing done successfully");
                    testNN_btn.setDisable(false);
                });

                testingService.setOnFailed(logicEvent -> {
                    //System.out.println(logicEvent.getEventType().getName());
                    setViewsIndicators(true, testCompleted_txt, testEfficiency_txt, testsLeft_txt ,testTimeLeft_txt,testExampleID_txt,testExamplePrediction_txt);
                    messageTest_txt.setText("Testing failed");
                    testNN_btn.setDisable(false);
                    });
                });
            dataService.setOnFailed(dataEvent -> {
                setViewsIndicators(true, testCompleted_txt, testEfficiency_txt, testsLeft_txt ,testTimeLeft_txt,testExampleID_txt,testExamplePrediction_txt);
                messageTest_txt.setText("Error while loading data");
                testNN_btn.setDisable(false);
                });

        } catch (IllegalArgumentException e){
            e.printStackTrace();
            testNN_btn.setDisable(false);
            messageTest_txt.setText("Error");
        }
    }

    @FXML
    void saveModelFromTrain(ActionEvent event) {
        save();
    }

    @FXML
    void saveModelFromBar(ActionEvent event) {
        save();
    }

    private void save(){
        class JacksonNetworkModel{
            private Integer imageWidth;
            private NeuralNetwork network;
            private JacksonNetworkModel(Integer imageWidth, NeuralNetwork network) {
                this.imageWidth = imageWidth;
                this.network = network;
            }

            public Integer getImageWidth() {
                return imageWidth;
            }

            public void setImageWidth(Integer imageWidth) {
                this.imageWidth = imageWidth;
            }

            public NeuralNetwork getNetwork() {
                return network;
            }

            public void setNetwork(NeuralNetwork network) {
                this.network = network;
            }
        }
        JacksonNetworkModel model = new JacksonNetworkModel(imageWidth,network);
        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new KotlinModule());
        try {
            System.out.println(jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
            messageTrain_txt.setText("Loading data");
            trainNN_btn.setDisable(true);

            Service dataService = new DataService(trainDataPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                messageTrain_txt.setText("Training network");
                Data data = ((DataService) dataService).getValue();
                setViewsIndicators(false, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExampleID_txt, trainingExamplePrediction_txt);

                ImageNNTrainingLogic trainingLogic = new ImageNNTrainingLogic(network, data, network.getLayers(), packageNumber, packageSize, packageRepetitions, learningRate);
                Observer trainingObserver = prepareObserver(trainingLogic, data,trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExample_img, trainingExampleID_txt, trainingExamplePrediction_txt);
                trainingLogic.addObserver(trainingObserver);

                Service logicService = new LogicService(trainingLogic);
                logicService.start();

                logicService.setOnSucceeded(logicEvent ->{
                    setViewsIndicators(true, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExampleID_txt, trainingExamplePrediction_txt);
                    messageTrain_txt.setText("Training done successfully");
                    trainNN_btn.setDisable(false);
                });
                logicService.setOnFailed(logicEvent -> {
                    setViewsIndicators(true, trainingCompleted_txt, trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExampleID_txt, trainingExamplePrediction_txt);
                    messageTrain_txt.setText("Training failed");
                    trainNN_btn.setDisable(false);
                });
            });
            dataService.setOnFailed(dataEvent -> {
                setViewsIndicators(true, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExampleID_txt, trainingExamplePrediction_txt);
                messageTrain_txt.setText("Error while loading data");
                trainNN_btn.setDisable(false);
            });
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            trainNN_btn.setDisable(false);
            messageTrain_txt.setText("Error");
        }
    }

    private void setViewsIndicators(boolean state, TextField completed, TextField efficiency, TextField left, TextField timeLeft, TextField id, TextField prediction){
        id.setDisable(state);
        prediction.setDisable(state);
        completed.setDisable(state);
        efficiency.setDisable(state);
        left.setDisable(state);
        timeLeft.setDisable(state);
    }

    private Observer prepareObserver(Observable logic, Data dataset, TextField completed, TextField efficiency, TextField left, TextField timeLeft, ImageView exampleImage, TextField exampleID ,TextField prediction){

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
                if(observed.getState() != null) {
                    completed.setText(observed.getState().getPercentageOfCompleted() + " %");
                    efficiency.setText(observed.getState().getEfficiencyInPercentage() + " %");
                    left.setText(observed.getState().getLeft());
                    timeLeft.setText(observed.getState().getTimeLeft());
                    prediction.setText(String.valueOf(observed.getState().getPrediction()));
                    exampleID.setText(String.valueOf(observed.getState().getCurrentInputData()));
                    int scale = (int) testsExample_img.getFitHeight()/imageWidth;
                    exampleImage.setImage(SwingFXUtils.toFXImage(new ImageCreator().displayData(dataset.getData(observed.getState().getCurrentInputData()).getSecond(),scale),null));
                }
            }
        }

        return new ObserverObject(logic);
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
