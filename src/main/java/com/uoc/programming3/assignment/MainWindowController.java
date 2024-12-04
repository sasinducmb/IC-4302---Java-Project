package com.uoc.programming3.assignment;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MainWindowController {
    ToggleGroup toggleGroup;
    AlgorithmsController data = new AlgorithmsController();

    @FXML
    private ChoiceBox<Map.Entry<String, Integer>> colSelect;

    @FXML
    private TextField pathString;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextArea resultView;

    @FXML
    private TableView<ObservableList<String>> dataTable;



    @FXML
    public void initialize() {
        colSelect.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Map.Entry<String, Integer> object) {
                return object==null ? "" : object.getKey();
            }

            @Override
            public Map.Entry<String, Integer> fromString(String s) {
                return null;
            }
        });
        progressBar.setVisible(false);
    }
    @FXML
    protected void startSort() {
        int column=colSelect.getValue().getValue();
        progressBar.setVisible(true);
        System.out.println(colSelect.getValue().getValue());
//        dataTable.getColumns().clear();
        dataTable.getItems().clear();

        Thread thread = new Thread(() -> {
            String message = "";
            long start = System.nanoTime();
            ArrayList<AlgorithmsController.SingleDataFormat> resultShell = data.shellSort(column);
            double shellTime = (System.nanoTime() - start) / 1e6;
            message += "Shell sort: " + String.format("%.2f", shellTime) + " ms\n";
            progressBar.setProgress(0.25);


            start = System.nanoTime();
            ArrayList<AlgorithmsController.SingleDataFormat> resultMerge = data.mergeSort(column);
            double mergeTime = (System.nanoTime() - start) / 1e6;
            message += "Merge sort: " + String.format("%.2f", mergeTime) + " ms\n";
            progressBar.setProgress(0.5);

            start = System.nanoTime();
            ArrayList<AlgorithmsController.SingleDataFormat> resultHeap = data.heapSort(column);
            double heapTime = (System.nanoTime() - start) / 1e6;
            message += "Heap sort: " + String.format("%.2f", heapTime) + " ms\n";
            progressBar.setProgress(0.75);

            start = System.nanoTime();
            ArrayList<AlgorithmsController.SingleDataFormat> resultQuick = data.quickSort(column);
            double quickTime = (System.nanoTime() - start) / 1e6;
            message += "Quick sort: " + String.format("%.2f", quickTime) + " ms\n";
            progressBar.setProgress(0.90);

            start = System.nanoTime();
            ArrayList<AlgorithmsController.SingleDataFormat> resultInsertion = data.quickSort(column);
            double insertionTime = (System.nanoTime() - start) / 1e6;
            message += "Insertion sort: " + String.format("%.2f", insertionTime) + " ms";
            progressBar.setProgress(1.00);

            resultView.setText(message);

            for (int i = 0; i < resultShell.size(); i++) {
                ObservableList<String> row = FXCollections.observableArrayList(Arrays.asList(data.data.get(resultShell.get(i).index).value));
                dataTable.getItems().add(row);
            }

            progressBar.setVisible(false);
            progressBar.setProgress(0.0);
        });
        thread.start();
    }

    @FXML
    protected void handleFileSelection() {
        data=new AlgorithmsController();
        colSelect.getItems().clear();
        dataTable.getColumns().clear();
        dataTable.getItems().clear();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        File selectedFile = fileChooser.showOpenDialog(colSelect.getScene().getWindow());

        if (selectedFile != null) {
            pathString.setText(selectedFile.getAbsolutePath());
            pathString.setDisable(true);
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                String headersLine = br.readLine();
                String[] headers = headersLine.split(",");
                dataTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
                for (int i = 0; i < headers.length; i++) {

                    final int colIndex = i;
                    javafx.scene.control.TableColumn<ObservableList<String>, String> column = new javafx.scene.control.TableColumn<>(headers[i]);
                    column.setCellValueFactory(cellData ->
                            new SimpleStringProperty(cellData.getValue().get(colIndex))
                    );
                    column.setSortable(false);
                    dataTable.getColumns().add(column);
                }
                boolean firstLine = true;
                int x=0;
                while ((line = br.readLine()) != null) {
                    String[] rowValues = line.split(",");
                    data.data.add(new Data(x, rowValues));
                    x++;

                    if(firstLine){
                        for(int i = 0; i < rowValues.length; i++){
                            try{
                                Double.parseDouble(rowValues[i]);
                                int finalI = i;
                                colSelect.getItems().add(new AbstractMap.SimpleEntry<>(headers[finalI], finalI));
                                colSelect.setValue(new AbstractMap.SimpleEntry<>(headers[finalI], finalI));
                            }catch(NumberFormatException e){
                            }
                        }
                        firstLine = false;
                    }
                    ObservableList<String> row = FXCollections.observableArrayList(Arrays.asList(rowValues));
                    dataTable.getItems().add(row);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}