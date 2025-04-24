package com.example.examplemod.codec01;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

abstract class ExampleObject{
    public abstract String getTypeIdentifier();
}


// Subclass storing only a string
class StringObject extends ExampleObject {
    private final String data;

    public StringObject(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String getTypeIdentifier() {
        return "simple_string"; // Unique identifier
    }

    // Define the Codec for this specific type
    // Using .fieldOf("value") makes it a MapCodec, suitable for inlining by dispatch
    public static final MapCodec<StringObject> CODEC = Codec.STRING.fieldOf("value")
            .xmap(StringObject::new, StringObject::getData)
            .stable(); // Mark as stable if appropriate

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringObject that = (StringObject) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "StringObject[data='" + data + "']";
    }
}


// Subclass storing multiple fields (record already implements correctly)
class ComplexObject extends ExampleObject { // Extends the class
    private final String name;
    private final int level;
    // Manual Constructor
    public ComplexObject(String name, int level) {
        this.name = name;
        this.level = level;
    }
    // Manual Getters
    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
    @Override
    public String getTypeIdentifier() {
        return "complex_data"; // Unique identifier
    }

    // Define the MapCodec using RecordCodecBuilder
    public static final MapCodec<ComplexObject> CODEC = RecordCodecBuilder.mapCodec(
            // Explicitly type the 'instance' parameter here!
            (RecordCodecBuilder.Instance<ComplexObject> instance) ->
                    instance.group(
                            // Use manual getter methods now
                            Codec.STRING.fieldOf("name").forGetter(ComplexObject::getName),
                            Codec.INT.fieldOf("level").forGetter(ComplexObject::getLevel)
                    ).apply(instance, ComplexObject::new) // Use manual constructor
    ).stable();

    // Manual equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexObject that = (ComplexObject) o;
        return level == that.level && Objects.equals(name, that.name);
    }

    // Manual hashCode
    @Override
    public int hashCode() {
        return Objects.hash(name, level);
    }

    // Manual toString
    @Override
    public String toString() {
        return "ComplexObject[" +
                "name='" + name + '\'' +
                ", level=" + level +
                ']';
    }
}

public class DispatchDemo {

    // --- 2. Codec Lookup Function ---
    // Takes a type identifier string and returns the corresponding MapCodec
    private static DataResult<? extends MapCodec<? extends ExampleObject>> getCodecByType(String type) {
        return switch (type) {
            case "simple_string" -> DataResult.success(StringObject.CODEC);
            case "complex_data" -> DataResult.success(ComplexObject.CODEC);
            default -> DataResult.error(() -> "Unknown ExampleObject type: " + type);
        };
    }

    // Helper function to get type identifier from an instance (for encoding)
    // Using a method reference makes the dispatch call cleaner
    private static DataResult<String> getTypeIdentifier(ExampleObject obj) {
        if (obj == null) {
            return DataResult.error(() -> "Cannot get type identifier from null object");
        }
        return DataResult.success(obj.getTypeIdentifier());
    }


    // --- 3. Create the Master Dispatch Codec ---
    // The codec for the type identifier itself (which is a string)
    public static final Codec<ExampleObject> DISPATCH_CODEC = Codec.STRING
            // Use partialDispatch here!
            .partialDispatch(
                    "type",                       // The name of the field in JSON holding the type identifier
                    DispatchDemo::getTypeIdentifier, // Function: Instance -> DataResult<Type Identifier>
                    DispatchDemo::getCodecByType      // Function: Type Identifier -> DataResult<MapCodec>
            );

    // --- 4. Main Demo Method ---
    public static void run() {
        DynamicOps<JsonElement> ops = JsonOps.INSTANCE;

        // Create instances of our objects
        ExampleObject simple = new StringObject("Just a string");
        ExampleObject complex = new ComplexObject("Player Character", 25);

        System.out.println("--- Encoding ---");

        // Encode the simple object
        DataResult<JsonElement> encodedSimpleResult = DISPATCH_CODEC.encodeStart(ops, simple);
        encodedSimpleResult.resultOrPartial(System.err::println).ifPresent(json ->
                        System.out.println("Encoded StringObject: " + json)
                // Expected: {"type":"simple_string","value":"Just a string"}
                // "value" field is included directly because StringObject.CODEC is a MapCodec (from .fieldOf)
        );

        // Encode the complex object
        DataResult<JsonElement> encodedComplexResult = DISPATCH_CODEC.encodeStart(ops, complex);
        encodedComplexResult.resultOrPartial(System.err::println).ifPresent(json ->
                        System.out.println("Encoded ComplexObject: " + json)
                // Expected: {"type":"complex_data","name":"Player Character","level":25}
                // "name" and "level" fields are included directly because ComplexObject.CODEC is a MapCodec
        );

        System.out.println("\n--- Decoding ---");

        // Create JSON representing the encoded objects
        JsonElement simpleJson = ops.createMap(Map.of(
                ops.createString("type"), ops.createString("simple_string"),
                ops.createString("value"), ops.createString("Loaded String Data")
        ));

        JsonElement complexJson = ops.createMap(Map.of(
                ops.createString("type"), ops.createString("complex_data"),
                ops.createString("name"), ops.createString("Loaded Complex"),
                ops.createString("level"), ops.createNumeric(99)
        ));

        JsonElement unknownJson = ops.createMap(Map.of(
                ops.createString("type"), ops.createString("unknown_type"),
                ops.createString("data"), ops.createString("Some data")
        ));

        // Decode the simple JSON
        DataResult<ExampleObject> decodedSimpleResult = DISPATCH_CODEC.parse(ops, simpleJson);
        decodedSimpleResult.resultOrPartial(System.err::println).ifPresent(obj -> {
            System.out.println("Decoded simpleJson: " + obj);
            if (obj instanceof StringObject so) {
                System.out.println("  Data: " + so.getData());
            }
            // Expected: Decoded simpleJson: StringObject[data='Loaded String Data']
        });

        // Decode the complex JSON
        DataResult<ExampleObject> decodedComplexResult = DISPATCH_CODEC.parse(ops, complexJson);
        decodedComplexResult.resultOrPartial(System.err::println).ifPresent(obj -> {
            System.out.println("Decoded complexJson: " + obj);
            if (obj instanceof ComplexObject co) {
                System.out.println("  Name: " + co.getName() + ", Level: " + co.getLevel());
            }
            // Expected: Decoded complexJson: ComplexObject[name=Loaded Complex, level=99]
        });

        // Decode JSON with an unknown type
        System.out.println("\n--- Decoding Unknown Type ---");
        DataResult<ExampleObject> decodedUnknownResult = DISPATCH_CODEC.parse(ops, unknownJson);
        decodedUnknownResult.error().ifPresent(error ->
                        System.out.println("Decoding failed as expected: " + error.message())
                // Expected: Decoding failed as expected: Unknown ExampleObject type: unknown_type ...
        );

        // --- Illustration: Codec NOT augmented to MapCodec (requires "value") ---
        // Let's quickly define a codec that *isn't* a MapCodec directly
        Codec<StringObject> nonMapCodec = Codec.STRING.xmap(StringObject::new, StringObject::getData);

        // If we tried to use this in a *hypothetical* modified dispatch:
        // Hypothetical lookup: case "non_map_string" -> DataResult.success(nonMapCodec)

        ExampleObject simpleNonMap = new StringObject("Non-Map Example");
        String typeIdentifierForNonMap = "non_map_string"; // Assume this exists in lookup

        // How dispatch *would* encode if the looked-up codec wasn't a MapCodec
        // (We can simulate this manually, as our main dispatch requires MapCodec)
        JsonObject manualEncoding = new JsonObject();
        manualEncoding.addProperty("type", typeIdentifierForNonMap);
        // Manually encode the object using its specific codec into a "value" field
        DataResult<JsonElement> encodedValue = nonMapCodec.encodeStart(ops, (StringObject) simpleNonMap);
        encodedValue.result().ifPresent(val -> manualEncoding.add("value", val)); // Store result under "value"

        System.out.println("\n--- Encoding Simulation (Non-MapCodec Sub-Codec) ---");
        System.out.println("Simulated encoding: " + manualEncoding);
        // Expected: {"type":"non_map_string","value":"Non-Map Example"}
        // Note the explicit "value" field.

        // How dispatch *would* decode this structure:
        // 1. Reads "type": "non_map_string"
        // 2. Looks up the codec (finds nonMapCodec)
        // 3. Sees it's *not* a MapCodec.
        // 4. Reads the *entire content* of the "value" field.
        // 5. Calls nonMapCodec.parse(ops, contentOfValueField)
        JsonElement valueContent = manualEncoding.get("value");
        DataResult<ExampleObject> decodedNonMap = nonMapCodec.parse(ops, valueContent)
                .map(obj -> (ExampleObject)obj); // Cast needed
        System.out.println("Simulated decoding of 'value' field: " + decodedNonMap.result().orElse(null));
        // Expected: Simulated decoding of 'value' field: StringObject[data='Non-Map Example']

    }
}