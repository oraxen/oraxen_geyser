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

import com.arakelian.jq.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonParser;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import org.geysermc.packconverter.api.converters.CustomModelDataConverter;
import repackaged.com.arakelian.jq.com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomModelDataHandler {
    public static JsonNode playerEntity1;

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
        if (file1.exists()) {
            try {
                BufferedImage bi = ImageIO.read(file1);
                if (bi.getWidth() > 16 || bi.getHeight() > 16) {
                    x = (0.075f / (bi.getWidth() / 16));
                    y = (0.125f / (bi.getHeight() / 16));
                    z = (0.075f / (bi.getWidth() / 16));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //ObjectNode renderOffsets = mapper.createObjectNode();
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
        String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName + ".json");
        String path001 = path002.contains("/") ? path002.substring(0, path002.lastIndexOf("/")) : path002;
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

    private static JqLibrary library = ImmutableJqLibrary.of();

    public static void threeDeeModelzBaby(ObjectMapper mapper, Path storage, String originalItemName, String fileName, JsonNode jsonData) {
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
        /*int width, height = 16;
        File file1 = storage.resolve("assets/minecraft/" + fileName + ".png").toFile();
        if (file1.exists()) {
            try {
                BufferedImage bi = ImageIO.read(file1);
                width = bi.getWidth();
                height = bi.getHeight();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        ObjectNode itemComponent = mapper.createObjectNode();
        itemComponent.put("minecraft:icon", identifier.replace("geysermc:", ""));
        itemComponent.put("minecraft:allow_off_hand", true);
        itemData.set("components", itemComponent);

        // Create, if necessary, the folder that stores all item information
        File itemJsonPath = storage.resolve("items").toFile();
        if (!itemJsonPath.exists()) {
            itemJsonPath.mkdir();
        }
        String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName + ".json");
        String path001 = path002.contains("/") ? path002.substring(0, path002.lastIndexOf("/")) : path002;
        Path path2 = itemJsonPath.toPath().resolve(path001);
       /* if(Files.notExists(path2.getParent())){
            path2.getParent().toFile().mkdirs();
        }*/
        // Write our item information
        //Path path = itemJsonPath.toPath().resolve(filePath.substring(filePath.lastIndexOf("/") + 1) + ".json");
        mapGeometry(mapper, storage, originalItemName, fileName, jsonData);
        mapAnimations(mapper, storage, originalItemName, fileName, jsonData);
        mapAttachable(mapper, storage, originalItemName, fileName, jsonData);
        try (OutputStream outputStream = Files.newOutputStream(path2,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, item);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public static String JQToJson(ObjectMapper mapper, JsonNode input, String filter) {
        final JqRequest request = ImmutableJqRequest.builder()
                .lib(library)
                .input(input.toString())
                .filter(filter)
                .build();
        final JqResponse response = request.execute();
        if (response.hasErrors()) {
            System.out.println("Error in converting 3D model using JQ, Error message: " + response.getErrors());
            return null;
        } else {
            return response.getOutput();
        }
    }

    public static void mapGeometry(ObjectMapper mapper, Path storage, String originalItemName, String fileName, JsonNode jsonData) {
        String filter = "def element_array:" +
                "(.textures | to_entries | sort_by(.key) | map({(.key): .value}) | add | keys_unsorted) as $texture_array" +
                "| ($texture_array | length) as $frames" +
                "| (($frames | sqrt) | ceil) as $sides" +
                "| (.texture_size[1] // 16) as $t1" +
                "| .elements | map({" +
                "  \"origin\": [(-.to[0] + 8), (.from[1]), (.from[2] - 8)]," +
                "  \"size\": [.to[0] - .from[0], .to[1] - .from[1], .to[2] - .from[2]]," +
                "  \"rotation\": (if (.rotation.axis) == \"x\" then [(.rotation.angle | tonumber * -1), 0, 0] elif (.rotation.axis) == \"y\" then [0, (.rotation.angle | tonumber * -1), 0] elif (.rotation.axis) == \"z\" then [0, 0, (.rotation.angle | tonumber)] else null end)," +
                "  \"pivot\": (if .rotation.origin then [(- .rotation.origin[0] + 8), .rotation.origin[1], (.rotation.origin[2] - 8)] else null end)," +
                "  \"uv\": (" +
                "def uv_calc($input):" +
                "  (if (.faces | .[$input]) then" +
                "  (.faces | .[$input].texture[1:] as $input_n | $texture_array | (index($input_n) // index(\"particle\"))) as $pos_n" +
                "  | ((.faces | .[$input].uv[0] / $sides) + ((fmod($pos_n; $sides)) * (16 / $sides))) as $fn0" +
                "  | ((.faces | .[$input].uv[1] / $sides) + ((($pos_n / $sides) | floor) * (16 / $sides))) as $fn1" +
                "  | ((.faces | .[$input].uv[2] / $sides) + ((fmod($pos_n; $sides)) * (16 / $sides))) as $fn2" +
                "  | ((.faces | .[$input].uv[3] / $sides) + ((($pos_n / $sides) | floor) * (16 / $sides))) as $fn3 |" +
                "  {" +
                "\"uv\": [($fn0), ($fn1)]," +
                "\"uv_size\": [($fn2 - $fn0), ($fn3 - $fn1)]" +
                "  } else null end);" +
                "{" +
                "\"north\": uv_calc(\"north\")," +
                "\"south\": uv_calc(\"south\")," +
                "\"east\": uv_calc(\"east\")," +
                "\"west\": uv_calc(\"west\")," +
                "\"up\": uv_calc(\"up\")," +
                "\"down\": uv_calc(\"down\")" +
                "})" +
                "}) | walk( if type == \"object\" then with_entries(select(.value != null)) else . end)" +
                ";" +
                "def pivot_groups:" +
                "(element_array) as $element_array |" +
                "[[.elements[].rotation] | unique | .[] | select (.!=null)]" +
                "| map((" +
                "[(- .origin[0] + 8), .origin[1], (.origin[2] - 8)] as $i_piv |" +
                "(if (.axis) == \"x\" then [(.angle | tonumber * -1), 0, 0] elif (.axis) == \"y\" then [0, (.angle | tonumber * -1), 0] else [0, 0, (.angle | tonumber)] end) as $i_rot |" +
                "{" +
                "  \"parent\": \"geysermc_z\"," +
                "  \"pivot\": ($i_piv)," +
                "  \"rotation\": ($i_rot)," +
                "  \"mirror\": true," +
                "  \"cubes\": [($element_array | .[] | select(.rotation == $i_rot and .pivot == $i_piv))]" +
                "}))" +
                ";" +
                "{" +
                "  \"format_version\": \"1.16.0\"," +
                "  \"minecraft:geometry\": [{" +
                "\"description\": {" +
                "  \"identifier\": (\"geometry.geysermc." + originalItemName + "\")," +
                "  \"texture_width\": 16," +
                "  \"texture_height\": 16," +
                "  \"visible_bounds_width\": 4," +
                "  \"visible_bounds_height\": 4.5," +
                "  \"visible_bounds_offset\": [0, 0.75, 0]" +
                "}," +
                "\"bones\": ([{" +
                "  \"name\": \"geysermc\"," +
                "  \"binding\": \"c.item_slot == 'head' ? 'head' : q.item_slot_to_bone_name(c.item_slot)\"," +
                "  \"pivot\": [0, 8, 0]" +
                "}, {" +
                "  \"name\": \"geysermc_x\"," +
                "  \"parent\": \"geysermc\"," +
                "  \"pivot\": [0, 8, 0]" +
                "}, {" +
                "  \"name\": \"geysermc_y\"," +
                "  \"parent\": \"geysermc_x\"," +
                "  \"pivot\": [0, 8, 0]" +
                "}, {" +
                "  \"name\": \"geysermc_z\"," +
                "  \"parent\": \"geysermc_y\"," +
                "  \"pivot\": [0, 8, 0]," +
                "  \"cubes\": [(element_array | .[] | select(.rotation == null))]" +
                "}] + (pivot_groups | map(del(.cubes[].rotation)) | to_entries | map( (.value.name = \"rot_\\(1+.key)\" ) | .value)))" +
                "  }]" +
                "}";
        try {
            String response = JQToJson(mapper, jsonData, filter);
            File itemJsonPath = storage.resolve("models/entity/geysermc").toFile();
            if (!itemJsonPath.exists()) {
                itemJsonPath.mkdirs();
            }
            String path001 = (originalItemName) + ".json";
            Path path2 = itemJsonPath.toPath().resolve(path001);
            try (OutputStream outputStream = Files.newOutputStream(path2, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
                String final1 = new JsonParser().parse(response).toString();
                byte[] bytes = final1.getBytes();
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } catch (Exception e) {
            System.out.println("Error in converting 3D model's geometry " + originalItemName + ", Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void mapAnimations(ObjectMapper mapper, Path storage, String originalItemName, String fileName, JsonNode jsonData) {
        String filter = "{\"format_version\": \"1.8.0\"," +
                "\"animations\": {" +
                "(\"animation.geysermc." + originalItemName + ".thirdperson_main_hand\"): {" +
                "\"loop\": true," +
                "\"bones\": {" +
                "\"geysermc_x\": (if .display.thirdperson_righthand then {" +
                "\"rotation\": (if .display.thirdperson_righthand.rotation then [(- .display.thirdperson_righthand.rotation[0]), 0, 0] else null end)," +
                "\"position\": (if .display.thirdperson_righthand.translation then [(- .display.thirdperson_righthand.translation[0]), (.display.thirdperson_righthand.translation[1]), (.display.thirdperson_righthand.translation[2])] else null end)," +
                "      \"scale\": (if .display.thirdperson_righthand.scale then [(.display.thirdperson_righthand.scale[0]), (.display.thirdperson_righthand.scale[1]), (.display.thirdperson_righthand.scale[2])] else null end)" +
                "} else null end)," +
                "    \"geysermc_y\": (if .display.thirdperson_righthand.rotation then {" +
                " \"rotation\": (if .display.thirdperson_righthand.rotation then [0, (- .display.thirdperson_righthand.rotation[1]), 0] else null end)" +
                "} else null end)," +
                "    \"geysermc_z\": (if .display.thirdperson_righthand.rotation then {" +
                "   \"rotation\": [0, 0, (.display.thirdperson_righthand.rotation[2])]" +
                "} else null end)," +
                "    \"geysermc\": {" +
                "       \"rotation\": [90, 0, 0]," +
                "       \"position\": [0, 13, -3]" +
                "    }" +
                "}" +
                "}," +
                "(\"animation.geysermc." + originalItemName + ".thirdperson_off_hand\"): {" +
                "\"loop\": true," +
                "\"bones\": {" +
                "\"geysermc_x\": (if .display.thirdperson_lefthand then {" +
                "\"rotation\": (if .display.thirdperson_lefthand.rotation then [(- .display.thirdperson_lefthand.rotation[0]), 0, 0] else null end)," +
                "\"position\": (if .display.thirdperson_lefthand.translation then [(- .display.thirdperson_lefthand.translation[0]), (.display.thirdperson_lefthand.translation[1]), (.display.thirdperson_lefthand.translation[2])] else null end)," +
                "      \"scale\": (if .display.thirdperson_lefthand.scale then [(.display.thirdperson_lefthand.scale[0]), (.display.thirdperson_lefthand.scale[1]), (.display.thirdperson_lefthand.scale[2])] else null end)" +
                "} else null end)," +
                "    \"geysermc_y\": (if .display.thirdperson_lefthand.rotation then {" +
                " \"rotation\": (if .display.thirdperson_lefthand.rotation then [0, (- .display.thirdperson_lefthand.rotation[1]), 0] else null end)" +
                "} else null end)," +
                "    \"geysermc_z\": (if .display.thirdperson_lefthand.rotation then {" +
                "   \"rotation\": [0, 0, (.display.thirdperson_lefthand.rotation[2])]" +
                "} else null end)," +
                "    \"geysermc\": {" +
                "       \"rotation\": [90, 0, 0]," +
                "       \"position\": [0, 13, -3]" +
                "    }" +
                "}" +
                "}," +
                "(\"animation.geysermc." + originalItemName + ".head\"): {" +
                "\"loop\": true," +
                "\"bones\": {" +
                "    \"geysermc_x\": {" +
                "\"rotation\": (if .display.head.rotation then [(- .display.head.rotation[0]), 0, 0] else null end)," +
                "\"position\": (if .display.head.translation then [(- .display.head.translation[0] * 0.625), (.display.head.translation[1] * 0.625), (.display.head.translation[2] * 0.625)] else null end)," +
                "      \"scale\": (if .display.head.scale then (.display.head.scale | map(. * 0.625)) else 0.625 end)" +
                "}," +
                "    \"geysermc_y\": (if .display.head.rotation then {" +
                "\"rotation\": [0, (- .display.head.rotation[1]), 0]" +
                "} else null end)," +
                "    \"geysermc_z\": (if .display.head.rotation then {" +
                "\"rotation\": [0, 0, (.display.head.rotation[2])]" +
                "} else null end)," +
                "    \"geysermc\": {" +
                "       \"position\": [0, 19.5, 0]" +
                "    }" +
                "}" +
                "}," +
                "(\"animation.geysermc." + originalItemName + ".firstperson_main_hand\"): {" +
                "\"loop\": true," +
                "\"bones\": {" +
                "    \"geysermc\": {" +
                "\"rotation\": [90, 60, -40]," +
                "\"position\": [4, 10, 4]," +
                "      \"scale\": 1.5" +
                "}," +
                "\"geysermc_x\": {" +
                "\"position\": (if .display.firstperson_righthand.translation then [(- .display.firstperson_righthand.translation[0]), (.display.firstperson_righthand.translation[1]), (- .display.firstperson_righthand.translation[2])] else null end)," +
                "\"rotation\": (if .display.firstperson_righthand.rotation then [(- .display.firstperson_righthand.rotation[0]), 0, 0] else [0.1, 0.1, 0.1] end)," +
                "      \"scale\": (if .display.firstperson_righthand.scale then (.display.firstperson_righthand.scale) else null end)" +
                "}," +
                "    \"geysermc_y\": (if .display.firstperson_righthand.rotation then {" +
                "\"rotation\": [0, (- .display.firstperson_righthand.rotation[1]), 0]" +
                "} else null end)," +
                "    \"geysermc_z\": (if .display.firstperson_righthand.rotation then {" +
                "\"rotation\": [0, 0, (.display.firstperson_righthand.rotation[2])]" +
                "} else null end)" +
                "}" +
                "}," +
                "(\"animation.geysermc." + originalItemName + ".firstperson_off_hand\"): {" +
                "\"loop\": true," +
                "\"bones\": {" +
                "    \"geysermc\": {" +
                "\"rotation\": [90, 60, -40]," +
                "\"position\": [4, 10, 4]," +
                "      \"scale\": 1.5" +
                "}," +
                "\"geysermc_x\": {" +
                "\"position\": (if .display.firstperson_lefthand.translation then [(.display.firstperson_lefthand.translation[0]), (.display.firstperson_lefthand.translation[1]), (- .display.firstperson_lefthand.translation[2])] else null end)," +
                "\"rotation\": (if .display.firstperson_lefthand.rotation then [(- .display.firstperson_lefthand.rotation[0]), 0, 0] else [0.1, 0.1, 0.1] end)," +
                "      \"scale\": (if .display.firstperson_lefthand.scale then (.display.firstperson_lefthand.scale) else null end)" +
                "}," +
                "    \"geysermc_y\": (if .display.firstperson_lefthand.rotation then {" +
                "\"rotation\": [0, (- .display.firstperson_lefthand.rotation[1]), 0]" +
                "} else null end)," +
                "    \"geysermc_z\": (if .display.firstperson_lefthand.rotation then {" +
                "\"rotation\": [0, 0, (.display.firstperson_lefthand.rotation[2])]" +
                "} else null end)" +
                "}" +
                "}" +
                "}" +
                "} | walk( if type == \"object\" then with_entries(select(.value != null)) else . end)";
        String s = "animation." + originalItemName;
        try {
            String response = JQToJson(mapper, jsonData, filter);
            File itemJsonPath = storage.resolve("animations/geysermc").toFile();
            if (!itemJsonPath.exists()) {
                itemJsonPath.mkdirs();
            }
            String path001 = s + ".json";
            Path path2 = itemJsonPath.toPath().resolve(path001);
            try (OutputStream outputStream = Files.newOutputStream(path2, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
                String final1 = new JsonParser().parse(response).toString();
                byte[] bytes = final1.getBytes();
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } catch (Exception e) {
            System.out.println("Error in converting 3D model's animation " + originalItemName + ", Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void mapAttachable(ObjectMapper mapper, Path storage, String originalItemName, String fileName, JsonNode jsonData) {
        //ArrayNode arrayNode = new ArrayNode();
        String texture1 = "";
        Map<String, String> map = mapper.convertValue(jsonData.get("textures"), Map.class);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            texture1 = entry.getValue();
            break;
        }
        String filter = "{" +
                "  \"format_version\": \"1.10.0\"," +
                "  \"minecraft:attachable\": {" +
                "    \"description\": {" +
                "      \"identifier\": (\"geysermc:" + originalItemName + "\")," +
                "      \"materials\": {" +
                "        \"default\": \"entity_alphatest\"," +
                "        \"enchanted\": \"entity_alphatest\"" +
                "      }," +
                "      \"textures\": {" +
                "        \"default\": (\"textures/" + texture1 + "\")," +
                "        \"enchanted\": \"textures/misc/enchanted_item_glint.png\"" +
                "      }," +
                "      \"geometry\": {" +
                "        \"default\": (\"geometry.geysermc." + originalItemName + "\")" +
                "      }," +
                "      \"scripts\": {" +
                "        \"pre_animation\": [\"v.main_hand = c.item_slot == 'main_hand';\", \"v.off_hand = c.item_slot == 'off_hand';\", \"v.head = c.item_slot == 'head';\"]," +
                "        \"animate\": [" +
                "          {\"thirdperson_main_hand\": \"v.main_hand && !c.is_first_person\"}," +
                "          {\"thirdperson_off_hand\": \"v.off_hand && !c.is_first_person\"}," +
                "          {\"thirdperson_head\": \"v.head && !c.is_first_person\"}," +
                "          {\"firstperson_main_hand\": \"v.main_hand && c.is_first_person\"}," +
                "          {\"firstperson_off_hand\": \"v.off_hand && c.is_first_person\"}," +
                "          {\"firstperson_head\": \"c.is_first_person && v.head\"}" +
                "        ]" +
                "      }," +
                "      \"animations\": {" +
                "        \"thirdperson_main_hand\": (\"animation.geysermc." + originalItemName + ".thirdperson_main_hand\")," +
                "        \"thirdperson_off_hand\": (\"animation.geysermc." + originalItemName + ".thirdperson_off_hand\")," +
                "        \"thirdperson_head\": (\"animation.geysermc." + originalItemName + ".head\")," +
                "        \"firstperson_main_hand\": (\"animation.geysermc." + originalItemName + ".firstperson_main_hand\")," +
                "        \"firstperson_off_hand\": (\"animation.geysermc." + originalItemName + ".firstperson_off_hand\")," +
                "        \"firstperson_head\": \"animation.geysermc.disable\"" +
                "      }," +
                "      \"render_controllers\": [ \"controller.render.item_default\" ]" +
                "    }" +
                "  }" +
                "}";
        try {
            String response = JQToJson(mapper, jsonData, filter);
            File itemJsonPath = storage.resolve("attachables").toFile();
            if (!itemJsonPath.exists()) {
                itemJsonPath.mkdirs();
            }
            String path001 = (originalItemName) + ".json";
            Path path2 = itemJsonPath.toPath().resolve(path001);
            try (OutputStream outputStream = Files.newOutputStream(path2, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
                String final1 = new JsonParser().parse(response).toString();
                byte[] bytes = final1.getBytes();
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } catch (Exception e) {
            System.out.println("Error in converting 3D model's animation " + originalItemName + ", Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ObjectNode handleItemTexture(ObjectMapper mapper, Path storage, String fileName) {
        String cleanIdentifier = fileName.substring(fileName.lastIndexOf("/") + 1);
        JsonNode textureFile;
        fileName = fileName.contains("\\") ? fileName.replaceAll("\\\\", "/") : fileName;
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
                            Files.copy(storage.resolve("textures/items/" + texturePath + ".png"), namespaceFile.toPath().resolve(namespaceSplit[1].substring(namespaceSplit[1].lastIndexOf("/") + 1) + ".png"));
                            textureName.put("textures", "textures/items/" + namespaceSplit[0] + "/" + namespaceSplit[1]);
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
                    textureString = textureString.replace("item/", "textures/items/");
                } else {
                    textureString = "textures/" + textureString;
                }
                File file1 = storage.resolve(textureString).toFile();
                if (file1.exists()) {
                    try {
                        BufferedImage bi = ImageIO.read(file1);
                        if (bi.getWidth() > 16 || bi.getHeight() > 16) {
                            ImageIO.write(scaleWithPadding(bi, 16, 16), "png", file1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ObjectNode textureName1 = textureName;
                textureName.put("textures", textureString);
                // Have the identifier point to that texture data
                textureData.set(cleanIdentifier, textureName);
                return textureData;
            }
            else if (textureFile.get("textures").has("all")) {
                String determine = "all";
                ObjectNode textureData = mapper.createObjectNode();
                ObjectNode textureName = mapper.createObjectNode();
                // Make JSON data for Bedrock pointing to where texture data for this item is stored
                String textureString = textureFile.get("textures").get(determine).textValue();
                if (textureString.startsWith("item/")) {
                    textureString = textureString.replace("item/", "textures/items/");
                } else {
                    textureString = "textures/" + textureString;
                }
                textureName.put("textures", textureString);
                // Have the identifier point to that texture data
                textureData.set("zzz_"+cleanIdentifier, textureName);
                return textureData;
            }
        }
        return null;
    }

}
