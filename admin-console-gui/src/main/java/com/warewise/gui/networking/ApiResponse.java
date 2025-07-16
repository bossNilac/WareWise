package com.warewise.gui.networking;

import com.google.gson.*;

public class ApiResponse {
    private boolean success;
    private String message;
    private String data;

    public ApiResponse(String jsonResponse) {
        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
        this.success = root.get("success").getAsBoolean();
        this.message = root.get("message").getAsString();

        JsonElement dataElement = root.get("data");

        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

        // Try parsing `data` in case it's a stringified JSON object/array
        if (dataElement.isJsonPrimitive() && dataElement.getAsJsonPrimitive().isString()) {
            String dataString = dataElement.getAsString();
            try {
                JsonElement parsed = JsonParser.parseString(dataString);
                this.data = prettyGson.toJson(parsed); // Pretty-print parsed object/array
            } catch (JsonSyntaxException e) {
                this.data = dataString; // It's a plain string (like a token)
            }
        } else {
            this.data = prettyGson.toJson(dataElement); // Pretty-print object/array directly
        }
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}