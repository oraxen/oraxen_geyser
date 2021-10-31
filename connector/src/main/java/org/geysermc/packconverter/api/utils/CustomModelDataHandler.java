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

package org.geysermc.packconverter.api.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CustomModelDataHandler {

    public static void handleItemData(ObjectMapper mapper, Path storage, String originalItemName, /*String filePath,*/String fileName/*, JsonNode itemJsonInfo, JsonNode predicate, int id*/) {
        // Start the creation of the JSON that registers the object
        //filePath = filePath.contains(":") ? filePath.split(":")[1] : filePath;
        ObjectNode item = mapper.createObjectNode();
        // Standard JSON
        item.put("format_version", "1.10");
        ObjectNode itemData = mapper.createObjectNode();
        ObjectNode itemDescription = mapper.createObjectNode();

        // Full identifier with geysermc: prefix (cmd for CustomModelData - just in case it clashes with something we do in the future)
        String identifier = "geysermc:" + originalItemName;
        // Add the original item name as well to prevent conflicts if multiple items share the same model
        //String identifier = "geysermc:" + fileName;
        // Register the full identifier
        itemDescription.put("identifier", identifier);
        itemData.set("description", itemDescription);
        //NbtMapBuilder componentBuilder = NbtMap.builder();
        //NbtMapBuilder itemPropertiesBuilder = NbtMap.builder();
        /*JsonNode pulling = predicate.get("pulling");
        if (pulling != null) {
            //itemPropertiesBuilder.putInt("use_animation", 1);
            componentBuilder.putString("minecraft:render_offsets", "miscellaneous");
            componentBuilder.putString("minecraft:use_animation", "bow");
        }*/

        /*int maxDamage = itemJsonInfo.get("max_damage").asInt();
        if (maxDamage != 0) {
            componentBuilder.putCompound("minecraft:durability", NbtMap.builder().putInt("max_durability", maxDamage).build());
        }
        itemPropertiesBuilder.putBoolean("allow_off_hand", true); // We always want offhand to be accessible
        itemPropertiesBuilder.putBoolean("hand_equipped", itemJsonInfo.get("hand_equipped").booleanValue());
        itemPropertiesBuilder.putInt("max_stack_size", itemJsonInfo.get("max_stack_size").intValue());
        componentBuilder.putCompound("item_properties", itemPropertiesBuilder.build());*/
        item.set("minecraft:item", itemData);

        //TODO make sure there can't be duplicates here
       // componentBuilder.putCompound("minecraft:icon", NbtMap.builder().putString("texture", fileName.substring(filePath.lastIndexOf("/") + 1)).build());
        float x = 0.05f;
        float y = 0.05f;
        float z = 0.05f;
        File file1 = storage.resolve("assets/minecraft/" + fileName + ".png").toFile();
        if(file1.exists()){
            try {
                BufferedImage bi = ImageIO.read(file1);
                if (bi.getWidth() > 16 || bi.getHeight() > 16) {
                    x = (0.075f/(bi.getWidth()/16));
                    y = (0.125f/(bi.getHeight()/16));
                    z = (0.075f/(bi.getWidth()/16));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ObjectNode renderOffsets = mapper.createObjectNode();
        ObjectNode itemComponent = mapper.createObjectNode();
        itemComponent.put("minecraft:icon", identifier.replace("geysermc:", ""));
        itemComponent.put("minecraft:allow_off_hand", true);
        /*ObjectNode final1 = mapper.createObjectNode();
        ObjectNode first_person = mapper.createObjectNode();
        ObjectNode third_person = mapper.createObjectNode();
        ObjectNode fp_rotation = mapper.createObjectNode();
        fp_rotation.put("x",0.0f);
        fp_rotation.put("y",0.0f);
        fp_rotation.put("z",0.0f);
        ObjectNode fp_position = mapper.createObjectNode();
        fp_position.put("x",0.0f);
        fp_position.put("y",0.0f);
        fp_position.put("z",0.0f);
        ObjectNode fp_scale = mapper.createObjectNode();
        fp_scale.put("x",x);
        fp_scale.put("y",y);
        fp_scale.put("z",z);
        first_person.set("position",fp_position);
        first_person.set("rotation",fp_rotation);
        first_person.set("scale",fp_scale);
        ObjectNode tp_rotation = mapper.createObjectNode();
        tp_rotation.put("x",x);
        tp_rotation.put("y",y);
        tp_rotation.put("z",z);
        ObjectNode tp_position = mapper.createObjectNode();
        tp_position.put("x","0.0");
        tp_position.put("y","0.0");
        tp_position.put("z","0.0");
        ObjectNode tp_scale = mapper.createObjectNode();
        tp_scale.put("x",x);
        tp_scale.put("y",y);
        tp_scale.put("z",z);
        third_person.set("position",tp_position);
        third_person.set("rotation",tp_rotation);
        third_person.set("scale",tp_scale);
        final1.set("first_person",first_person);
        final1.set("third_person",third_person);
        renderOffsets.set("main_hand",final1);
        renderOffsets.set("off_hand",final1);
        //itemComponent.put("minecraft:hand_equipped", itemJsonInfo.get("hand_equipped").booleanValue());
        //itemComponent.put("minecraft:max_stack_size", itemJsonInfo.get("max_stack_size").intValue());
        if (pulling != null) {
            //itemPropertiesBuilder.putInt("use_animation", 1);
            itemComponent.put("minecraft:render_offsets", "miscellaneous");
            itemComponent.put("minecraft:use_animation", "bow");
        }
        // Define which texture in item_texture.json this should use. We just set it to the "clean identifier"
        //itemComponent.put("minecraft:icon", identifier.replace("geysermc:", ""));
        itemComponent.set("minecraft:render_offsets",renderOffsets);*/
        itemData.set("components", itemComponent);

        // Create, if necessary, the folder that stores all item information
        File itemJsonPath = storage.resolve("items").toFile();
        if (!itemJsonPath.exists()) {
            itemJsonPath.mkdir();
        }
        String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName+".json");
       String path001 = path002.contains("/")?path002.substring(0,path002.lastIndexOf("/")):path002;
        Path path2 = itemJsonPath.toPath().resolve(path001);
       /* if(Files.notExists(path2.getParent())){
            path2.getParent().toFile().mkdirs();
        }*/
        // Write our item information
        //Path path = itemJsonPath.toPath().resolve(filePath.substring(filePath.lastIndexOf("/") + 1) + ".json");
        try (OutputStream outputStream = Files.newOutputStream(path2,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, item);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //CustomModelData customModelData = new CustomModelData();
       //customModelData.setIdentifier(identifier);
        //customModelData.setNbt(componentBuilder.build());

        //return customModelData;
    }

    private static NbtMap buildCompoundValue(Object value) {
        NbtMapBuilder builder = NbtMap.builder();
        builder.put("value", value);
        return builder.build();
    }
    public static BufferedImage scaleWithPadding(BufferedImage img, int newWidth, int newHeight) {
        int currentWidth = img.getWidth();
        int currentHeight = img.getHeight();

        int scaledWidth;
        int scaledHeight;
        if (currentWidth == 0 || currentHeight == 0
                || (currentWidth == newWidth && currentHeight == newHeight)) {
            return img;
        } else if (currentWidth == currentHeight) {
            scaledWidth = newWidth;
            scaledHeight = newHeight;
        } else if (currentWidth >= currentHeight) {
            scaledWidth = newWidth;
            double scale = (double) newWidth / (double) currentWidth;
            scaledHeight = (int) Math.round(currentHeight * scale);
        } else {
            scaledHeight = newHeight;
            double scale = (double) newHeight / (double) currentHeight;
            scaledWidth = (int) Math.round(currentWidth * scale);
        }

        int x = (newWidth - scaledWidth) / 2;
        int y = (newHeight - scaledHeight) / 2;

        /*
         * This is _very_ painful. I've tried a large number of different permutations here trying to
         * get the white image background to be transparent without success. We've tried different
         * fills, composite types, image types, etc.. I'm moving on now.
         */
        BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImg.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, newWidth, newHeight);
        g.drawImage(img, x, y, x + scaledWidth, y + scaledHeight, 0, 0, currentWidth, currentHeight,
                new Color(0, 0, 0, 0), null);
        g.dispose();

        return newImg;
    }
    public static ObjectNode handleItemTexture(ObjectMapper mapper, Path storage,String fileName) {
        String cleanIdentifier = fileName.substring(fileName.lastIndexOf("/") + 1);
        JsonNode textureFile;
        fileName = fileName.contains("\\")? fileName.replaceAll("\\\\","/") :fileName;
        File textureFilePath;
        /*if (filePath.contains(":")) {
            String[] namespaceSplit = filePath.split(":");
            System.out.println("assets/" + namespaceSplit[0] + "/models/" + fileName+ ".json");
            textureFilePath = storage.resolve("assets/" + namespaceSplit[0] + "/models/" + fileName+ ".json").toFile();
        } else {*/
            System.out.println("assets/minecraft/models/" + fileName + ".json");
            textureFilePath = storage.resolve("assets/minecraft/models/" + fileName + ".json").toFile();
       //}
        if (!textureFilePath.exists()) {
            System.out.println("No texture file found at " + textureFilePath + "; we were given assets/minecraft/models/" + fileName);
            return null;
        }
        try (InputStream stream = new FileInputStream(textureFilePath)) {
            // Read the model information for the Java CustomModelData
            textureFile = mapper.readTree(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // TODO: This is called BSing it. It works but is it correct?
        if (textureFile.has("textures")) {
            if (textureFile.get("textures").has("0") || textureFile.get("textures").has("layer0")) {
                String determine = textureFile.get("textures").has("0") ? "0" : "layer0";
                ObjectNode textureData = mapper.createObjectNode();
                ObjectNode textureName = mapper.createObjectNode();
                // Make JSON data for Bedrock pointing to where texture data for this item is stored
                String textureString = textureFile.get("textures").get(determine).textValue();
                if (textureString.contains(":")) {
                    String[] namespaceSplit = textureString.split(":");
                    String texturePath = "assets/" + namespaceSplit[0] + "/textures/" + fileName;
                    String restOfTheTexturePath = fileName.substring(0, fileName.lastIndexOf("/"));
                    if (!namespaceSplit[0].equals("minecraft")) {
                        File namespaceFile = storage.resolve("textures/items/" + namespaceSplit[0] + "/" + restOfTheTexturePath).toFile();
                        if (!namespaceFile.exists()) {
                            //noinspection ResultOfMethodCallIgnored
                            namespaceFile.mkdirs();
                        }
                        try {
                            // Copy from the original location to a new place in the resource pack
                            // For example: /assets/itemsadder/textures/item/crystal.png to textures/itemsadder/item/crystal.png
                            Files.copy(storage.resolve("textures/items/"+texturePath + ".png"), namespaceFile.toPath().resolve(namespaceSplit[1].substring(namespaceSplit[1].lastIndexOf("/") + 1) + ".png"));
                            textureName.put("textures", "textures/items/" + namespaceSplit[0] + "/" +  namespaceSplit[1]);
                            // Have the identifier point to that texture data
                            textureData.set(cleanIdentifier, textureName);
                            return textureData;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                if (textureString.startsWith("item/")) {
                    textureString = textureString.replace("item/", "textures/items/")+".png";
                } else {
                    textureString = "textures/" + textureString+".png";
                }
                File file1 = storage.resolve(textureString).toFile();
                if(file1.exists()){
                    try {
                        BufferedImage bi = ImageIO.read(file1);
                        if (bi.getWidth() > 16 || bi.getHeight() > 16) {
                            ImageIO.write(scaleWithPadding(bi,16,16),"png",file1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                textureName.put("textures", textureString);
                // Have the identifier point to that texture data
                textureData.set(cleanIdentifier, textureName);
                return textureData;
            }
        }

        return null;
    }

}
