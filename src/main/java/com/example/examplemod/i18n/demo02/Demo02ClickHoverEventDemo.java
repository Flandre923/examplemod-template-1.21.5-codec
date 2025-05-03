package com.example.examplemod.i18n.demo02;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.net.URI;
import java.net.URISyntaxException;

@EventBusSubscriber
public class Demo02ClickHoverEventDemo {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        if (player == null) return;

        // 基础文本
        MutableComponent baseText = Component.literal("点击以下文本测试 ClickEvent:\n");

        try {
            // OPEN_URL
            baseText.append(Component.literal("▶ 打开网页 (OPEN_URL)\n")
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(new URI("https://www.example.com")))));

            // OPEN_FILE（仅限单人游戏）
            baseText.append(Component.literal("▶ 打开文件 (OPEN_FILE)\n")
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.OpenFile("C:/example.txt"))));

            // RUN_COMMAND
            baseText.append(Component.literal("▶ 执行命令 (RUN_COMMAND)\n")
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.RunCommand("/gamerule sendCommandFeedback false"))));

            // SUGGEST_COMMAND
            baseText.append(Component.literal("▶ 建议命令 (SUGGEST_COMMAND)\n")
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand("/give @s dirt 64"))));

            // CHANGE_PAGE
            baseText.append(Component.literal("▶ 改变书页 (CHANGE_PAGE)\n")
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.ChangePage(2))));

            // COPY_TO_CLIPBOARD
            baseText.append(Component.literal("▶ 复制文本 (COPY_TO_CLIPBOARD)\n")
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard("Hello World!"))));
        } catch (URISyntaxException e) {
            player.displayClientMessage(Component.literal("发生错误：URI格式不正确"),false);
            e.printStackTrace();
        }

        // 显示提示信息
        player.displayClientMessage(baseText,false);
    }
}
