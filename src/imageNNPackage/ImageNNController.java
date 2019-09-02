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
    private TextField testFilePath_txt;

    @FXML
    private Button runTest_btn;

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
    private Button runTest_btn1;

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
    }


    @FXML
    void chooseSaveFilePath(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        System.out.println(file.getName());
    }

    @FXML
    void chooseTestFilePath(ActionEvent event) {

    }

    @FXML
    void chooseTrainFilePath(ActionEvent event) {

    }

    @FXML
    void createNewModelFromBar(ActionEvent event) {

    }

    @FXML
    void loadModelFromBar(ActionEvent event) {

    }

    @FXML
    void runTest(ActionEvent event) {
        Data data = new Data(testFilePath_txt.getText());
        if (layers[0] != data.getInputSize()) throw new IllegalArgumentException();
        int examplesNumber;
        if (data.getDataSize() < 10){
            examplesNumber = 1;
        } else if (data.getDataSize() < 100){
            examplesNumber = 10;
        } else if (data.getDataSize() < 1000){
            examplesNumber = 100;
        } else if (data.getDataSize() < 10000){
            examplesNumber = 1000;
        } else examplesNumber = 10000;
        DecimalFormat percentFormatter = new DecimalFormat("#0.00");
        DecimalFormat minutesFormatter = new DecimalFormat("###0");
        DecimalFormat secondsFormatter = new DecimalFormat("#0");


        long tik = 0;
        long tok = 0;
        List<Double> result;
        int correct = 0;
        for(int i = 0; i < data.getDataSize(); i++){
            tik = System.currentTimeMillis();
            result = Arrays.asList(network.predict(data.getData(i).getSecond()));
            if(result.indexOf(Collections.max(result)) == data.getData(i).getFirst().intValue())
                correct++;
            tok = System.currentTimeMillis();
            if(i % examplesNumber == 0){
                updateExample(i,data.getData(i),data.getInputSize());
            }
            int testsLeft = data.getDataSize()-i;
            testCompleted_txt.setText(percentFormatter.format(i/data.getDataSize()) + " %");
            testEfficiency_txt.setText(percentFormatter.format(correct/(i+1)) + " %");
            testTimeLeft_txt.setText(minutesFormatter.format(testsLeft * -(tik - tok) / 60000) + " m " + secondsFormatter.format((testsLeft * -(tik - tok) % 60000) / 1000) + " s");
            testsLeft_txt.setText(String.valueOf(testsLeft));
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
        Data data = new Data(trainDataPath_txt.getText());
        if (layers[0] != data.getInputSize()) throw new IllegalArgumentException();
        DecimalFormat percentFormatter = new DecimalFormat("#0.00");
        DecimalFormat minutesFormatter = new DecimalFormat("###0");
        DecimalFormat secondsFormatter = new DecimalFormat("#0");


        long tik = 0;
        long tok = 0;
        List<Double> result;
        int correct = 0;
        int packages = Integer.valueOf(packagesNumber_txt.getText());
        int packageSize =  Integer.valueOf(packageSize_txt.getText());
        int packageRepetitions =  Integer.valueOf(packageRepetitions_txt.getText());
        network.setLearningRate(Integer.valueOf(learningRate_txt.getText()));


        for(int l = 0; l < packages; l++){
            int[] dataPack = createDataPack(packageSize,data.getDataSize());
            tik = System.currentTimeMillis();
            for (int rep = 0; rep < packageRepetitions; rep++) {
                for (int element: dataPack) {
                    network.predict(data.getData(element).getSecond());
                    network.learnNetwork(data.getData(element).getFirst().intValue());
                }
            }
            correct = 0;
            for (int element: dataPack) {
                result = Arrays.asList(network.predict(data.getData(element).getSecond()));
                if(result.indexOf(Collections.max(result)) == data.getData(element).getFirst().intValue())
                    correct++;
            }
            tok = System.currentTimeMillis();
            int packagesLeft = packages-l;
            trainingCompleted_txt.setText(percentFormatter.format(l/packages) + " %");
            packagesLeft_txt.setText(String.valueOf(packagesLeft));
            lastPackageEfficiency_txt.setText(percentFormatter.format(correct/dataPack.length) + " %");
            trainingTimeLeft_txt.setText(minutesFormatter.format(packagesLeft * -(tik - tok) / 60000) + " m " + secondsFormatter.format((packagesLeft * -(tik - tok) % 60000) / 1000) + " s");

        }
    }

    private int[] createDataPack(int packagesSize,int dataSize) {
        int[] pack = new int[packagesSize];
        Random rand = new Random();
        for (int i = 0; i < pack.length; i++){
            pack[i] = rand.nextInt(dataSize);
        }
        return pack;
    }
}
