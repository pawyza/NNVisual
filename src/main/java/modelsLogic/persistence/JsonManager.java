package modelsLogic.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import modelsLogic.neuralNetworkLogic.NeuralNetwork;

import java.io.IOException;

public class JsonManager {

    public String modelToJson(Integer imageWidth, NeuralNetwork network){
        JacksonNetworkModel model = new JacksonNetworkModel(imageWidth,network);
        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new KotlinModule());
        try {
            return jacksonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e){

        }
        return null;
    }

    public JacksonNetworkModel modelFromJson(String modelJson){
        ObjectMapper jacksonMapper = new ObjectMapper().registerModule(new KotlinModule());
        try {
            return jacksonMapper.readValue(modelJson, JacksonNetworkModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
