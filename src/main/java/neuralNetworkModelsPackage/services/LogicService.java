package neuralNetworkModelsPackage.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import neuralNetworkModelsPackage.interfaces.Logic;

import java.security.Provider;

public class LogicService extends Service<Void> {

    private Logic logic;

    public LogicService(Logic logic){
        this.logic = logic;
    }

    @Override
   protected Task<Void> createTask(){
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                logic.run();
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Success");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Fail");
            }
        };
    }
}
