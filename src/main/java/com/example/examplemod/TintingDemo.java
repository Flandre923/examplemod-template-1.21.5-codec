package com.example.examplemod;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber
public class TintingDemo {

    // 注册颜色处理器（客户端事件）
    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex == 0 && level != null && pos != null) {
                // 根据生物群系返回颜色（示例：绿色）
                return 0xFF00FF00; // ARGB 格式
            }   
            return 0xFFFFFFFF; // 默认白色
        }, ModBlockRegistries.EXAMPLE_BLOCK.get());
    }
}
