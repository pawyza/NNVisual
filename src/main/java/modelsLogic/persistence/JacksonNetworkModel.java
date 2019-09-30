package modelsLogic.persistence;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import modelsLogic.neuralNetworkLogic.NeuralNetwork;

public class JacksonNetworkModel {
    private Integer imageWidth;
    private NeuralNetwork network;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public JacksonNetworkModel(@JsonProperty("imageWidth") Integer imageWidth,@JsonProperty("network") NeuralNetwork network) {
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
