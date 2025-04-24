package com.example.examplemod.codec01;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.*;

public class FormatConversionDemo {

    public static void formatConversionDemo(){
        // --- Part 1: Convert NBT to JSON ---
        System.out.println("--- 1. NBT to JSON Conversion ---");

        // a) Create some sample NBT data (CompoundTag)
        CompoundTag originalNbt = new CompoundTag();
        originalNbt.putString("name", "Player1");
        originalNbt.putInt("level", 15);
        originalNbt.putBoolean("isActive", true); // putBoolean actually uses ByteTag (0 or 1)

        ListTag itemsNbt = new ListTag();
        itemsNbt.add(StringTag.valueOf("sword"));
        itemsNbt.add(StringTag.valueOf("shield"));
        originalNbt.put("items", itemsNbt);

        System.out.println("Original NBT: " + originalNbt);
        // Expected approx: {name: "Player1", level: 15, isActive: 1b, items: ["sword", "shield"]}

        // b) Get the NbtOps instance
        DynamicOps<Tag> nbtOps = NbtOps.INSTANCE;

        // c) Get the JsonOps instance
        DynamicOps<JsonElement> jsonOps = JsonOps.INSTANCE; // Or JsonOps.COMPRESSED
        JsonElement convertedJson = nbtOps.convertTo(jsonOps, originalNbt);
        System.out.println("Converted JSON: " + convertedJson);
        // Expected: {"name":"Player1","level":15,"isActive":true,"items":["sword","shield"]}


        System.out.println("\n--- 2. JSON to NBT Conversion ---");
        JsonObject originalJson = new JsonObject();
        originalJson.addProperty("name", "Structure Alpha");
        originalJson.addProperty("x", 100.5); // Use a double
        originalJson.addProperty("generated", false);

        JsonArray blocksJson = new JsonArray();
        blocksJson.add("minecraft:stone");
        blocksJson.add("minecraft:dirt");
        originalJson.add("blocks", blocksJson);

        System.out.println("Original JSON: " + originalJson);
        // Expected: {"name":"Structure Alpha","x":100.5,"generated":false,"blocks":["minecraft:stone","minecraft:dirt"]}

        // b) JsonOps and NbtOps instances are already available from Part 1

        // c) Perform the conversion using convertTo
        //    Input Ops: jsonOps (because original data is JSON)
        //    Output Ops: nbtOps (because we want NBT)
        //    Input Data: originalJson
        Tag convertedNbt = jsonOps.convertTo(nbtOps, originalJson);

        System.out.println("Converted NBT: " + convertedNbt);
        // Expected approx: {name: "Structure Alpha", x: 100.5d, generated: 0b, blocks: ["minecraft:stone", "minecraft:dirt"]}
        // Note: JSON double becomes NBT DoubleTag, JSON false becomes NBT ByteTag(0b).
    }
}
