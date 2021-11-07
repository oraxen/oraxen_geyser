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
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CustomBlockHandler {

    public static ObjectNode handleBlockData(ObjectMapper mapper, Path storage, String originalItemName) {
        ObjectNode block = mapper.createObjectNode();
        //String identifier = "geysermc:" + originalItemName;
        ObjectNode finalBlock = mapper.createObjectNode();
        finalBlock.put("up","zzz_"+originalItemName);
        finalBlock.put("down","zzz_"+originalItemName);
        finalBlock.put("north","zzz_"+originalItemName);
        finalBlock.put("south","zzz_"+originalItemName);
        finalBlock.put("west","zzz_"+originalItemName);
        finalBlock.put("east","zzz_"+originalItemName);
        block.set("textures",finalBlock);
        block.put("sound","stone");
        //finalBlock.set(identifier,block);
        return block;
    }
    public static String handleItemTexture(ObjectMapper mapper, Path storage, String fileName) {
        JsonNode textureFile;
        fileName = fileName.contains("\\") ? fileName.replaceAll("\\\\", "/") : fileName;
        File textureFilePath;
        System.out.println("assets/minecraft/models/" + fileName + ".json");
        textureFilePath = storage.resolve("assets/minecraft/models/" + fileName + ".json").toFile();
        if (!textureFilePath.exists()) {
            System.out.println("No texture file found at " + textureFilePath + "; we were given assets/minecraft/models/" + fileName);
            return null;
        }
        try (InputStream stream = new FileInputStream(textureFilePath)) {
            textureFile = mapper.readTree(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // TODO: This is called BSing it. It works but is it correct?
        if (textureFile.has("textures")) {
            if (textureFile.get("textures").has("all")) {
                String determine = "all";
                String textureString = textureFile.get("textures").get(determine).textValue();
                if (textureString.startsWith("item/")) {
                    textureString = textureString.replace("item/", "textures/items/");
                } else {
                    textureString = "textures/" + textureString;
                }
                return textureString;
            }
        }
        return null;
    }
}
