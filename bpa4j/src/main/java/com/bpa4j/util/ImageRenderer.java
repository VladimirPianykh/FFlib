package com.bpa4j.util;

import javax.imageio.ImageIO;

import com.bpa4j.ui.swing.util.PathIcon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Класс для генерации изображений с заданным текстом
 * <p>
 * Пример:
 * <pre>
 * {@code
 * File generatedFile = File generatedFile = ImageRenderer.generateAndSaveImage("Some text", pathIcon);
 *
 * PathIcon pathIcon = new PathIcon("ui/left.png");
 * File refactoredPathIcon = ImageRenderer.generateAndSaveImage("Test 2", pathIcon);
 * }
 * </pre>
 */
public final class ImageRenderer{
    public static final ClassLoader CL = ClassLoader.getSystemClassLoader();
    
    private ImageRenderer(){}

    /**
     * Генерирует картинку с нуля с заданными размерами и текстом
     * <p>
     * Сгенерированный файл сохраняется в Downloads
     * </p>
     * Пример:
     * <pre>
     * {@code
     * File generatedFile = ImageRenderer.generateAndSaveImage("Some text", pathIcon);
     * }
     * </pre>
     *
     * @param text - текст, который нужно отобразить на картинке
     * @param width - ширина
     * @param height - высота
     * @return созданный файл
     */
    public static File generateAndSaveImage(String text, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(16, 141, 63));
        graphics.fillRect(0, 0, width, height);

        addText(text, width, height, graphics);
        return savePng(text, image);
    }

    /**
     * Берёт {@link PathIcon}, добавляет на него заданный текст и сохраняет (не изменяя исходный файл)
     * <p>Сгенерированный файл сохраняется в Downloads</p>
     * <p>
     * Пример:
     * <pre>
     * {@code
     * PathIcon pathIcon = new PathIcon("ui/left.png");
     * File refactoredPathIcon = ImageRenderer.generateAndSaveImage("Test 2", pathIcon);
     * }
     * </pre>
     *
     * @param text - текст, который нужно отобразить на картинке
     * @param pathIcon - объект {@link PathIcon} на который будет накладываться текст
     * @return созданный файл
     */
    public static File generateAndSaveImage(String text, PathIcon pathIcon) {
        String path = pathIcon.path;

        File file = new File(CL.getResource("resources/" + path).getPath());
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException ex) {
            throw new UncheckedIOException("Unable to reach pathIcon " + pathIcon.path,ex);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        Graphics graphics = image.getGraphics();

        addText(text, width, height, graphics);
        return savePng(text, image);
    }

    /**
     * Утилитный метод для сохранения файла
     */
    private static File savePng(String text, Image image) {
        String home = System.getProperty("user.home");
        String outputPath = home + "\\Downloads\\" + text.substring(0, Math.min(text.length(), 8)) + ".png";

        File file = new File(outputPath);
        try {
            ImageIO.write((RenderedImage) image, "png", file);
            System.out.println("Saved generated image to: " + outputPath);
            return file;
        } catch (IOException ex) {
            throw new UncheckedIOException("Unable to save generated image",ex);
        }
    }

    /**
     * Утилитный метод для добавления текста на картинку
     */
    private static void addText(String text, int width, int height, Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.setFont(Font.getFont("Arial"));
        float fontSize = Math.min((float) 0.8 * width / text.length() * 2, height);

        graphics.setFont(graphics.getFont().deriveFont(Font.PLAIN, fontSize));
        int x = (int) (width - text.length() * fontSize / 2) / 2;
        int y = (height + (int) fontSize) / 2;
        graphics.drawString(text, x, y);
        graphics.dispose();
    }

}
