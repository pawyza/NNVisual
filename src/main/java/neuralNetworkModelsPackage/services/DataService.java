package neuralNetworkModelsPackage.services;

import dataLoadersPackage.csvLoader.Data;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DataService extends Service<Data> {

    private String path;

    public DataService(String path) {
        this.path = path;
    }

    @Override
    protected Task<Data> createTask() {
        return new Task<>() {
            @Override
            protected Data call() throws Exception {
                return new Data(path);
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
