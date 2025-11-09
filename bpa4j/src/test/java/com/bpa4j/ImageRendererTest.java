package com.bpa4j;

import com.bpa4j.ui.swing.util.PathIcon;
import com.bpa4j.util.ImageRenderer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ImageRendererTest {
    @Disabled
    @DisplayName("generate and display a simple image with text on it")
    @Test
    void generateAndSaveImage() throws IOException, InterruptedException {
        String text = "Test 1";
        File generatedFile = ImageRenderer.generateAndSaveImage(text, 200, 200);

        assertEquals(text + ".png", generatedFile.getName());
        assert generatedFile.getPath().contains("Downloads");
        assert generatedFile.exists();
        generatedFile.delete(); // Удаляем
    }

    @Disabled
    @DisplayName("adding text on pathIcon and displaying it")
    @Test
    void addText() throws IOException, InterruptedException {
        PathIcon pathIcon = new PathIcon("ui/left.png");
        String text = "Test 2";
        File generatedFile = ImageRenderer.generateAndSaveImage(text, pathIcon);

        Desktop desktop = Desktop.getDesktop();
        desktop.open(generatedFile); // Открыть файл в ассоциированном приложении
        Thread.sleep(Duration.of(3, ChronoUnit.SECONDS)); // Наслаждаемся 3 секунды
        generatedFile.delete(); // Удаляем

        assertEquals(text + ".png", generatedFile.getName());
        assertTrue(generatedFile.getPath().contains("Downloads"));
    }
}