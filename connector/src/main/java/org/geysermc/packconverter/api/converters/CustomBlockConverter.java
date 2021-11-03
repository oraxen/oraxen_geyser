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

package org.geysermc.packconverter.api.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.geysermc.connector.registry.populator.BlockRegistryPopulator;
import org.geysermc.packconverter.api.PackConverter;
import org.geysermc.packconverter.api.utils.CustomArmorHandler;
import org.geysermc.packconverter.api.utils.CustomBlockHandler;
import org.geysermc.packconverter.api.utils.CustomModelDataHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CustomBlockConverter extends AbstractConverter {

    @Getter
    public static final List<Object[]> defaultData = new ArrayList<>();

    static {
        defaultData.add(new String[]{"assets/minecraft/blockstates", "textures/terrain_texture.json", "blocks.json"});
    }

    public CustomBlockConverter(PackConverter packConverter, Path storage, Object[] data, Path storageBp) {
        super(packConverter, storage, data, storageBp);
    }

    JsonNode itemInformation;
    public static ObjectNode blockData;

    @Override
    public List<AbstractConverter> convert() {
        packConverter.log("Checking for custom blocks");
        try {

            String from = (String) this.data[0];
            String to = (String) this.data[1];
            String blocksJson = (String) this.data[2];

            ObjectMapper mapper = new ObjectMapper();
            packConverter.log(String.format("Converted custom blocks %s", from));

            // Create the texture_data file that will map all textures
            ObjectNode textureData = mapper.createObjectNode();
            textureData.put("resource_pack_name", "geysermc");
            textureData.put("texture_name", "atlas.terrain");
            textureData.put("padding", 8);
            textureData.put("num_mip_levels", 4);
            ObjectNode allTextures = mapper.createObjectNode();
            blockData = mapper.createObjectNode();
            blockData.set("format", mapper.createArrayNode().add(1).add(1).add(0));
            handleCustomBlockData(/*itemInformation,*/ allTextures, mapper, storage.resolve(from).toFile());

            textureData.set("texture_data", allTextures);
            //if (!packConverter.getCustomModelData().isEmpty()) {
            // We have custom model data, so let's write the textures
            OutputStream outputStream = Files.newOutputStream(storage.resolve(to), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, textureData);
            OutputStream outputStream1 = Files.newOutputStream(storage.resolve(blocksJson), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream1, blockData);
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public List<File> traverseDirectory(final File folder, List<File> fileNamesList) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory()) {
                traverseDirectory(f, fileNamesList);
            }
            if (f.isFile()) {
                fileNamesList.add(f);
            }

        }
        return fileNamesList;
    }

    private void handleCustomBlockData(/*JsonNode itemInformation,*/ ObjectNode allTextures, ObjectMapper mapper, File directory) {
        List<File> fileList = traverseDirectory(directory, new ArrayList<File>());
        for (File file : fileList) {
            try {
                if (!file.isFile()) {
                    handleCustomBlockData(/*itemInformation,*/ allTextures, mapper, file);
                }
                InputStream stream = new FileInputStream(file);

                JsonNode node = mapper.readTree(stream);
                if (node.has("multipart")) {
                    //String originalItemName = file.getName().replace(".json", "");
                    for (JsonNode blockstate : node.get("multipart")) {
                        // The "ID" of the CustomModelData. If the ID is 1, then to get the custom model data
                        // You need to run in Java `/give @s stick{CustomModelData:1}`
                        //int id = predicate.get("custom_model_data").asInt();
                        // Get the identifier that we'll register the item with on Bedrock, and create the JSON file
                        // List<String> strArr1 = file.getPath().contains("/") ? java.util.Arrays.asList(file.getPath().split("/")) : java.util.Arrays.asList(file.getPath().split("\\\\"));
                        // Create the texture information
                        if (blockstate.get("apply") == null) {
                            continue;
                        }
                        JsonNode apply = blockstate.get("apply");
                        String model = "";
                        //String out2 = join(strArr1, "/", strArr1.indexOf("models") + 1, strArr1.size()).replace(".json", "");
                        if (apply.get("model") != null) {
                            model = apply.get("model").asText();
                            if (model != null && !model.equals("required/note_block")) {
                                blockData.set("geysermc:" + model, CustomBlockHandler.handleBlockData(mapper, storage, model));
                                BlockRegistryPopulator.blockStates.add(node);
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }

                        // See if we have registered the vanilla item already
                        //Int2ObjectMap<CustomModelData> data = packConverter.getCustomModelData().getOrDefault(originalItemName, null);
                        //packConverter.getBehaviorPack().writeBehaviorPackItem(mapper, filePath, itemJsonInfo);
                        // if (data == null) {
                        // Create a fresh map of Java CustomModelData IDs to Bedrock string identifiers
                        //Int2ObjectMap<CustomModelData> map = new Int2ObjectOpenHashMap<>();
                        //map.put(id, customModelData);
                        // Put the vanilla item (stick) and the initialized map in the custom model data table
                        //packConverter.getCustomModelData().put(file.getName().replace(".json", ""), map);
                        // } else {
                        // Map exists, add the new CustomModelData ID and Bedrock string identifier
                        //data.put(id, customModelData);
                        //}
                        ObjectNode textureInfo = CustomModelDataHandler.handleItemTexture(mapper, storage, "default/" + model);
                        if (textureInfo != null) {
                            // If texture was created, add it to the file where Bedrock will read all textures
                            allTextures.setAll(textureInfo);
                        } else {
                            System.out.println("No texture for " + model);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String join(List<String> array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }

        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return "";
        }

        bufSize *= ((array.get(startIndex) == null ? 16 : array.get(startIndex).toString().length())
                + separator.length());

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array.get(i) != null) {
                buf.append(array.get(i));
            }
        }
        return buf.toString();
    }
}
