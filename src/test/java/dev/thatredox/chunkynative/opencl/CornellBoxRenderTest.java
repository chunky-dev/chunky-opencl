package dev.thatredox.chunkynative.opencl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.llbit.chunky.PersistentSettings;
import se.llbit.chunky.main.Chunky;
import se.llbit.chunky.main.ChunkyOptions;
import se.llbit.chunky.main.CommandLineOptions;
import se.llbit.chunky.renderer.DefaultRenderManager;
import se.llbit.chunky.renderer.SceneProvider;
import se.llbit.chunky.renderer.scene.Scene;
import se.llbit.util.TaskTracker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CornellBoxRenderTest {
    @BeforeAll
    static void download_mc() {
        new CommandLineOptions(new String[] { "-download-mc", "1.21.8" });
    }

    private static String getMcPath() {
        return new File(new File(PersistentSettings.settingsDirectory(), "resources"), "minecraft.jar").getPath();
    }

    @Test
    void render() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Initialize Chunky
        Chunky.loadDefaultTextures();
        ChunkyOptions options = ChunkyOptions.getDefaults();
        options.addResourcePacks(getMcPath());
        Chunky chunky = new Chunky(options);
        Field chunkyHeadless = chunky.getClass().getDeclaredField("headless");
        chunkyHeadless.setAccessible(true);
        chunkyHeadless.set(chunky, true);
        new ChunkyCl().attach(chunky);

        // Load the test scene
        chunky.getSceneManager().loadScene(new File("src/test/resources/Cornell Box/"), "Cornell Box");
        Scene scene = chunky.getSceneManager().getScene();

        // Do the rendering
        DefaultRenderManager renderer = new DefaultRenderManager(chunky.getRenderContext(), true);
        renderer.setSceneProvider((SceneProvider) chunky.getSceneManager());
        renderer.setRenderTask(TaskTracker.Task.NONE);

        scene.haltRender();
        scene.startHeadlessRender();

        renderer.start();
        renderer.join();
        renderer.shutdown();

        renderer.bufferedScene.saveFrame(new File("build/test-results/test_render.png"), TaskTracker.NONE);
    }

    @Test
    void preview_render() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Initialize Chunky
        Chunky.loadDefaultTextures();
        ChunkyOptions options = ChunkyOptions.getDefaults();
        options.addResourcePacks(getMcPath());
        Chunky chunky = new Chunky(options);
        Field chunkyHeadless = chunky.getClass().getDeclaredField("headless");
        chunkyHeadless.setAccessible(true);
        chunkyHeadless.set(chunky, true);
        new ChunkyCl().attach(chunky);

        // Load the test scene
        chunky.getSceneManager().loadScene(new File("src/test/resources/Cornell Box/"), "Cornell Box");
        Scene scene = chunky.getSceneManager().getScene();

        // Do the rendering
        DefaultRenderManager renderer = new DefaultRenderManager(chunky.getRenderContext(), true);
        renderer.setSceneProvider((SceneProvider) chunky.getSceneManager());
        renderer.setRenderTask(TaskTracker.Task.NONE);

        scene.setBufferFinalization(true);
        scene.haltRender();

        renderer.start();
        renderer.join();
        renderer.shutdown();

        renderer.withBufferedImage(bitmap -> {
            BufferedImage im = new BufferedImage(bitmap.width, bitmap.height, BufferedImage.TYPE_INT_ARGB);
            im.setRGB(0, 0, bitmap.width, bitmap.height, bitmap.data, 0, bitmap.width);
            try {
                ImageIO.write(im, "png", new File("build/test-results/test_preview.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
