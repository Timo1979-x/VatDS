package by.gto.controllers;

import javafx.stage.Stage;

public class BaseController<M, R> {
    protected M main;
    protected Stage stage;
    protected R result;

    public void setInitialData(M main, Stage stage) {
        this.main = main;
        this.stage = stage;
    }

    public R getResult() {
        return result;
    }
}