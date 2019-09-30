package modelsLogic.services;

import data.holder.NotDescribedData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class NotDescribedDataService extends Service<NotDescribedData> {

    private String path;

    public NotDescribedDataService(String path) {
        this.path = path;
    }

    @Override
    protected Task<NotDescribedData> createTask() {
        return new Task<>() {
            @Override
            protected NotDescribedData call() throws Exception {
                return new NotDescribedData(path);
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
