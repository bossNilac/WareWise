package warewise.server.common.handler;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonSerializer {

    /**
     * Serializes a list of any class type to JSON with optional excluded fields.
     *
     * @param list The list of objects to serialize.
     * @param excludedFields List of field names to exclude. Can be null or empty.
     * @param <T> The type parameter.
     * @return The JSON representation of the list.
     */
    public static <T> String serializeListToJson(List<T> list, List<String> excludedFields) {
        Gson gson;

        if (excludedFields == null || excludedFields.isEmpty()) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        } else {
            ExclusionStrategy strategy = new ExclusionStrategy() {
                private final Set<String> fieldsToSkip = new HashSet<>(excludedFields);

                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return fieldsToSkip.contains(f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false; // Don't skip any class entirely
                }
            };

            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .addSerializationExclusionStrategy(strategy)
                    .create();
        }

        return gson.toJson(list);
    }

    /**
     * Convenience method with no exclusions.
     */
    public static <T> String serializeListToJson(List<T> list) {
        return serializeListToJson(list, null);
    }
}
