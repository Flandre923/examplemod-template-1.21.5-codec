package com.example.examplemod.objdemo;

import com.example.examplemod.ExampleMod;
import com.jcraft.jorbis.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static DeferredRegister.Items MODITEMS = DeferredRegister.createItems(ExampleMod.MODID);


    public static DeferredHolder<Item,Item> OBJ_BLOCK_ITEM = MODITEMS.registerItem(
            "obj_block",
            (properties) -> new BlockItem(ModBlocks.OBJ_BLOCK.get(),properties),
            new Item.Properties()
    );


}
