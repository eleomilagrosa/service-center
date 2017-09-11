package com.fusiotec.servicecenterapi.servicecenter.models.serialize_object;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;

import java.lang.reflect.Type;

/**
 * Created by Owner on 8/12/2017.
 */

public class AccountsSerialize implements JsonSerializer<Accounts> {
    @Override
    public JsonElement serialize(Accounts src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("first_name", src.getFirst_name());
        jsonObject.addProperty("last_name", src.getLast_name());
        jsonObject.addProperty("username", src.getUsername());
        jsonObject.addProperty("password", src.getPassword());
        jsonObject.addProperty("email", src.getEmail());
        jsonObject.addProperty("phone_no", src.getPhone_no());
        jsonObject.addProperty("image", src.getImage());
        jsonObject.addProperty("account_type_id", src.getAccount_type_id());
        jsonObject.addProperty("approved_by", src.getApproved_by());
        jsonObject.addProperty("is_main_branch", src.getIs_main_branch());
        jsonObject.addProperty("is_deleted", src.getIs_deleted());
        jsonObject.add("date_approved", context.serialize(src.getDate_approved()));
        jsonObject.add("date_created", context.serialize(src.getDate_created()));
        jsonObject.add("date_modified", context.serialize(src.getDate_modified()));
        jsonObject.add("station", context.serialize(src.getStation()));
        return jsonObject;
    }
}
