package com.example.examplemod.i18n.demo03;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.i18n.demo03.registry.ModBlocks;
import com.example.examplemod.i18n.demo03.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class Demo03ExampleLanguageProvider extends LanguageProvider {

    public Demo03ExampleLanguageProvider(PackOutput output) {
        super(output, ExampleMod.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // 添加通用翻译
        this.add("message.examplemod.welcome", "Welcome to the Example Mod!");

        // 添加物品翻译
        this.addItem(ModItems.EXAMPLE_ITEM, "Example Item");

        // 添加方块翻译
        this.addBlock(ModBlocks.EXAMPLE_BLOCK, "Example Block");
    }
}
