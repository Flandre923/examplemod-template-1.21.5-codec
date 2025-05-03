package com.example.examplemod.objdemo.block;

import com.example.examplemod.objdemo.blockentity.ObjBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ObjBlock extends BaseEntityBlock {
    public static final MapCodec<ObjBlock> CODEC = simpleCodec(ObjBlock::new);

    public ObjBlock(Properties p_49224_) {
        super(p_49224_);
    }


    public void showCountNumber(){

    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ObjBlockEntity objBlockEntity) {
                objBlockEntity.addCounterNumber();
                player.displayClientMessage(Component.literal("当前数值: " + objBlockEntity.getCountNumber()),false);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ObjBlockEntity(blockPos, blockState);
    }
}
