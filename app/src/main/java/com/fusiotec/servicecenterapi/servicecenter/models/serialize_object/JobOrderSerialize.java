package com.fusiotec.servicecenterapi.servicecenter.models.serialize_object;

import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Owner on 8/12/2017.
 */

public class JobOrderSerialize implements JsonSerializer<JobOrders> {
    @Override
    public JsonElement serialize(JobOrders src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("unit", src.getUnit());
        jsonObject.addProperty("model", src.getModel());
        jsonObject.addProperty("dealer", src.getDealer());
        jsonObject.addProperty("serial_number", src.getSerial_number());
        jsonObject.addProperty("warranty_label", src.getWarranty_label());
        jsonObject.addProperty("complaint", src.getComplaint());
        jsonObject.addProperty("customer_id", src.getCustomer_id());
        jsonObject.addProperty("station_id", src.getStation_id());
        jsonObject.addProperty("account_id", src.getAccount_id());
        jsonObject.addProperty("status_id", src.getStation_id());
        jsonObject.addProperty("repair_status", src.getRepair_status());
        jsonObject.add("date_of_purchased", context.serialize(src.getDate_created()));
        jsonObject.add("date_created", context.serialize(src.getDate_created()));
        jsonObject.add("date_modified", context.serialize(src.getDate_modified()));
        jsonObject.add("date_time_closed", context.serialize(src.getDate_time_closed()));
        jsonObject.add("jobOrderDiagnosis", context.serialize(src.getJobOrderDiagnosis()));
        jsonObject.add("jobOrderShipping", context.serialize(src.getJobOrderShipping()));
        jsonObject.add("jobOrderRepairStatus", context.serialize(src.getRepair_status()));
        jsonObject.add("jobOrderForReturn", context.serialize(src.getJobOrderForReturn()));
        jsonObject.add("customer", context.serialize(src.getCustomer()));
        jsonObject.add("jobOrderImages", context.serialize(src.getJobOrderImages()));
        return jsonObject;
    }
}
