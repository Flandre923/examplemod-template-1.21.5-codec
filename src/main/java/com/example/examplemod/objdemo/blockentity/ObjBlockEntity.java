package com.example.examplemod.objdemo.blockentity;

import com.example.examplemod.objdemo.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ObjBlockEntity extends BlockEntity {
    public static final String COUNT_NUMBER_TAG = "CountNumber";
    private int countNumber = 0;

    public ObjBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.OBJ_BLOCK_ENTITY.get(), pos, blockState);
        countNumber = 0;
    }

    public void setCountNumber(int countNumber){
        this.countNumber = countNumber;
        this.setChanged(); // 标记数据已更改需要保存
    }
    public int getCountNumber() {
        return countNumber;
    }

    public void addCounterNumber() {
        this.setCountNumber(this.countNumber + 1);
    }

    public void subCounterNumber() {
        this.setCountNumber(this.countNumber - 1);
    }


    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(COUNT_NUMBER_TAG, countNumber);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        countNumber = tag.getInt(COUNT_NUMBER_TAG).orElse(0);
    }
}
