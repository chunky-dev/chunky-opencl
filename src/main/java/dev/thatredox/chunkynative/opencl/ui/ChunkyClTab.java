package dev.thatredox.chunkynative.opencl.ui;

import dev.thatredox.chunkynative.opencl.context.ContextManager;
import dev.thatredox.chunkynative.opencl.context.KernelLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.chunky.ui.render.RenderControlsTab;

public class ChunkyClTab implements RenderControlsTab {
    protected final VBox box;
    private Scene scene;

    public ChunkyClTab(Scene scene) {
        this.scene = scene;

        box = new VBox(10.0);
        box.setPadding(new Insets(10.0));

        Button deviceSelectorButton = new Button("Select OpenCL Device");
        deviceSelectorButton.setOnMouseClicked(event -> {
            DeviceSelector selector = new DeviceSelector();
            selector.show();
        });
        box.getChildren().add(deviceSelectorButton);

        if (KernelLoader.canHotReload()) {
            Button reloadButton = new Button("Reload!");
            reloadButton.setOnMouseClicked(event -> {
                ContextManager.reload();
                scene.refresh();
            });
            box.getChildren().add(reloadButton);
        }
    }

    @Override
    public void update(Scene scene) {
        this.scene = scene;
    }

    @Override
    public String getTabTitle() {
        return "OpenCL";
    }

    @Override
    public Node getTabContent() {
        return box;
    }
}
