package warewise.server.common.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class JsonSerializer {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Generic method to serialize a list of any class type to JSON.
     *
     * @param list  The list of objects to serialize.
     * @param <T>   The type parameter.
     * @return The JSON representation of the list.
     */
    public static <T> String serializeListToJson(List<T> list) {
        // The clazz parameter is not directly needed for Gson, but can be useful for checks.
        return gson.toJson(list);
    }
}