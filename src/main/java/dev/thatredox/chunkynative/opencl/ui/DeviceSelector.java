package dev.thatredox.chunkynative.opencl.ui;

import dev.thatredox.chunkynative.opencl.context.ContextManager;
import dev.thatredox.chunkynative.opencl.context.Device;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;

public class DeviceSelector extends Stage {

    public DeviceSelector() {
        // Build scene
        ClDevice[] devices = Arrays.stream(Device.getDevices()).map(ClDevice::new).toArray(ClDevice[]::new);

        TableView<ClDevice> table = new TableView<>();
        table.setPrefWidth(500);
        table.setPrefHeight(200);
        table.setItems(FXCollections.observableList(Arrays.asList(devices)));

        TableColumn<ClDevice, String> nameCol = new TableColumn<>("Device Name");
        nameCol.setCellValueFactory(dev -> new SimpleStringProperty(dev.getValue().name));

        TableColumn<ClDevice, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(dev -> new SimpleStringProperty(dev.getValue().type.toString()));

        TableColumn<ClDevice, Double> computeCol = new TableColumn<>("Compute Capacity");
        computeCol.setCellValueFactory(dev -> new SimpleDoubleProperty(dev.getValue().computeCapacity).asObject());

        table.getColumns().clear();
        table.getColumns().add(nameCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(computeCol);

        VBox box = new VBox();
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setSpacing(10);
        box.setPadding(new Insets(10));

        box.getChildren().add(new Label("Select OpenCL device to use:"));
        box.getChildren().add(table);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.TOP_RIGHT);
        buttons.setSpacing(10);

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnMouseClicked(event -> this.close());
        buttons.getChildren().add(cancelButton);

        Button selectButton = new Button("Select Device");
        selectButton.setDefaultButton(true);
        selectButton.setOnMouseClicked(event -> {
            if (!table.getSelectionModel().isEmpty()) {
                this.close();
                Device device = table.getSelectionModel().getSelectedItem().device;
                Device.setPreferredDevice(device);
                ContextManager.setDevice(device);
            }
        });
        buttons.getChildren().add(selectButton);

        box.getChildren().add(buttons);

        Scene scene = new Scene(box);

        // Apply scene
        this.setTitle("Select OpenCL Device");
        this.setScene(scene);
    }

    private static class ClDevice {
        public final String name;
        public final Device.DeviceType type;
        public final double computeCapacity;
        public final int id;
        public final Device device;

        public ClDevice(Device device) {
            this.name = device.name();
            this.type = device.type();
            this.computeCapacity = device.computeCapacity();
            this.id = device.id;
            this.device = device;
        }
    }
}
