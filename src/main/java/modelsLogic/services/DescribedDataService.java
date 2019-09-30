package modelsLogic.services;

import data.holder.DescribedData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DescribedDataService extends Service<DescribedData> {

    private String path;

    public DescribedDataService(String path) {
        this.path = path;
    }

    @Override
    protected Task<DescribedData> createTask() {
        return new Task<>() {
            @Override
            protected DescribedData call() throws Exception {
                return new DescribedData(path);
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
