package com.example.examplemod.objdemo;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.objdemo.block.ObjBlock;
import com.example.examplemod.objdemo.blockentity.ObjBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, ExampleMod.MODID);


    public static final DeferredHolder<Block,ObjBlock> OBJ_BLOCK =
            BLOCKS.register("obj_block",
                    registryName -> new ObjBlock(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK,registryName))));
}
