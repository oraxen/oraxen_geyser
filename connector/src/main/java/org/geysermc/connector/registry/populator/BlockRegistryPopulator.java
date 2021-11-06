/*
 * Copyright (c) 2019-2021 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.connector.registry.populator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;
import com.nukkitx.nbt.*;
import com.nukkitx.protocol.bedrock.data.inventory.ComponentItemData;
import com.nukkitx.protocol.bedrock.v448.Bedrock_v448;
import com.nukkitx.protocol.bedrock.v465.Bedrock_v465;
import com.nukkitx.protocol.bedrock.v471.Bedrock_v471;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import org.geysermc.connector.GeyserConnector;
import org.geysermc.connector.network.translators.world.block.BlockStateValues;
import org.geysermc.connector.network.translators.world.chunk.BlockStorage;
import org.geysermc.connector.network.translators.world.chunk.ChunkSection;
import org.geysermc.connector.registry.BlockRegistries;
import org.geysermc.connector.registry.type.BlockMapping;
import org.geysermc.connector.registry.type.BlockMappings;
import org.geysermc.connector.utils.BlockUtils;
import org.geysermc.connector.utils.FileUtils;
import org.geysermc.connector.utils.PistonBehavior;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiFunction;
import java.util.zip.GZIPInputStream;

/**
 * Populates the block registries.
 */
public class BlockRegistryPopulator {
    public static List<NbtMapBuilder> blockStates = new ArrayList<>();
    public static List<String> blockStateIds = new ArrayList<>();
    public static List<JsonNode> blockStatesNode = new ArrayList<>();
    public static List<String> customBlockTags = new ArrayList<>();
    public static Map<String, NbtMap> customBlocks = new HashMap<>();

    private static final ImmutableMap<ObjectIntPair<String>, BiFunction<String, NbtMapBuilder, String>> BLOCK_MAPPERS;
    private static final BiFunction<String, NbtMapBuilder, String> EMPTY_MAPPER = (bedrockIdentifier, statesBuilder) -> null;

    static {
        ImmutableMap.Builder<ObjectIntPair<String>, BiFunction<String, NbtMapBuilder, String>> stateMapperBuilder = ImmutableMap.<ObjectIntPair<String>, BiFunction<String, NbtMapBuilder, String>>builder()
                .put(ObjectIntPair.of("1_17_10", Bedrock_v448.V448_CODEC.getProtocolVersion()), EMPTY_MAPPER)
                .put(ObjectIntPair.of("1_17_30", Bedrock_v465.V465_CODEC.getProtocolVersion()), EMPTY_MAPPER)
                .put(ObjectIntPair.of("1_17_40", Bedrock_v471.V471_CODEC.getProtocolVersion()), EMPTY_MAPPER);

        BLOCK_MAPPERS = stateMapperBuilder.build();
    }

    /**
     * Stores the raw blocks JSON until it is no longer needed.
     */
    private static JsonNode BLOCKS_JSON;

    public static void populate() {
        registerJavaBlocks();
        registerBedrockBlocks();
        BLOCKS_JSON = null;
    }

    private static NbtMap putWithValue(Object value) {
        NbtMapBuilder builder = NbtMap.builder();
        builder.put("value", value);

        return builder.build();
    }

    private static void registerBedrockBlocks() {
        int counter = 0;
        for (Map.Entry<ObjectIntPair<String>, BiFunction<String, NbtMapBuilder, String>> palette : BLOCK_MAPPERS.entrySet()) {
            InputStream stream = FileUtils.getResource(String.format("bedrock/block_palette.%s.nbt", palette.getKey().key()));
            NbtList<NbtMap> blocksTag;
            try (NBTInputStream nbtInputStream = new NBTInputStream(new DataInputStream(new GZIPInputStream(stream)), true, true)) {
                NbtMap blockPalette = (NbtMap) nbtInputStream.readTag();
                blocksTag = (NbtList<NbtMap>) blockPalette.getList("blocks", NbtType.COMPOUND);
            } catch (Exception e) {
                throw new AssertionError("Unable to get blocks from runtime block states", e);
            }
            Map<String, NbtMap> javaIdentifierToBedrockTag = new Object2ObjectOpenHashMap<>(blocksTag.size());
            // New since 1.16.100 - find the block runtime ID by the order given to us in the block palette,
            // as we no longer send a block palette
            Object2IntMap<NbtMap> blockStateOrderedMap = new Object2IntOpenHashMap<>(blocksTag.size());

            int stateVersion = -1;

            int airRuntimeId = -1;
            int commandBlockRuntimeId = -1;
            int javaRuntimeId = -1;
            int waterRuntimeId = -1;
            int movingBlockRuntimeId = -1;
            Iterator<Map.Entry<String, JsonNode>> blocksIterator = BLOCKS_JSON.fields();

            BiFunction<String, NbtMapBuilder, String> stateMapper = BLOCK_MAPPERS.getOrDefault(palette.getKey(), EMPTY_MAPPER);
            for (int i = 0; i < blocksTag.size(); i++) {
                NbtMap tag = blocksTag.get(i);
                if (blockStateOrderedMap.containsKey(tag)) {
                    throw new AssertionError("Duplicate block states in Bedrock palette: " + tag);
                }
                blockStateOrderedMap.put(tag, i);
                if (stateVersion == -1) {
                    stateVersion = tag.getInt("version");
                }
            }
            int[] javaToBedrockBlocks = new int[BLOCKS_JSON.size()];

            Map<String, NbtMap> flowerPotBlocks = new Object2ObjectOpenHashMap<>();
            Object2IntMap<NbtMap> itemFrames = new Object2IntOpenHashMap<>();

            IntSet jigsawStateIds = new IntOpenHashSet();

            BlockMappings.BlockMappingsBuilder builder = BlockMappings.builder();
            while (blocksIterator.hasNext()) {
                javaRuntimeId++;
                Map.Entry<String, JsonNode> entry = blocksIterator.next();
                String javaId = entry.getKey();

                int bedrockRuntimeId = blockStateOrderedMap.getOrDefault(buildBedrockState(entry.getValue(), stateVersion, stateMapper), -1);
                if (bedrockRuntimeId == -1) {
                    throw new RuntimeException("Unable to find " + javaId + " Bedrock runtime ID! Built NBT tag: \n" +
                            buildBedrockState(entry.getValue(), stateVersion, stateMapper));
                }
                switch (javaId) {
                    case "minecraft:air" -> airRuntimeId = bedrockRuntimeId;
                    case "minecraft:water[level=0]" -> waterRuntimeId = bedrockRuntimeId;
                    case "minecraft:command_block[conditional=false,facing=north]" -> commandBlockRuntimeId = bedrockRuntimeId;
                    case "minecraft:moving_piston[facing=north,type=normal]" -> movingBlockRuntimeId = bedrockRuntimeId;
                }

                if (javaId.contains("jigsaw")) {
                    jigsawStateIds.add(bedrockRuntimeId);
                }

                boolean waterlogged = entry.getKey().contains("waterlogged=true")
                        || javaId.contains("minecraft:bubble_column") || javaId.contains("minecraft:kelp") || javaId.contains("seagrass");

                if (waterlogged) {
                    int finalJavaRuntimeId = javaRuntimeId;
                    BlockRegistries.WATERLOGGED.register(set -> set.add(finalJavaRuntimeId));
                }

                String cleanJavaIdentifier = BlockUtils.getCleanIdentifier(entry.getKey());

                // Get the tag needed for non-empty flower pots
                if (entry.getValue().get("pottable") != null) {
                    flowerPotBlocks.put(cleanJavaIdentifier.intern(), blocksTag.get(bedrockRuntimeId));
                }
                boolean hasNotRegistered = true;
                for (JsonNode blockState : blockStatesNode) {
                    if (javaId.contains("note_block")) {
                        if (blockState.has("when")) {
                            JsonNode when = blockState.get("when");
                            if (when.has("instrument") && when.has("note") && when.has("powered")) {
                                String instrument = when.get("instrument").asText();
                                int note = when.get("note").asInt();
                                boolean powered = when.get("powered").asBoolean();
                                if (javaId.contains("minecraft:note_block[instrument=" + instrument + ",note=" + note + ",powered=" + powered + "]")) {
                                    if (blockState.has("apply")) {
                                        //javaRuntimeId++;
                                        String model = blockState.get("apply").get("model").asText();
                                        NbtMap bedrockRuntimeId001 = buildCustomBedrockState("geysermc:zzz_" + model, entry.getValue(), stateVersion, stateMapper);
                                        blockStateOrderedMap.put(bedrockRuntimeId001,blockStateOrderedMap.size()+1);
                                        //String[] splitStr = java.intern().split(":");
                                        //very very very very very very very very very very very very very very very very very very very very very very very very very very very very hacky method, do not use in production
                                        //NbtMapBuilder nbt = blockStates.get(blockState.get("arrayIndex").asInt());
                                        //javaIdentifierToBedrockTag.put(cleanJavaIdentifier, bedrockRuntimeId001);
                                        NbtMap nbt = NbtMap.builder().putCompound("components", NbtMap.builder().putCompound("minecraft:block_light_absorption", putWithValue(0)).putCompound("minecraft:entity_collision", NbtMap.builder().putBoolean("enabled", true).putList("origin", NbtType.FLOAT, Arrays.asList(0f, 0f, 0f)).putList("size", NbtType.FLOAT, Arrays.asList(16f, 16f, 16f)).build())
                                                .putCompound("minecraft:unit_cube", NbtMap.EMPTY).putCompound("minecraft:material_instances", NbtMap.builder().putCompound("mappings", NbtMap.EMPTY).putCompound("materials", NbtMap.builder().putCompound("*", NbtMap.builder().putBoolean("ambient_occlusion", true).putBoolean("face_dimming", true).putString("texture", "zzz_" + model).putString("render_method", "opaque").build()).build()).build()).build()).build();
                                        customBlocks.put(("geysermc:zzz_" + model).intern(), nbt);
                                        javaToBedrockBlocks[javaRuntimeId] = blockStateOrderedMap.getOrDefault(bedrockRuntimeId001, -1);
                                        //System.out.println(bedrockRuntimeId001+"    |      "+blocksTag.get(bedrockRuntimeId));
                                        hasNotRegistered = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (hasNotRegistered) {
                    if (!cleanJavaIdentifier.equals(entry.getValue().get("bedrock_identifier").asText())) {
                        javaIdentifierToBedrockTag.put(cleanJavaIdentifier.intern(), blocksTag.get(bedrockRuntimeId));
                    }
                    javaToBedrockBlocks[javaRuntimeId] = bedrockRuntimeId;
                }
            }

            if (commandBlockRuntimeId == -1) {
                throw new AssertionError("Unable to find command block in palette");
            }
            builder.commandBlockRuntimeId(commandBlockRuntimeId);

            if (waterRuntimeId == -1) {
                throw new AssertionError("Unable to find water in palette");
            }
            builder.bedrockWaterId(waterRuntimeId);

            if (airRuntimeId == -1) {
                throw new AssertionError("Unable to find air in palette");
            }
            builder.bedrockAirId(airRuntimeId);

            if (movingBlockRuntimeId == -1) {
                throw new AssertionError("Unable to find moving block in palette");
            }

            builder.bedrockMovingBlockId(movingBlockRuntimeId);

            // Loop around again to find all item frame runtime IDs
            for (Object2IntMap.Entry<NbtMap> entry : blockStateOrderedMap.object2IntEntrySet()) {
                String name = entry.getKey().getString("name");
                if (name.equals("minecraft:frame") || name.equals("minecraft:glow_frame")) {
                    itemFrames.put(entry.getKey(), entry.getIntValue());
                }
            }
            builder.bedrockBlockStates(blocksTag);
            BlockRegistries.BLOCKS.register(palette.getKey().valueInt(), builder.blockStateVersion(stateVersion)
                    .emptyChunkSection(new ChunkSection(new BlockStorage[]{new BlockStorage(airRuntimeId)}))
                    .javaToBedrockBlocks(javaToBedrockBlocks)
                    .javaIdentifierToBedrockTag(javaIdentifierToBedrockTag)
                    .itemFrames(itemFrames)
                    .flowerPotBlocks(flowerPotBlocks)
                    .jigsawStateIds(jigsawStateIds)
                    .build());
        }
    }

    private static void registerJavaBlocks() {
        InputStream stream = FileUtils.getResource("mappings/blocks.json");

        JsonNode blocksJson;
        try {
            blocksJson = GeyserConnector.JSON_MAPPER.readTree(stream);
        } catch (Exception e) {
            throw new AssertionError("Unable to load Java block mappings", e);
        }

        BlockRegistries.JAVA_BLOCKS.set(new BlockMapping[blocksJson.size()]); // Set array size to number of blockstates

        Set<String> cleanIdentifiers = new HashSet<>();

        int javaRuntimeId = -1;
        int bellBlockId = -1;
        int cobwebBlockId = -1;
        int furnaceRuntimeId = -1;
        int furnaceLitRuntimeId = -1;
        int honeyBlockRuntimeId = -1;
        int slimeBlockRuntimeId = -1;
        int spawnerRuntimeId = -1;
        int uniqueJavaId = -1;
        int waterRuntimeId = -1;
        Iterator<Map.Entry<String, JsonNode>> blocksIterator = blocksJson.fields();
        while (blocksIterator.hasNext()) {
            javaRuntimeId++;
            Map.Entry<String, JsonNode> entry = blocksIterator.next();
            String javaId = entry.getKey();

            // TODO fix this, (no block should have a null hardness)
            BlockMapping.BlockMappingBuilder builder = BlockMapping.builder();
            JsonNode hardnessNode = entry.getValue().get("block_hardness");
            if (hardnessNode != null) {
                builder.hardness(hardnessNode.doubleValue());
            }

            JsonNode canBreakWithHandNode = entry.getValue().get("can_break_with_hand");
            if (canBreakWithHandNode != null) {
                builder.canBreakWithHand(canBreakWithHandNode.booleanValue());
            } else {
                builder.canBreakWithHand(false);
            }

            JsonNode collisionIndexNode = entry.getValue().get("collision_index");
            if (hardnessNode != null) {
                builder.collisionIndex(collisionIndexNode.intValue());
            }

            JsonNode pickItemNode = entry.getValue().get("pick_item");
            if (pickItemNode != null) {
                builder.pickItem(pickItemNode.textValue().intern());
            }

            if (javaId.equals("minecraft:obsidian") || javaId.equals("minecraft:crying_obsidian") || javaId.startsWith("minecraft:respawn_anchor")) {
                builder.pistonBehavior(PistonBehavior.BLOCK);
            } else {
                JsonNode pistonBehaviorNode = entry.getValue().get("piston_behavior");
                if (pistonBehaviorNode != null) {
                    builder.pistonBehavior(PistonBehavior.getByName(pistonBehaviorNode.textValue()));
                } else {
                    builder.pistonBehavior(PistonBehavior.NORMAL);
                }
            }

            JsonNode hasBlockEntityNode = entry.getValue().get("has_block_entity");
            if (hasBlockEntityNode != null) {
                builder.isBlockEntity(hasBlockEntityNode.booleanValue());
            } else {
                builder.isBlockEntity(false);
            }

            BlockStateValues.storeBlockStateValues(entry.getKey(), javaRuntimeId, entry.getValue());

            String cleanJavaIdentifier = BlockUtils.getCleanIdentifier(entry.getKey());
            String bedrockIdentifier = entry.getValue().get("bedrock_identifier").asText();

            if (!cleanIdentifiers.contains(cleanJavaIdentifier)) {
                uniqueJavaId++;
                cleanIdentifiers.add(cleanJavaIdentifier.intern());
            }

            builder.javaIdentifier(javaId);
            builder.javaBlockId(uniqueJavaId);

            BlockRegistries.JAVA_IDENTIFIERS.register(javaId, javaRuntimeId);
            BlockRegistries.JAVA_BLOCKS.register(javaRuntimeId, builder.build());

            // Keeping this here since this is currently unchanged between versions
            // It's possible to only have this store differences in names, but the key set of all Java names is used in sending command suggestions

            boolean hasNotRegistered = true;
            for (JsonNode blockState : blockStatesNode) {
                if (javaId.contains("note_block")) {
                    if (blockState.has("when")) {
                        JsonNode when = blockState.get("when");
                        if (when.has("instrument") && when.has("note") && when.has("powered")) {
                            String instrument = when.get("instrument").asText();
                            int note = when.get("note").asInt();
                            boolean powered = when.get("powered").asBoolean();
                            if (javaId.contains("minecraft:note_block[instrument=" + instrument + ",note=" + note + ",powered=" + powered + "]")) {
                                if (blockState.has("apply")) {
                                    String model = blockState.get("apply").get("model").asText();
                                    //int bedrockRuntimeId001 = blockStateOrderedMap.getOrDefault(buildCustomBedrockState("geysermc:zzz_"+model,entry.getValue(), stateVersion, stateMapper), -1);
                                    //very very very very very very very very very very very very very very very very very very very very very very very very very very very very hacky method, do not use in production
                                    //NbtMapBuilder nbt = blockStates.get(blockState.get("arrayIndex").asInt());
                                    BlockRegistries.JAVA_TO_BEDROCK_IDENTIFIERS.register(cleanJavaIdentifier.intern(), ("geysermc:zzz_" + note + "_" + instrument + "_" + powered).intern());
                                    hasNotRegistered = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (hasNotRegistered) BlockRegistries.JAVA_TO_BEDROCK_IDENTIFIERS.register(cleanJavaIdentifier.intern(), bedrockIdentifier.intern());
            if (javaId.startsWith("minecraft:bell[")) {
                bellBlockId = uniqueJavaId;

            } else if (javaId.contains("cobweb")) {
                cobwebBlockId = uniqueJavaId;

            } else if (javaId.startsWith("minecraft:furnace[facing=north")) {
                if (javaId.contains("lit=true")) {
                    furnaceLitRuntimeId = javaRuntimeId;
                } else {
                    furnaceRuntimeId = javaRuntimeId;
                }

            } else if (javaId.startsWith("minecraft:spawner")) {
                spawnerRuntimeId = javaRuntimeId;

            } else if ("minecraft:water[level=0]".equals(javaId)) {
                waterRuntimeId = javaRuntimeId;
            } else if (javaId.equals("minecraft:honey_block")) {
                honeyBlockRuntimeId = javaRuntimeId;
            } else if (javaId.equals("minecraft:slime_block")) {
                slimeBlockRuntimeId = javaRuntimeId;
            }
        }
        if (bellBlockId == -1) {
            throw new AssertionError("Unable to find bell in palette");
        }
        BlockStateValues.JAVA_BELL_ID = bellBlockId;

        if (cobwebBlockId == -1) {
            throw new AssertionError("Unable to find cobwebs in palette");
        }
        BlockStateValues.JAVA_COBWEB_ID = cobwebBlockId;

        if (furnaceRuntimeId == -1) {
            throw new AssertionError("Unable to find furnace in palette");
        }
        BlockStateValues.JAVA_FURNACE_ID = furnaceRuntimeId;

        if (furnaceLitRuntimeId == -1) {
            throw new AssertionError("Unable to find lit furnace in palette");
        }
        BlockStateValues.JAVA_FURNACE_LIT_ID = furnaceLitRuntimeId;

        if (honeyBlockRuntimeId == -1) {
            throw new AssertionError("Unable to find honey block in palette");
        }
        BlockStateValues.JAVA_HONEY_BLOCK_ID = honeyBlockRuntimeId;

        if (slimeBlockRuntimeId == -1) {
            throw new AssertionError("Unable to find slime block in palette");
        }
        BlockStateValues.JAVA_SLIME_BLOCK_ID = slimeBlockRuntimeId;

        if (spawnerRuntimeId == -1) {
            throw new AssertionError("Unable to find spawner in palette");
        }
        BlockStateValues.JAVA_SPAWNER_ID = spawnerRuntimeId;

        if (waterRuntimeId == -1) {
            throw new AssertionError("Unable to find Java water in palette");
        }
        BlockStateValues.JAVA_WATER_ID = waterRuntimeId;

        BLOCKS_JSON = blocksJson;
    }

    private static NbtMap buildBedrockState(JsonNode node, int blockStateVersion, BiFunction<String, NbtMapBuilder, String> statesMapper) {
        NbtMapBuilder tagBuilder = NbtMap.builder();
        String bedrockIdentifier = node.get("bedrock_identifier").textValue();
        tagBuilder.putString("name", bedrockIdentifier)
                .putInt("version", blockStateVersion);

        NbtMapBuilder statesBuilder = NbtMap.builder();

        // check for states
        if (node.has("bedrock_states")) {
            Iterator<Map.Entry<String, JsonNode>> statesIterator = node.get("bedrock_states").fields();

            while (statesIterator.hasNext()) {
                Map.Entry<String, JsonNode> stateEntry = statesIterator.next();
                JsonNode stateValue = stateEntry.getValue();
                switch (stateValue.getNodeType()) {
                    case BOOLEAN -> statesBuilder.putBoolean(stateEntry.getKey(), stateValue.booleanValue());
                    case STRING -> statesBuilder.putString(stateEntry.getKey(), stateValue.textValue());
                    case NUMBER -> statesBuilder.putInt(stateEntry.getKey(), stateValue.intValue());
                }
            }
        }
        String newIdentifier = statesMapper.apply(bedrockIdentifier, statesBuilder);
        if (newIdentifier != null) {
            tagBuilder.putString("name", newIdentifier);
        }
        tagBuilder.put("states", statesBuilder.build());
        return tagBuilder.build();
    }

    private static NbtMap buildCustomBedrockState(String name1, JsonNode node, int blockStateVersion, BiFunction<String, NbtMapBuilder, String> statesMapper) {
        NbtMapBuilder tagBuilder = NbtMap.builder();
        String bedrockIdentifier = node.get("bedrock_identifier").textValue();
        tagBuilder.putString("name", name1)
                .putInt("version", blockStateVersion);

        NbtMapBuilder statesBuilder = NbtMap.builder();

        // check for states
        if (node.has("bedrock_states")) {
            Iterator<Map.Entry<String, JsonNode>> statesIterator = node.get("bedrock_states").fields();

            while (statesIterator.hasNext()) {
                Map.Entry<String, JsonNode> stateEntry = statesIterator.next();
                JsonNode stateValue = stateEntry.getValue();
                switch (stateValue.getNodeType()) {
                    case BOOLEAN -> statesBuilder.putBoolean(stateEntry.getKey(), stateValue.booleanValue());
                    case STRING -> statesBuilder.putString(stateEntry.getKey(), stateValue.textValue());
                    case NUMBER -> statesBuilder.putInt(stateEntry.getKey(), stateValue.intValue());
                }
            }
        }
        String newIdentifier = statesMapper.apply(name1, statesBuilder);
        if (newIdentifier != null) {
            tagBuilder.putString("name", name1);
        }
        tagBuilder.put("states", statesBuilder.build());
        /*tagBuilder.putCompound("components",NbtMap.builder().putCompound("minecraft:material_instances", NbtMap.builder().putCompound("materials", NbtMap.builder().putCompound("*", NbtMap.builder().putBoolean("ambient_occlusion", true).putBoolean("face_dimming", true).putString("texture", "zzz_"+name1).putString("render_method", "opaque").build()).build()).build())
                .putCompound("minecraft:entity_collision", NbtMap.builder().putBoolean("enabled", true).putList("origin", NbtType.FLOAT, Arrays.asList(0f, 0f, 0f)).putList("size", NbtType.FLOAT, Arrays.asList(16f, 16f, 16f)).build())
                .putCompound("minecraft:unit_cube", NbtMap.EMPTY)
                .putInt("minecraft:block_light_absorption", 0)
                .putString("frame", "0.000000")
                .putInt("frame_version", 1)
                .putString("legacy_id", "").build()).build();*/
        return tagBuilder.build();
    }
}
