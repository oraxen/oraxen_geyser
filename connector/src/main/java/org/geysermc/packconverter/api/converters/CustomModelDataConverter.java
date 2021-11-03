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
import org.geysermc.packconverter.api.PackConverter;
import org.geysermc.packconverter.api.utils.CustomArmorHandler;
import org.geysermc.packconverter.api.utils.CustomModelDataHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CustomModelDataConverter extends AbstractConverter {

    @Getter
    public static final List<Object[]> defaultData = new ArrayList<>();

    static {
        defaultData.add(new String[] {"assets/minecraft/models", "textures/item_texture.json"});
    }

    public CustomModelDataConverter(PackConverter packConverter, Path storage, Object[] data, Path storageBp) {
        super(packConverter, storage, data, storageBp);
    }
    JsonNode itemInformation;
    @Override
    public List<AbstractConverter> convert() {
        packConverter.log("Checking for custom model data");
        try {

            String from = (String) this.data[0];
            String to = (String) this.data[1];

            ObjectMapper mapper = new ObjectMapper();
            packConverter.log(String.format("Converted models %s", from));
            String lazyJsonString = "{\n" +
                    "  \"format_version\": \"1.8.0\",\n" +
                    "  \"animations\": {\n" +
                    "    \"animation.geysermc.disable\": {\n" +
                    "      \"loop\": true,\n" +
                    "      \"override_previous_animation\": true,\n" +
                    "      \"bones\": {\n" +
                    "        \"geysermc\": {\n" +
                    "          \"scale\": 0\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            try {
                InputStream stream = PackConverter.class.getResourceAsStream("/item_information.json");
                itemInformation = mapper.readTree(stream);
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }

            // Create the texture_data file that will map all textures
            ObjectNode textureData = mapper.createObjectNode();
            textureData.put("resource_pack_name", "geysermc");
            textureData.put("texture_name", "atlas.items");
            ObjectNode allTextures = mapper.createObjectNode();
            handleCustomModelData(/*itemInformation,*/ allTextures, mapper, storage.resolve(from).toFile());

            textureData.set("texture_data", allTextures);
            //if (!packConverter.getCustomModelData().isEmpty()) {
            // We have custom model data, so let's write the textures
            OutputStream outputStream = Files.newOutputStream(storage.resolve(to), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, textureData);
            // }
            File itemJsonPath = storage.resolve("animations/geysermc").toFile();
            if (!itemJsonPath.exists()) {
                itemJsonPath.mkdirs();
            }
            OutputStream outputStream1 = Files.newOutputStream(storage.resolve("animations/geysermc/animation.geysermc.disable.json"), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            String final1 = new JsonParser().parse(lazyJsonString).toString();
            byte[] bytes = final1.getBytes();
            outputStream1.write(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
    public List<File> traverseDirectory(final File folder, List<File> fileNamesList) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory()) {
                if(!f.getName().contains("item")) {
                    traverseDirectory(f, fileNamesList);
                }
            }
            if (f.isFile()) {
                fileNamesList.add(f);
            }

        }
        return fileNamesList;
    }
    private void handleCustomModelData(/*JsonNode itemInformation,*/ ObjectNode allTextures, ObjectMapper mapper, File directory) {
        List<File> fileList = traverseDirectory(directory,new ArrayList<File>());
        for (File file : fileList) {
            try {
                if (!file.isFile()) {
                    handleCustomModelData(/*itemInformation,*/ allTextures, mapper, file);
                }
                InputStream stream = new FileInputStream(file);

                JsonNode node = mapper.readTree(stream);
                //if (node.has("overrides")) {
                String originalItemName = file.getName().replace(".json", "");
                //for (JsonNode override : node.get("overrides")) {
                //JsonNode predicate = override.get("predicate");
                //JsonNode pulling = predicate.get("pulling");
                //if (pulling != null && pulling.asInt() != 0) {
                //if (pulling != null) { //FIXME: Don't translate bows or they are otherwise non-functional for Bedrock
                // Animation for bow handled for the core bow item
                //   continue;
                //}
                // This is where the custom model data happens - each one is registered here under "predicate"
                //if (predicate.has("custom_model_data")) {
                //String filePath = override.get("model").asText();
                            /*if (filePath.startsWith("minecraft:")) {
                                // We don't need this lol
                                continue;
                            }*/
                //JsonNode itemJsonInfo = itemInformation.get(filePath.contains(":") ? filePath.split(":")[1]:filePath);
                //if (itemJsonInfo == null) {
                //    System.out.println("No item information for " + file.getName().replace(".json", "")+ ", "+(filePath.contains(":") ? filePath.split(":")[1]:filePath));
                //    continue;
                //}
                String type = "chest";
                if(originalItemName.contains("_boots")) type = "feet";
                if(originalItemName.contains("_chestplate")) type = "chest";
                if(originalItemName.contains("_leggings")) type = "legs";
                if(originalItemName.contains("_helmet")) type = "head";
                // The "ID" of the CustomModelData. If the ID is 1, then to get the custom model data
                // You need to run in Java `/give @s stick{CustomModelData:1}`
                //int id = predicate.get("custom_model_data").asInt();
                // Get the identifier that we'll register the item with on Bedrock, and create the JSON file
                List<String> strArr1 = file.getPath().contains("/") ? java.util.Arrays.asList(file.getPath().split("/")) : java.util.Arrays.asList(file.getPath().split("\\\\"));
                // Create the texture information

                String out2 = join(strArr1,"/",strArr1.indexOf("models")+1,strArr1.size()).replace(".json","");
                if(node.get("elements") != null){
                    CustomModelDataHandler.threeDeeModelzBaby(mapper, storage, originalItemName, /*filePath,*/ out2, node/*, itemJsonInfo, predicate,id*/);
                }
                else if (originalItemName.contains("_boots") || originalItemName.contains("_leggings") || originalItemName.contains("_chestplate") || originalItemName.contains("_helmet")) {
                    CustomArmorHandler.handleCustomArmor_normal(mapper, storage, originalItemName, /*filePath,*/ out2,/* itemJsonInfo, predicate,*/ type,storageBp/*,id*/);
                }
                else{
                    CustomModelDataHandler.handleItemData(mapper, storage, originalItemName, /*filePath,*/ out2/*, itemJsonInfo, predicate,id*/);
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

                List<String> strArr = file.getPath().contains("/") ? java.util.Arrays.asList(file.getPath().split("/")) : java.util.Arrays.asList(file.getPath().split("\\\\"));
                // Create the texture information
                String out1 = join(strArr,"/",strArr.indexOf("models")+1,strArr.size()).replace(".json","");
                ObjectNode textureInfo = CustomModelDataHandler.handleItemTexture(mapper, storage, /*filePath,*/out1);
                if (textureInfo != null) {
                    // If texture was created, add it to the file where Bedrock will read all textures
                    allTextures.setAll(textureInfo);
                } else {
                    System.out.println("No texture for " + out1);
                    //  }
                    // }

                    //}
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
