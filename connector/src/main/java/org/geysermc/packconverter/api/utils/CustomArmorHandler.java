/*
 * Copyright (c) 2019-2021 GeyserMC. http://geysermc.org
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonParser;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import org.geysermc.connector.GeyserConnector;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CustomArmorHandler {
    //public static CustomModelData handleCustomArmor(ObjectMapper mapper, Path storage, String originalItemName, String filePath, String fileName, JsonNode itemJsonInfo, JsonNode predicate, String type, Path storageBp,int id) {
        // Start the creation of the JSON that registers the object
       // filePath = filePath.contains(":") ? filePath.split(":")[1] : filePath;
       // ObjectNode item = mapper.createObjectNode();
        // Standard JSON
       // item.put("format_version", "1.10");
       // ObjectNode itemData = mapper.createObjectNode();
       // ObjectNode itemDescription = mapper.createObjectNode();
        //ObjectNode itemWearable = mapper.createObjectNode();
        // Full identifier with geysermc: prefix (cmd for CustomModelData - just in case it clashes with something we do in the future)
        //String identifier = "geysermc:" + originalItemName;
        // Add the original item name as well to prevent conflicts if multiple items share the same model
        //String identifier = "geysermc:" + fileName;
        // Register the full identifier
       // itemDescription.put("identifier", identifier);
       // itemData.set("description", itemDescription);
       // NbtMapBuilder componentBuilder = NbtMap.builder();

        //item.set("minecraft:item", itemData);

        //TODO make sure there can't be duplicates here
       // componentBuilder.putCompound("minecraft:icon", NbtMap.builder().putString("texture", fileName.substring(filePath.lastIndexOf("/") + 1)).build());
        //ObjectNode itemComponent = mapper.createObjectNode();
        // Define which texture in item_texture.json this should use. We just set it to the "clean identifier"
      //  itemComponent.set("minecraft:icon", mapper.createObjectNode().put("texture",identifier.replace("geysermc:", "")));
        //itemComponent.put("allow_off_hand", true);
        //itemComponent.put("hand_equipped", itemJsonInfo.get("hand_equipped").booleanValue());
        //itemComponent.put("max_stack_size", itemJsonInfo.get("max_stack_size").intValue());
        /*itemWearable.put("dispensable", true);
        itemWearable.put("slot", "slot.armor." + type);
        itemComponent.set("minecraft:wearable", itemWearable);
        ObjectNode enchants = mapper.createObjectNode();
        enchants.put("value",10);
        String enchantType = "armor_torso";
        if(type.equals("feet")) enchantType = "armor_feet";
        if(type.equals("legs")) enchantType = "armor_legs";
        if(type.equals("chest")) enchantType = "armor_torso";
        if(type.equals("head")) enchantType = "armor_head";
        enchants.put("slot", enchantType);
        itemComponent.set("minecraft:enchantable", enchants);*/
      //  ObjectNode armor = mapper.createObjectNode();
       // armor.set("texture_type",mapper.createObjectNode());
       // itemComponent.set("minecraft:armor", armor);
      //  String enchantType2 = "";
      //  if(type.equals("feet")) enchantType2 = "boots";
      //  if(type.equals("legs")) enchantType2 = "leggings";
     //   if(type.equals("chest")) enchantType2 = "chestplates";
     //   if(type.equals("head")) enchantType2 = "helmets";
      //  itemComponent.put("minecraft:render_offsets",enchantType2);
     //   itemData.set("components", itemComponent);
//
        // Create, if necessary, the folder that stores all item information
     //   File itemJsonPath = storage.resolve("items").toFile();
     //   if (!itemJsonPath.exists()) {
      //      itemJsonPath.mkdir();
      //  }
      //  String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName + ".json");
      //  String path001 = path002.contains("/") ? path002.substring(0, path002.lastIndexOf("/")) : path002;
     //   Path path2 = itemJsonPath.toPath().resolve(path001);
     //   try (OutputStream outputStream = Files.newOutputStream(path2,
     //           StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
    //        mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, item);
    //    } catch (IOException e) {
      //      e.printStackTrace();
      //      return null;
      //  }
      //  createAttachable(mapper, storage, originalItemName, filePath, fileName, itemJsonInfo, predicate,type);
      //  createEntries(mapper, storageBp, originalItemName, filePath, fileName, itemJsonInfo, predicate,type);
      //  CustomModelData customModelData = new CustomModelData();
      //  customModelData.setIdentifier(identifier);
       // customModelData.setNbt(componentBuilder.build());
      // return customModelData;
    //}
    public static void handleCustomArmor_normal(ObjectMapper mapper, Path storage, String originalItemName, /*String filePath,*/ String fileName, /*JsonNode itemJsonInfo, JsonNode predicate,*/ String type, Path storageBp/*,int id*/) {
        // Start the creation of the JSON that registers the object
      //  filePath = filePath.contains(":") ? filePath.split(":")[1] : filePath;
        ObjectNode item = mapper.createObjectNode();
        // Standard JSON
        item.put("format_version", "1.10");
        ObjectNode itemData = mapper.createObjectNode();
        ObjectNode itemDescription = mapper.createObjectNode();
        ObjectNode itemWearable = mapper.createObjectNode();
        // Full identifier with geysermc: prefix (cmd for CustomModelData - just in case it clashes with something we do in the future)
        String identifier = "geysermc:" + originalItemName;
        // Add the original item name as well to prevent conflicts if multiple items share the same model
        //String identifier = "geysermc:" + fileName;
        // Register the full identifier
        itemDescription.put("identifier", identifier);
        itemData.set("description", itemDescription);
        //NbtMapBuilder componentBuilder = NbtMap.builder();

        item.set("minecraft:item", itemData);

        //TODO make sure there can't be duplicates here
        //componentBuilder.putCompound("minecraft:icon", NbtMap.builder().putString("texture", fileName.substring(filePath.lastIndexOf("/") + 1)).build());
        ObjectNode itemComponent = mapper.createObjectNode();
        // Define which texture in item_texture.json this should use. We just set it to the "clean identifier"
        itemComponent.set("minecraft:icon", mapper.createObjectNode().put("texture",identifier.replace("geysermc:", "")));
        itemComponent.put("allow_off_hand", true);
        //itemComponent.put("hand_equipped", itemJsonInfo.get("hand_equipped").booleanValue());
        //itemComponent.put("max_stack_size", itemJsonInfo.get("max_stack_size").intValue());
        itemWearable.put("dispensable", true);
        itemWearable.put("slot", "slot.armor." + type);
        itemComponent.set("minecraft:wearable", itemWearable);
        ObjectNode enchants = mapper.createObjectNode();
        enchants.put("value",10);
        String enchantType = "armor_torso";
        if(type.equals("feet")) enchantType = "armor_feet";
        if(type.equals("legs")) enchantType = "armor_legs";
        if(type.equals("chest")) enchantType = "armor_torso";
        if(type.equals("head")) enchantType = "armor_head";
        enchants.put("slot", enchantType);
        itemComponent.set("minecraft:enchantable", enchants);
        ObjectNode armor = mapper.createObjectNode();
        armor.set("texture_type",mapper.createObjectNode());
        itemComponent.set("minecraft:armor", armor);
        String enchantType2 = "";
        if(type.equals("feet")) enchantType2 = "boots";
        if(type.equals("legs")) enchantType2 = "leggings";
        if(type.equals("chest")) enchantType2 = "chestplates";
        if(type.equals("head")) enchantType2 = "helmets";
        itemComponent.put("minecraft:render_offsets",enchantType2);
        itemData.set("components", itemComponent);

        // Create, if necessary, the folder that stores all item information
        File itemJsonPath = storageBp.resolve("items").toFile();
        if (!itemJsonPath.exists()) {
            itemJsonPath.mkdir();
        }
        String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName + ".json");
        String path001 = path002.contains("/") ? path002.substring(0, path002.lastIndexOf("/")) : path002;
        Path path2 = itemJsonPath.toPath().resolve(path001);
        try (OutputStream outputStream = Files.newOutputStream(path2,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, item);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        createAttachable(mapper, storage, originalItemName, /*filePath,*/ fileName,/* itemJsonInfo, predicate,*/type);
        createEntries(mapper, storageBp, originalItemName, /*filePath,*/  fileName,/* itemJsonInfo, predicate,*/type);
        //CustomModelData customModelData = new CustomModelData();
        //customModelData.setIdentifier(identifier);
        //customModelData.setNbt(componentBuilder.build());
        return;
    }
    private static void createEntries(ObjectMapper mapper, Path storage, String originalItemName/*,String filePath*/, String fileName, /*JsonNode itemJsonInfo, JsonNode predicate,*/ String type) {
        // Start the creation of the JSON that registers the object
        //filePath = filePath.contains(":") ? filePath.split(":")[1] : filePath;
        ObjectNode item = mapper.createObjectNode();
        // Standard JSON
        item.put("format_version", "1.10");
        ObjectNode itemData = mapper.createObjectNode();
        ObjectNode itemDescription = mapper.createObjectNode();
        ObjectNode itemWearable = mapper.createObjectNode();
        // Full identifier with geysermc: prefix (cmd for CustomModelData - just in case it clashes with something we do in the future)
        String identifier = "geysermc:" + originalItemName;
        // Add the original item name as well to prevent conflicts if multiple items share the same model
        //String identifier = "geysermc:" + fileName;
        // Register the full identifier
        itemDescription.put("identifier", identifier);
        itemData.set("description", itemDescription);
        NbtMapBuilder componentBuilder = NbtMap.builder();

        item.set("minecraft:item", itemData);

        //TODO make sure there can't be duplicates here
        //componentBuilder.putCompound("minecraft:icon", NbtMap.builder().putString("texture", fileName.substring(filePath.lastIndexOf("/") + 1)).build());
        ObjectNode itemComponent = mapper.createObjectNode();
        // Define which texture in item_texture.json this should use. We just set it to the "clean identifier"
        itemComponent.set("minecraft:icon", mapper.createObjectNode().put("texture",identifier.replace("geysermc:", "")));
        itemComponent.put("allow_off_hand", true);
        //itemComponent.put("hand_equipped", itemJsonInfo.get("hand_equipped").booleanValue());
       // itemComponent.put("max_stack_size", itemJsonInfo.get("max_stack_size").intValue());
        itemWearable.put("dispensable", true);
        itemWearable.put("slot", "slot.armor." + type);
        itemComponent.set("minecraft:wearable", itemWearable);
        ObjectNode enchants = mapper.createObjectNode();
        enchants.put("value",10);
        String enchantType = "armor_torso";
        if(type.equals("feet")) enchantType = "armor_feet";
        if(type.equals("legs")) enchantType = "armor_legs";
        if(type.equals("chest")) enchantType = "armor_torso";
        if(type.equals("head")) enchantType = "armor_head";
        enchants.put("slot", enchantType);
        itemComponent.set("minecraft:enchantable", enchants);
        ObjectNode armor = mapper.createObjectNode();
        armor.put("texture_type","");
        itemComponent.set("minecraft:armor", armor);
        String enchantType2 = "";
        itemData.set("components", itemComponent);

        // Create, if necessary, the folder that stores all item information
        File itemJsonPath = storage.resolve("items").toFile();
        if (!itemJsonPath.exists()) {
            itemJsonPath.mkdir();
        }
        String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName + ".json");
        String path001 = path002.contains("/") ? path002.substring(0, path002.lastIndexOf("/")) : path002;
        Path path2 = itemJsonPath.toPath().resolve(path001);
        try (OutputStream outputStream = Files.newOutputStream(path2,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            mapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream, item);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
    private static void createAttachable(ObjectMapper mapper, Path storage, String originalItemName, /*String filePath,*/ String fileName,/* JsonNode itemJsonInfo, JsonNode predicate,*/ String type) {
        //filePath = filePath.contains(":") ? filePath.split(":")[1] : filePath;
        // Full identifier with geysermc: prefix (cmd for CustomModelData - just in case it clashes with something we do in the future)
        String identifier = "geysermc:" + originalItemName;
        String enchantType = "armor_torso";
        if(type.equals("feet")) enchantType = "boots";
        if(type.equals("chest")) enchantType = "chestplate";
        if(type.equals("legs")) enchantType = "leggings";
        if(type.equals("head")) enchantType = "helmet";
        String enchantType1 = "armor_torso";
        if(type.equals("feet")) enchantType1 = "boots";
        if(type.equals("chest")) enchantType1 = "chest";
        if(type.equals("legs")) enchantType1 = "leg";
        if(type.equals("head")) enchantType1 = "helmet";
        String jsonStringSinceTooLazyToWrite = "{\"format_version\": \"1.10\",\"minecraft:attachable\": {\"description\": {\"identifier\": \""+identifier+"\",\"materials\": {\"default\": \"armor\",\"enchanted\": \"armor_enchanted\"},\"textures\": {\"default\": \"textures/default/armors/"+((type.contains("feet") || type.contains("legs"))? originalItemName.split("_")[0] +"_armor_layer_2" : originalItemName.split("_")[0] +"_armor_layer_1" )+"\",\"enchanted\": \"textures/misc/enchanted_item_glint\"},\"geometry\": {\"default\": \"geometry.humanoid.armor."+enchantType+"\"},\"scripts\": {\"parent_setup\": \"variable."+enchantType1+"_layer_visible = 0.0;\"},\"render_controllers\": [\"controller.render.armor\"]}}}";
        String jsonStringSinceTooLazyToWrite1 = "{\"format_version\": \"1.10\",\"minecraft:attachable\": {\"description\": {\"identifier\": \""+identifier+".player\",\"item\": {\""+identifier+"\": \"query.owner_identifier == 'minecraft:player'\"},\"materials\": {\"default\": \"armor\",\"enchanted\": \"armor_enchanted\"},\"textures\": {\"default\": \"textures/default/armors/"+((type.contains("feet") || type.contains("legs"))? originalItemName.split("_")[0] +"_armor_layer_2" : originalItemName.split("_")[0] +"_armor_layer_1" )+"\",\"enchanted\": \"textures/misc/enchanted_item_glint\"},\"geometry\": {\"default\": \"geometry.player.armor."+enchantType+"\"},\"scripts\": {\"parent_setup\": \"variable."+enchantType1+"_layer_visible = 0.0;\"},\"render_controllers\": [\"controller.render.armor\"]}}}";
        // Create, if necessary, the folder that stores all item information
        File itemJsonPath = storage.resolve("attachables").toFile();
        if (!itemJsonPath.exists()) {
            itemJsonPath.mkdir();
        }
        String layerTextureString = ((type.contains("feet") || type.contains("legs"))? originalItemName.split("_")[0] +"_armor_layer_2" : originalItemName.split("_")[0] +"_armor_layer_1" )+".png";
        Path oraxenFolder = GeyserConnector.getInstance().getBootstrap().getConfigFolder().getParent().resolve("Oraxen/pack/textures/default/armors");
        String thingyfortextures = "textures/default/armors/"+ layerTextureString;
        if(!storage.resolve(thingyfortextures).toFile().exists()) {
            if(oraxenFolder.toFile().exists()) {
                if(oraxenFolder.resolve(layerTextureString).toFile().exists()) {
                    try {
                        Files.copy(oraxenFolder.resolve(layerTextureString), storage.resolve(thingyfortextures));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String path002 = (fileName.contains("item/") ? fileName.replace("item/", "") + ".json" : fileName + ".json");
        String path001 = path002.contains("/") ? path002.substring(0, path002.lastIndexOf("/")) : path002;
        Path path2 = itemJsonPath.toPath().resolve(path001);
        try (OutputStream outputStream = Files.newOutputStream(path2,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            String final1 = new JsonParser().parse(jsonStringSinceTooLazyToWrite).toString();
            byte[] bytes = final1.getBytes();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Path path3 = itemJsonPath.toPath().resolve(path001.replace(".json",".player.json"));
        try (OutputStream outputStream = Files.newOutputStream(path3,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            String final1 = new JsonParser().parse(jsonStringSinceTooLazyToWrite1).toString();
            byte[] bytes = final1.getBytes();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
