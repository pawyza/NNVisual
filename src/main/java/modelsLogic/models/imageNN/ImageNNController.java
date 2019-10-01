package modelsLogic.models.imageNN;

import data.holder.DescribedData;
import data.holder.NotDescribedData;
import data.representation.ImageCreator;
import data.loaders.SupportedFiles;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import kotlin.Pair;
import modelsLogic.persistence.JacksonNetworkModel;
import modelsLogic.persistence.JsonManager;
import modelsLogic.neuralNetworkLogic.NeuralNetwork;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import modelsLogic.interfaces.Observable;
import modelsLogic.interfaces.Observer;
import modelsLogic.services.DescribedDataService;
import modelsLogic.services.LogicService;
import modelsLogic.services.NotDescribedDataService;

import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.time.Instant;
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
    private Text messagePredict_txt;

    @FXML
    private ImageView trainingExample_img;

    @FXML
    private ImageView trainingExampleNNState_img;

    @FXML
    private TextField trainingExamplePrediction_txt;

    @FXML
    private TextField trainingExampleID_txt;

    @FXML
    private TextField trainingPackagesLeft_txt;

    @FXML
    private TextField trainingTimeLeft_txt;

    @FXML
    private TextField trainingCompleted_txt;

    @FXML
    private TextField trainingLastPackageEfficiency_txt;

    @FXML
    private Button predict_btn;

    @FXML
    private ImageView predict_img;

    @FXML
    private ImageView predictState_img;

    @FXML
    private TextField predictPath_txt;

    @FXML
    private TableView<Prediction> prediction_tab;

    @FXML
    private TableColumn<Prediction, Integer> predictionTabID_col;

    @FXML
    private TableColumn<Prediction, Integer> predictionTabPred_col;

    @FXML
    private MenuItem saveModel_barBtn;
    //</editor-fold>

    private FileChooser.ExtensionFilter supportedExtension;

    private Integer imageWidth;
    private NeuralNetwork network;

    public ImageNNController(Integer imageWidth,Integer[] layers, Double learningRate){
        this.imageWidth = imageWidth;
        supportedExtension = new FileChooser.ExtensionFilter("DescribedData" , SupportedFiles.INSTANCE.getExtensionsList());
        network = new NeuralNetwork(layers, learningRate);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        learningRate_txt.setText(String.valueOf(network.getLearningRate()));

        prediction_tab.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSlection) -> {
            if(newSlection != null){
                int scale = (int) testsExample_img.getFitHeight() / imageWidth;
                predict_img.setImage(SwingFXUtils.toFXImage(new ImageCreator().displayData(newSlection.getData(), scale), null));

            }
        });

        //TODO do usuniecia
        testSetup();
        //TODO sprawdzanie poprawnosci wpisywanych ustawien i wybieranych sciezek, poprawianie/wyswietlanie komuniktow analogiczne do tworzenia modelu
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

    @FXML
    void chooseDataToPredictFilePath(ActionEvent event) {
        File file = getFile();
        if(file != null)
            predictPath_txt.setText(file.getPath());
    }

    @FXML
    void loadModel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files","*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            load(file);
        }
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
    void saveModel(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)","*.txt"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null){
            JsonManager jsonManager = new JsonManager();
            String jsonModel = jsonManager.modelToJson(imageWidth,network);
            if(jsonModel != null) {
                save(jsonModel, file);
            } else {
                showAlert("Error","Cannot save model.");
            }
        }
    }

    private void save(String jsonModel, File file){
        try {
            PrintWriter modelWriter = new PrintWriter(new FileWriter(file, false));
            modelWriter.print(jsonModel);
            modelWriter.close();
            addSaveLocation(file);
            showAlert("Success","Model saved successfully.");
        } catch (IOException e) {
            showAlert("Error","Cannot save model.");
        }
    }

    private void addSaveLocation(File file){
        try {
            File saveLocation = new File("src/main/resources/settings/savedModelLocation.txt").getAbsoluteFile();
            saveLocation.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(saveLocation));
            String line;
            boolean exists = false;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.split("\\|")[0].trim().equals(file.getAbsolutePath())){
                    exists = true;
                }
            }
            if(!exists) {
                PrintWriter settingsWriter = new PrintWriter(new FileWriter(saveLocation, true));
                settingsWriter.println(file.getAbsolutePath() + " | " + getLastPackageName(getClass().getPackage().getName()));
                settingsWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLastPackageName(String fullPackage){
        String[] spliced = fullPackage.split("\\.");
        return spliced[spliced.length-1];
    }

    private void load(File file){
        JsonManager jsonManager = new JsonManager();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JacksonNetworkModel jsonModel = jsonManager.modelFromJson(stringBuilder.toString());
            network = jsonModel.getNetwork();
            imageWidth = jsonModel.getImageWidth();
            showAlert("Success","Model loaded");
        } catch (IOException e) {
            showAlert("Error","Cannot load model");
        }
    }

    private void showAlert(String title, String header){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    @FXML
    void showInfo(ActionEvent event) {

    }

    @FXML
    void testNN(ActionEvent event) {
        try {
            messageTest_txt.setText("Loading data");
            testNN_btn.setDisable(true);
            Service dataService = new DescribedDataService(testDataPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                messageTest_txt.setText("Testing network");
                DescribedData describedData = ((DescribedDataService) dataService).getValue();
                setViewsIndicators(false, testCompleted_txt,testEfficiency_txt,testsLeft_txt,testTimeLeft_txt,testExampleID_txt,testExamplePrediction_txt);

                ImageNNTestingLogic testingLogic = new ImageNNTestingLogic(network, describedData);

                Observer testingObserver = prepareObserver(testingLogic, describedData,testCompleted_txt,testEfficiency_txt,testsLeft_txt,testTimeLeft_txt,testsExample_img,testExampleID_txt,testExamplePrediction_txt);
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
    void trainNN(ActionEvent event) {
        try {
            int packageNumber = Integer.valueOf(packagesNumber_txt.getText());
            int packageSize = Integer.valueOf(packageSize_txt.getText());
            int packageRepetitions = Integer.valueOf(packageRepetitions_txt.getText());
            double learningRate = Double.valueOf(learningRate_txt.getText());
            messageTrain_txt.setText("Loading data");
            trainNN_btn.setDisable(true);

            Service dataService = new DescribedDataService(trainDataPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                messageTrain_txt.setText("Training network");
                DescribedData describedData = ((DescribedDataService) dataService).getValue();
                setViewsIndicators(false, trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExampleID_txt, trainingExamplePrediction_txt);

                ImageNNTrainingLogic trainingLogic = new ImageNNTrainingLogic(network, describedData, packageNumber, packageSize, packageRepetitions, learningRate);
                Observer trainingObserver = prepareObserver(trainingLogic, describedData,trainingCompleted_txt,trainingLastPackageEfficiency_txt, trainingPackagesLeft_txt, trainingTimeLeft_txt, trainingExample_img, trainingExampleID_txt, trainingExamplePrediction_txt);
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

    @FXML
    void predict(ActionEvent event) {
        try {
            predict_btn.setDisable(true);

            Service dataService = new NotDescribedDataService(predictPath_txt.getText());
            dataService.start();
            dataService.setOnSucceeded(dataEvent -> {
                NotDescribedData notDescribedData = ((NotDescribedDataService) dataService).getValue();

                ImageNNPredictLogic predictLogic = new ImageNNPredictLogic(network, notDescribedData);
                Observer trainingObserver = prepareObserver(predictLogic, notDescribedData, predict_img);
                predictLogic.addObserver(trainingObserver);

                Service logicService = new LogicService(predictLogic);
                logicService.start();

                logicService.setOnSucceeded(logicEvent ->{
                    ObservableList<Prediction> predictions = FXCollections.observableArrayList(createList(predictLogic.getPredictions()));
                    predictionTabID_col.setCellValueFactory(new PropertyValueFactory<>("id"));
                    predictionTabPred_col.setCellValueFactory(new PropertyValueFactory<>("prediction"));
                    prediction_tab.setItems(predictions);
                    messagePredict_txt.setDisable(true);
                    messagePredict_txt.setText("");
                    predict_btn.setDisable(false);
                });
                logicService.setOnFailed(logicEvent -> {
                    messagePredict_txt.setDisable(false);
                    messagePredict_txt.setText("Predicting failed");
                    predict_btn.setDisable(false);
                });
            });
            dataService.setOnFailed(dataEvent -> {
                messagePredict_txt.setDisable(true);
                messagePredict_txt.setText("Error while loading data");
                predict_btn.setDisable(false);
            });
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            predict_btn.setDisable(false);
            messagePredict_txt.setText("Error");
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

    private Observer prepareObserver(Observable logic, NotDescribedData dataset, ImageView exampleImage) {

        class ObserverObject implements Observer {

            private final Observable observed;
            private ObservableList<Prediction> predictions = FXCollections.observableArrayList();

            private ObserverObject(Observable observed) {
                this.observed = observed;
            }

            @Override
            public Observable getObserved() {
                return observed;
            }

            @Override
            public void update() {
                if (observed.getState() != null) {
                    Platform.runLater(() -> {
                        int scale = (int) testsExample_img.getFitHeight() / imageWidth;
                        exampleImage.setImage(SwingFXUtils.toFXImage(new ImageCreator().displayData(dataset.getData(observed.getState().getCurrentInputData()), scale), null));
                    });
                }
            }
        }

        return new ObserverObject(logic);
    }

    private Observer prepareObserver(Observable logic, DescribedData dataset, TextField completed, TextField efficiency, TextField left, TextField timeLeft, ImageView exampleImage, TextField exampleID , TextField prediction){

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
                Platform.runLater(() -> {
                    if (observed.getState() != null) {
                        completed.setText(observed.getState().getPercentageOfCompleted() + " %");
                        efficiency.setText(observed.getState().getEfficiencyInPercentage() + " %");
                        left.setText(observed.getState().getLeft());
                        timeLeft.setText(observed.getState().getTimeLeft());
                        prediction.setText(String.valueOf(observed.getState().getPrediction()));
                        exampleID.setText(String.valueOf(observed.getState().getCurrentInputData()));
                        int scale = (int) testsExample_img.getFitHeight() / imageWidth;
                        exampleImage.setImage(SwingFXUtils.toFXImage(new ImageCreator().displayData(dataset.getData(observed.getState().getCurrentInputData()).getSecond(), scale), null));
                    }
                });
            }
        }

        return new ObserverObject(logic);
    }

    //TODO do usuniecia
    private void testSetup(){
        testDataPath_txt.setText("D:\\nnLearning\\mnist_test.csv");
        trainDataPath_txt.setText("D:\\nnLearning\\mnist_train.csv");
        predictPath_txt.setText("D:\\nnLearning\\mnist_predict_ten.csv");
        packagesNumber_txt.setText("100");
        packageSize_txt.setText("10");
        packageRepetitions_txt.setText("10");
    }

    private List<Prediction> createList(List<Pair<Integer,Double[]>> predictions) {
        List<Prediction> predictionsObjList = new ArrayList<>();
        for(int i = 0; i < predictions.size(); i++){
            predictionsObjList.add(new Prediction(i+1,predictions.get(i).getFirst(),predictions.get(i).getSecond()));
        }
        return predictionsObjList;
    }

    public class Prediction{
        private Integer id;
        private Integer prediction;
        private Double[] data;

        Prediction(Integer id, Integer prediction, Double[] data) {
            this.id = id;
            this.prediction = prediction;
            this.data = data;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getPrediction() {
            return prediction;
        }

        public void setPrediction(Integer prediction) {
            this.prediction = prediction;
        }

        public Double[] getData() {
            return data;
        }

        public void setData(Double[] data) {
            this.data = data;
        }

    }
}
