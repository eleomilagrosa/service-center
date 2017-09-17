package com.fusiotec.servicecenterapi.servicecenter.manager;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Owner on 9/16/2017.
 */

public class PhotoUploader extends IntentService {
    public static final String TAG = PhotoUploader.class.getSimpleName();

    public final static int REQUEST_UPLOAD_IMAGE = 301;

    Realm realm;
    ArrayList<JobOrderImages> images = new ArrayList<>();
    RetrofitRequestManager requestManager;
    LocalStorage ls;
    int current_index = 0;
    boolean stopper = true;
    public PhotoUploader(){
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent){
        ls = new LocalStorage(this);
        if(!ls.getBoolean(LocalStorage.IS_STILL_UPLOADING,true)){
            ls.saveBooleanOnLocalStorage(LocalStorage.IS_STILL_UPLOADING,true);
            realm = Realm.getDefaultInstance();
            requestManager = new RetrofitRequestManager(this,callBackListener);
            prepareToUpload();
        }
    }
    public void prepareToUpload(){
        RealmResults<JobOrderImages> results = realm.where(JobOrderImages.class).lessThan("id",0).findAll();
        images.addAll(realm.copyFromRealm(results));
        startUploading();
    }

    public void startUploading(){
        while (current_index < images.size() && stopper){
            uploadImage(images.get(current_index));
        }
        realm.close();
    }
    public void uploadImage(JobOrderImages job_order_images){
        File file = new File(job_order_images.getImage());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

        RequestBody label = Utils.convertToRequestBody("text/plain",job_order_images.getLabel());
        RequestBody job_order_id = Utils.convertToRequestBody("text/plain",job_order_images.getJob_order_id());
        RequestBody status_id = Utils.convertToRequestBody("text/plain",String.valueOf(job_order_images.getJob_order_status_id()));

        requestManager.setRequestSync(requestManager.getApiService().upload_job_order_image(body,label,status_id,job_order_id),REQUEST_UPLOAD_IMAGE);
    }

    public void setReceiver(String response,int process){
        switch (process){
            case REQUEST_UPLOAD_IMAGE:
                if(setImage(response)){
                    current_index++;
                }else{
                    stopper = false;
                }
                break;
            default:
                stopper = false;
                break;
        }
    }

    public boolean setImage(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(JobOrderImages.TABLE_NAME);
                final ArrayList<JobOrderImages> jobOrders = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<JobOrderImages>>(){}.getType());
                if(!jobOrders.isEmpty()){
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm){
                            JobOrderImages old_image = realm.where(JobOrderImages.class).equalTo("id",images.get(current_index).getId()).findFirst();
                            if(old_image != null){
                                old_image.deleteFromRealm();
                            }
                            JobOrders job_order = realm.where(JobOrders.class).equalTo("id",jobOrders.get(0).getJob_order_id()).findFirst();
                            if(job_order != null){
                                job_order.getJobOrderImages().add(realm.copyToRealmOrUpdate(jobOrders.get(0)));
                            }
                        }
                    });
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch (Exception e){
            Log.e("upload_error",e.getMessage()+"");
            return false;
        }
        return true;
    }

    RetrofitRequestManager.callBackListener callBackListener = new RetrofitRequestManager.callBackListener(){
        @Override
        public void requestReceiver(String response, int process, int status, int response_code,String message){
            if(response_code == RetrofitRequestManager.REQUEST_SUCCESS){
                setReceiver(response,process);
            }else{
                realm.close();
                stopper = false;
            }
        }
    };

    @Override
    public void onDestroy(){
        ls.saveBooleanOnLocalStorage(LocalStorage.IS_STILL_UPLOADING,false);
        super.onDestroy();
    }
}
