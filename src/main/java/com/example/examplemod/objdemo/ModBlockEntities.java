package com.example.examplemod.objdemo;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.objdemo.block.ObjBlock;
import com.example.examplemod.objdemo.blockentity.ObjBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ExampleMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ObjBlockEntity>> OBJ_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("obj_block_entity",
                    ()->new BlockEntityType<ObjBlockEntity>(
                            ObjBlockEntity::new,
                            ModBlocks.OBJ_BLOCK.get()
                    ));
}