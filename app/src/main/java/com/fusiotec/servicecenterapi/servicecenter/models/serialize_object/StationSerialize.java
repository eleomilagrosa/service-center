package com.fusiotec.servicecenterapi.servicecenter.models.serialize_object;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;

import java.lang.reflect.Type;

/**
 * Created by Owner on 8/12/2017.
 */

public class StationSerialize implements JsonSerializer<Stations> {
    @Override
    public JsonElement serialize(Stations src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("station_name", src.getStation_name());
        jsonObject.addProperty("station_prefix", src.getStation_prefix());
        jsonObject.addProperty("station_address", src.getStation_address());
        jsonObject.addProperty("station_number", src.getStation_number());
        jsonObject.addProperty("station_description", src.getStation_description());
        jsonObject.addProperty("station_image", src.getStation_image());
        jsonObject.add("date_created", context.serialize(src.getDate_created()));
        jsonObject.add("date_modified", context.serialize(src.getDate_modified()));
        return jsonObject;
    }
}
