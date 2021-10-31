/*
 * Copyright (c) 2019-2020 GeyserMC. http://geysermc.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 *  @author GeyserMC
 *  @link https://github.com/GeyserMC/PackConverter
 *
 */

package org.geysermc.packconverter.api;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lombok.Getter;
import lombok.Setter;
import org.geysermc.packconverter.api.utils.CustomModelData;
import org.geysermc.packconverter.api.utils.OnLogListener;
import org.geysermc.packconverter.api.utils.ZipUtils;
import org.geysermc.packconverter.api.converters.AbstractConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PackConverter {

    @Getter
    private final Path output;

    @Getter
    private final Path tmpDir;
    @Getter
    private final Path input1;

    @Getter
    private final Map<String, Int2ObjectMap<CustomModelData>> customModelData = new HashMap<>();
    @Getter
    private final Map<String, Int2ObjectMap<CustomModelData>> customModelData1 = new HashMap<>();
    @Setter
    private OnLogListener onLogListener;

    public PackConverter(Path input, Path output) throws IOException {
        this.output = output;
        this.input1 = input;
        // Load any image plugins
        ImageIO.scanForPlugins();

        // Extract the zip to a temp location
        // This is quite slow, maybe try and find a faster method?
        tmpDir = input.toAbsolutePath().getParent().resolve(input.getFileName() + "_mcpack/");
        tmpDir.toFile().mkdir();
        ZipFile zipFile = new ZipFile(input.toFile());
        Path resourcesDir = tmpDir.resolve("resources");
        //Path behaviourDir = tmpDir.resolve("behaviour");
        //Path behaviourDir = tmpDir.resolve("resources").getParent();
        ZipEntry entry;
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            if (!entry.isDirectory()) {
                File newFile = resourcesDir.resolve(entry.getName()).toFile();
                newFile.getParentFile().mkdirs();

                InputStream fileStream = zipFile.getInputStream(entry);
                FileOutputStream outStream = new FileOutputStream(newFile);

                byte[] buf = new byte[fileStream.available()];
                int length;
                while ((length = fileStream.read(buf)) != -1) {
                    outStream.write(buf, 0, length);
                }

                outStream.flush();
                outStream.close();
            }
        }
       /* if (Files.notExists(behaviourDir)) {
            behaviourDir.toFile().mkdir();
        }*/
    }
    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
    /**
     * Convert all resources in the pack using the converters
     */
    public void convert() {
        List<AbstractConverter> additionalConverters = new ArrayList<>();
        Path resources = tmpDir.resolve("resources");
        //Path behaviour = tmpDir.resolve("behaviour");
        //Path behaviour = tmpDir.resolve("resources").getParent();
        for (Class<? extends AbstractConverter> converterClass : ConverterHandler.converterList) {
            try {
                List<Object[]> defaultData = (List<Object[]>) converterClass.getMethod("getDefaultData").invoke(null);
                AbstractConverter converter;
                for (Object[] data : defaultData) {

                    converter = converterClass.getDeclaredConstructor(PackConverter.class, Path.class, Object[].class, Path.class).newInstance(this, resources, data,resources);
                    additionalConverters.addAll(converter.convert());
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log(e.getMessage() + "\n" + e.getStackTrace());
            }
        }
        for (AbstractConverter converter : additionalConverters) {
            converter.convert();
        }
    }

    /**
     * Convert the temporary folder into the output zip
     */
    public void pack() {
        ZipUtils zipUtils = new ZipUtils(this, tmpDir.resolve("resources").toFile());
        zipUtils.generateFileList();
        zipUtils.zipIt(output.toString());
        //ZipUtils zipUtils1 = new ZipUtils(this, tmpDir.resolve("behaviour").toFile());
        //ZipUtils zipUtils1 = new ZipUtils(this, tmpDir.resolve("resources").getParent().toFile());
        //zipUtils1.generateFileList();
        //zipUtils1.zipIt(output.toString().replace(".mcpack","-behaviour.zip"));
    }

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * Remove the temporary folder generated by the converter.
     * Silently fails.
     */
    public void cleanup() {
        deleteDirectory(tmpDir.toFile());
        input1.toFile().delete();
    }

    public void log(String message) {
        if (onLogListener != null) {
            onLogListener.onLog();
        } else {
            System.out.println(message);
        }
    }
}
