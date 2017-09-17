package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderStatus;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.AccountsSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.CrashCatcher;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.HTTP_BAD_REQUEST;
import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.INTERNAL_SERVER_ERROR;
import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.REQUEST_SUCCESS;
import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.UNAUTHORIZED;

public class LoginActivity extends BaseActivity{


    private final static int LOGIN = 2;

    private final int REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE = 1000;

    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private Button mConnect;
    private Button registration;

    RetrofitRequestManager requestManager;
    String mUsername,mPassword;

    String accounts = "{\"accounts\":[{\"id\":1,\"first_name\":\"Eleojasmil\",\"last_name\":\"Milagrosa\",\"username\":\"eleo\",\"password\":\"ed2b1f468c5f915f3f1cf75d7068baae\",\"email\":\"eleomilagrosa2@yahoo.com\",\"phone_no\":null,\"image\":null,\"account_type_id\":1,\"station_id\":1,\"is_main_branch\":0,\"approved_by\":null,\"date_approved\":\"2017-08-06 23:46:41\",\"date_created\":\"2017-08-06 21:30:57\",\"date_modified\":\"2017-08-13 20:42:23\",\"station\":{\"id\":1,\"station_name\":\"Indian Palace\",\"station_prefix\":\"IN\",\"station_address\":\"davao\",\"station_number\":\"321213213\",\"station_description\":\"kanto\",\"station_image\":null,\"date_created\":\"2017-08-12 14:41:57\",\"date_modified\":\"2017-08-12 14:41:57\"}}],\"success\":1,\"message\":\"Success\"}";


    public AppCompatActivity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ls.saveBooleanOnLocalStorage(LocalStorage.IS_STILL_UPLOADING,false);

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashCatcher)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashCatcher());
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ArrayList<JobOrderStatus> jobOrderStatus = new ArrayList<>();
                jobOrderStatus.add(new JobOrderStatus(1,"PROCESSING"));
                jobOrderStatus.add(new JobOrderStatus(2,"DIAGNOSED"));
                jobOrderStatus.add(new JobOrderStatus(3,"FORWARDED"));
                jobOrderStatus.add(new JobOrderStatus(4,"RECEIVED AT MAIN"));
                jobOrderStatus.add(new JobOrderStatus(5,"FOR RETURN"));
                jobOrderStatus.add(new JobOrderStatus(6,"RECEIVED AT SC"));
                jobOrderStatus.add(new JobOrderStatus(7,"PICK UP"));
                jobOrderStatus.add(new JobOrderStatus(8,"CLOSED"));
                realm.copyToRealmOrUpdate(jobOrderStatus);

//                Stations station = new Stations();
//                station.setId(1);
//                station.setStation_name("Indian Palace");
//                station.setStation_prefix("IN");
//                station.setStation_address("davao");
//                station.setStation_number("31242134213");
//                station.setStation_image("");
//                station.setDate_created(Utils.stringToDate("2017-08-12 14:41:57","yyyy-mm-dd HH:mm:ss"));
//                station.setDate_created(Utils.stringToDate("2017-08-12 14:41:57","yyyy-mm-dd HH:mm:ss"));
//                realm.copyToRealmOrUpdate(station);
            }
        });

        int account_id = ls.getInt(LocalStorage.ACCOUNT_ID,0);
        if(account_id != 0){
            goToDashboard();
        }

        permissionRequester(REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA);

        initUI();

    }

    public void initUI(){
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConnect = (Button) findViewById(R.id.login_connect);
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                setLogin(accounts);
                attemptLogin();
            }
        });
        registration = (Button) findViewById(R.id.registration);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.bottom_in, R.anim.freeze);
            }
        });
        mProgressView = findViewById(R.id.login_progress);
    }
    private void attemptLogin(){
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        if (cancel){
            focusView.requestFocus();
        } else {
            showProgress(true);
            loginProcess(mUsername, mPassword);
        }
    }

    public void loginProcess(String username,String password){
        requestManager = new RetrofitRequestManager(this,callBackListener);
        requestManager.setRequestAsync(requestManager.getApiService().login(username,password),LOGIN);
    }

    public void setLogin(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(Accounts.TABLE_NAME);
                final ArrayList<Accounts> accounts = new GsonBuilder()
                        .registerTypeAdapter(Accounts.class,new AccountsSerialize())
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<Accounts>>(){}.getType());
                if(!accounts.isEmpty()){
//                    mUsername = mUsernameView.getText().toString();
//                    mPassword = mPasswordView.getText().toString();
//                    accounts.get(0).setAccount_type_id(Integer.parseInt(mUsername));
//                    accounts.get(0).setIs_main_branch(Integer.parseInt(mPassword));
                    final Accounts temp_account = accounts.get(0);
                    if(temp_account.getDate_approved() != null){
                        realm.executeTransaction(new Realm.Transaction(){
                            @Override
                            public void execute(Realm realm){
                                realm.insertOrUpdate(temp_account);
                            }
                        });
                        ls.saveIntegerOnLocalStorage(LocalStorage.ACCOUNT_ID, temp_account.getId());
                        ls.saveStringOnLocalStorage(LocalStorage.ACCOUNT_PASSWORD, mPassword);
                        goToDashboard();
                    }else{
                        errorMessage("Account did not approved yet");
                    }
                }else{
                    errorMessage("Account does not exist");
                }
            }else{
                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
            }
        }catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        showProgress(false);
    }

    public void goToDashboard(){
        Intent in = new Intent(LoginActivity.this,Dashboard.class);
        startActivity(in);
        finish();
        overridePendingTransition(R.anim.bottom_in, R.anim.freeze);
    }
    @Override
    public void showProgress(final boolean show){
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mConnect.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mConnect.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mConnect.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });
        registration.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        registration.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registration.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation){
                mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }



    public void permissionRequester(int code,String... permissions){
        ArrayList<String> permission_list = new ArrayList<>();
        for(String temp:permissions){
            int checker = ActivityCompat.checkSelfPermission(this,temp);
            if(checker != PackageManager.PERMISSION_GRANTED){
                permission_list.add(temp);
            }
        }
        if(!permission_list.isEmpty()){
            ActivityCompat.requestPermissions(this,permission_list.toArray(new String[permission_list.size()]),
                    code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE:
                for (int results:grantResults){
                    if(results != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    }
                }
                break;
        }
    }

    public void setReceiver(String response,int process,int status){
        switch (process){
            case LOGIN : setLogin(response);
                break;
        }
    }
}
