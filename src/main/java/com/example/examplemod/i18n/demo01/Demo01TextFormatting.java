package com.example.examplemod.i18n.demo01;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class Demo01TextFormatting {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // 创建基础文本
        MutableComponent text = Component.literal("Hello World!");

        // 创建样式
        Style blue = Style.EMPTY.withColor(0x0000FF); // 蓝色
        Style blueItalic = Style.EMPTY.withColor(0x0000FF).withItalic(true); // 蓝色斜体
        Style bold = Style.EMPTY.withBold(true); // 加粗
        Style strikethrough = Style.EMPTY.withStrikethrough(true); // 删除线

        // 合并样式（最终为蓝色+斜体+加粗+删除线）
        Style merged = blueItalic.applyTo(bold).applyTo(strikethrough);

        // 应用合并样式
        text.setStyle(merged);

        // 添加红色样式（注意：withStyle 返回新实例，需要重新赋值）
        text = text.withStyle(Style.EMPTY.withColor(0xFF0000)); // 红色

        // 发送带样式的文本给玩家
        if (event.getEntity() instanceof Player player) {
            player.displayClientMessage(text,false);
        }
    }
}
