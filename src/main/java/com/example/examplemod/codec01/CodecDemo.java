package com.example.examplemod.codec01;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


public class CodecDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger("CodeDemo01");

    // 1. 基本Codec操作
    public static void basicCodecOperations() {
        LOGGER.info("=== 基本Codec操作 ===");

        // 基本类型Codec
        Codec<Integer> intCodec = Codec.INT;
        Codec<Boolean> boolCodec = Codec.BOOL;
        Codec<String> stringCodec = Codec.STRING;

        // 编码示例
        JsonOps jsonOps = JsonOps.INSTANCE;

        // 编码整数
        DataResult<JsonElement> encodedInt = intCodec.encodeStart(jsonOps, 42);
        LOGGER.info("编码整数42: {}", encodedInt.result().get());

        // 编码布尔值
        DataResult<JsonElement> encodedBool = boolCodec.encodeStart(jsonOps, true);
        LOGGER.info("编码布尔值true: {}", encodedBool.result().get());

        // 解码示例
        JsonElement intJson = com.mojang.serialization.JsonOps.INSTANCE.createInt(100);
        DataResult<Integer> decodedInt = intCodec.parse(jsonOps, intJson);
        LOGGER.info("解码整数100: {}", decodedInt.result().get());
    }

    // 2. 记录Codec (Record Codec)
    public static void recordCodecExample() {
        LOGGER.info("=== 记录Codec示例 ===");

        // 定义一个记录类
        record Person(String name, int age, boolean isStudent) {}

        // 创建记录的Codec
        Codec<Person> personCodec = RecordCodecBuilder.create(
                person -> person.group(
                        Codec.STRING.fieldOf("name").forGetter(Person::name),
                        Codec.INT.fieldOf("age").forGetter(Person::age),
                        Codec.BOOL.fieldOf("isStudent").forGetter(Person::isStudent)
                ).apply(person, Person::new)
        );

        // 编码示例
        Person alice = new Person("Alice", 20, true);
        DataResult<JsonElement> encodedPerson = personCodec.encodeStart(JsonOps.INSTANCE, alice);
        LOGGER.info("编码Person对象: {}", encodedPerson.result().get());

        // 解码示例
        Person bob = new Person("Bob", 25, false);
        JsonElement personJson = personCodec.encodeStart(JsonOps.INSTANCE, bob)
                .result()
                .get();
        System.out.println("编码Person对象: " + personJson);


        DataResult<Person> decodedPerson = personCodec.parse(JsonOps.INSTANCE, personJson);
        LOGGER.info("解码Person对象: {}", decodedPerson.result().get());
    }

    // 3. 变换Codec (Transforming Codecs)
    public static void transformingCodecs() {
        LOGGER.info("=== 变换Codec示例 ===");

        // 定义两个等效的类
        record ClassA(String value) {}
        record ClassB(String value) {}

        // 创建ClassA的Codec
        Codec<ClassA> aCodec = Codec.STRING.xmap(ClassA::new, ClassA::value);

        // 使用xmap创建ClassB的Codec
        Codec<ClassB> bCodec = aCodec.xmap(
                a -> new ClassB(a.value()), // A -> B
                b -> new ClassA(b.value())  // B -> A
        );

        // 编码ClassB
        ClassB bob = new ClassB("Bob");
        DataResult<JsonElement> encodedB = bCodec.encodeStart(JsonOps.INSTANCE, bob);
        LOGGER.info("编码ClassB: {}", encodedB.result().get());

        // 解码为ClassA
        DataResult<ClassA> decodedA = aCodec.parse(JsonOps.INSTANCE, encodedB.result().get());
        LOGGER.info("解码为ClassA: {}", decodedA.result().get());
    }

    // 4. 范围Codec (Range Codec)
    public static void rangeCodecExample() {
        LOGGER.info("=== 范围Codec示例 ===");

        // 创建0-100的整数范围Codec
        Codec<Integer> rangeCodec = Codec.intRange(0, 100);

        // 在范围内的值
        DataResult<Integer> validNumber = rangeCodec.parse(JsonOps.INSTANCE, com.mojang.serialization.JsonOps.INSTANCE.createInt(50));
        LOGGER.info("有效数字50: {}", validNumber.result().get());

        // 超出范围的值
        DataResult<Integer> invalidNumber = rangeCodec.parse(JsonOps.INSTANCE, com.mojang.serialization.JsonOps.INSTANCE.createInt(150));
        String errorMessage = invalidNumber.error()
                .map(DataResult.Error::message)
                .orElse("无错误");

        LOGGER.info("无效数字150: {}", errorMessage);

        // 组合处理
        invalidNumber.resultOrPartial(
                error -> LOGGER.error("转换失败: {}", error)  // ✅ 直接使用 message() 方法
        );
    }

    // 5. 列表和映射Codec
    public static void listAndMapCodecs() {
        LOGGER.info("=== 列表和映射Codec示例 ===");

        // BlockPos的Codec
        Codec<Vector3i> vectorCodec = Codec.INT.listOf()
                .xmap(
                        list -> new Vector3i(list.get(0), list.get(1), list.get(2)),
                        vector -> List.of(vector.x(), vector.y(), vector.z())
                );

        // 编码列表
        Vector3i position = new Vector3i(10, 20, 30);
        DataResult<JsonElement> encodedList = vectorCodec.encodeStart(JsonOps.INSTANCE, position);
        LOGGER.info("编码位置: {}", encodedList.result().get());

        // 解码列表
        DataResult<Vector3i> decodedPosition = vectorCodec.parse(JsonOps.INSTANCE, encodedList.result().get());
        LOGGER.info("解码位置: {}", decodedPosition.result().get());

        // 创建映射Codec
        Codec<Map<String, Integer>> mapCodec = Codec.unboundedMap(Codec.STRING, Codec.INT);

        // 编码映射
        Map<String, Integer> scores = Map.of("Alice", 90, "Bob", 85, "Charlie", 95);
        DataResult<JsonElement> encodedMap = mapCodec.encodeStart(JsonOps.INSTANCE, scores);
        LOGGER.info("编码分数映射: {}", encodedMap.result().get());
    }


    // 7. 注册表Codec (Registry Codec)
    public static void registryCodecExample() {
        LOGGER.info("=== 注册表Codec示例 ===");

        // 获取方块注册表的Codec
        Codec<Block> blockCodec = BuiltInRegistries.BLOCK.byNameCodec();

        // 编码方块
        DataResult<JsonElement> encodedBlock = blockCodec.encodeStart(JsonOps.INSTANCE, Blocks.DIRT);
        LOGGER.info("编码DIRT方块: {}", encodedBlock.result().get());

        // 解码方块
        DataResult<Block> decodedBlock = blockCodec.parse(JsonOps.INSTANCE, encodedBlock.result().get());
        LOGGER.info("解码方块: {}", BuiltInRegistries.BLOCK.getKey(decodedBlock.result().get()));

        // 获取物品注册表的Codec
        Codec<Item> itemCodec = BuiltInRegistries.ITEM.byNameCodec();

        // 编码物品
        DataResult<JsonElement> encodedItem = itemCodec.encodeStart(JsonOps.INSTANCE, Items.DIAMOND);
        LOGGER.info("编码DIAMOND物品: {}", encodedItem.result().get());

        // 解码物品
        DataResult<Item> decodedItem = itemCodec.parse(JsonOps.INSTANCE, encodedItem.result().get());
        LOGGER.info("解码物品: {}", BuiltInRegistries.ITEM.getKey(decodedItem.result().get()));
    }


    // 8. 完整示例：自定义配置
    public static void customConfigExample() {
        LOGGER.info("=== 自定义配置示例 ===");

        // 定义配置记录
        record Config(int maxPlayers, boolean pvpEnabled, List<String> allowedItems, Either<String, Integer> specialSetting) {}

        // 创建配置的Codec
        Codec<Config> configCodec = RecordCodecBuilder.create(
                record -> record.group(
                        Codec.INT.fieldOf("max_players").forGetter(Config::maxPlayers),
                        Codec.BOOL.fieldOf("pvp_enabled").forGetter(Config::pvpEnabled),
                        Codec.STRING.listOf().fieldOf("allowed_items").forGetter(Config::allowedItems),
                        Codec.either(Codec.STRING, Codec.INT).fieldOf("special_setting").forGetter(Config::specialSetting)
                ).apply(record, Config::new)
        );

        // 编码示例配置
        Config exampleConfig = new Config(10, true, List.of("minecraft:diamond", "minecraft:iron_sword"), Either.right(42));
        DataResult<JsonElement> encodedConfig = configCodec.encodeStart(JsonOps.INSTANCE, exampleConfig);
        LOGGER.info("编码配置: {}", encodedConfig.result().get());

        // 解码配置
        DataResult<Config> decodedConfig = configCodec.parse(JsonOps.INSTANCE, encodedConfig.result().get());
        LOGGER.info("解码配置: {}", decodedConfig.result().get());

        // 处理解码结果
        decodedConfig.resultOrPartial(
                error -> LOGGER.error("配置解析失败: {}", error)
        ).ifPresent(c->{
            LOGGER.info("{}",c);
        });
    }


    public static void run() {
        // 运行所有示例
        basicCodecOperations();
        recordCodecExample();
        transformingCodecs();
        rangeCodecExample();
        listAndMapCodecs();
        registryCodecExample();
//        tagCodecExample();
        customConfigExample();
    }
}
