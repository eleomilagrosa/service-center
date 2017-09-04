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

import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderStatus;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.AccountsSerialize;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.StationSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.network.Token;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Constants;
import com.fusiotec.servicecenterapi.servicecenter.utilities.CrashCatcher;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager.*;
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

//        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashCatcher)) {
//            Thread.setDefaultUncaughtExceptionHandler(new CrashCatcher());
//        }


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
            }
        });

        Accounts accounts = realm.where(Accounts.class).findFirst();
        if(accounts != null){
            goToDashboard();
        }
        permissionRequester(REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA);

        requestManager = new RetrofitRequestManager(this,callBackListener);
        initUI();

    }

    public void initUI(){
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConnect = (Button) findViewById(R.id.login_connect);
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogin(accounts);
//                attemptLogin();
            }
        });
        mProgressView = findViewById(R.id.login_progress);
    }
    private void attemptLogin() {
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
            requestToken();
        }
    }

    public void loginProcess(String username,String password){
        requestManager = new RetrofitRequestManager(this,callBackListener);
        requestManager.setRequestAsync(requestManager.getApiService().kroid_check_if_account_exist(username,password),LOGIN);
    }

    public void setLogin(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
//            JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
//                JsonArray jsonArray = jsonObject.getAsJsonArray(Accounts.TABLE_NAME).getAsJsonArray();
                JSONArray jsonArray = jsonObject.getJSONArray(Accounts.TABLE_NAME);
                final ArrayList<Accounts> accounts = new GsonBuilder()
                        .registerTypeAdapter(Accounts.class,new AccountsSerialize())
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<Accounts>>(){}.getType());
                if(!accounts.isEmpty()){
                    mUsername = mUsernameView.getText().toString();
                    accounts.get(0).setAccount_type_id(Integer.parseInt(mUsername));
                    final Accounts temp_account = accounts.get(0);
                    if(temp_account.getDate_approved() != null){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.delete(Accounts.class);
                                realm.insertOrUpdate(temp_account);
                            }
                        });
                        goToDashboard();
                    }else{
                        errorMessage("Account did not approved yet");
                    }
                }else{
                    errorMessage("Account does not exist");
                }
            }else{
//                errorMessage(jsonObject.get(RetrofitRequestManager.MESSAGE).getAsString());
                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
            }
        }catch(Exception e){
            Log.e("Accounts",""+e.getMessage());
        }
        showProgress(false);
    }


    public void requestToken(){
        Call<Token> callBack = requestManager.getApiService().kroid_get_token("password", Constants.client_id, Constants.client_secret,mUsername,mPassword,"");
        callBack.enqueue(new Callback<Token>(){
            @Override
            public void onResponse(Call<Token> call, Response<Token> response){
                if(response.code() == REQUEST_SUCCESS){
                    ls.saveStringOnLocalStorage(LocalStorage.TOKEN_TYPE,response.body().getToken_type());
                    ls.saveStringOnLocalStorage(LocalStorage.ACCESS_TOKEN,response.body().getAccess_token());
                    loginProcess(mUsername, mPassword);
                }else{
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Utils.saveToErrorLogs("onResponse:authentication"+"\n"+response.toString());
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t){
                showProgress(false);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.saveToErrorLogs("onFailure:authentication\n"+t.getMessage()+"\n\n"+call.toString());
            }
        });
    }

    public void goToDashboard(){
        Intent in = new Intent(LoginActivity.this,Dashboard.class);
        startActivity(in);
        finish();
        overridePendingTransition(R.anim.bottom_in, R.anim.freeze);
    }

    private void showProgress(final boolean show){
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mConnect.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mConnect.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mConnect.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
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

    public void errorMessage(final String message){
        runOnUiThread(new Runnable() {

            @Override
            public void run()
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Error");
                alertDialog.setMessage(message);
                alertDialog.setIcon(android.R.drawable.stat_notify_error);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }

        });
    }


    RetrofitRequestManager.callBackListener callBackListener = new RetrofitRequestManager.callBackListener() {
        @Override
        public void requestReceiver(String response, int process, int status, int response_code, String message) {
            if(response_code == REQUEST_SUCCESS){
                setReceiver(response,process,status);
            }else{
                String title;
                switch (response_code){
                    case INTERNAL_SERVER_ERROR:
                        title = "Internal Server Error";
                        break;
                    case HTTP_BAD_REQUEST:
                        title = "Http Bad Request";
                        break;
                    case UNAUTHORIZED:
                        title = "Unauthorized";
                        break;
                    default:
                        title = "Unknown";
                        break;
                }
                errorMessage(title + " - "+ message);
                showProgress(false);
            }
        }
    };

    public void setReceiver(String response,int process,int status){
        Log.e(process+"response"+status,response);
        switch (process) {
            case RetrofitRequestManager.HTTP_BAD_REQUEST :
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                showProgress(false);
                errorMessage("Can't Connect Please Check Internet Connection");
                break;
            case RetrofitRequestManager.NETWORK_CONNECTION_FAILED :
            case RetrofitRequestManager.SERVER_CONNECTION_FAILED:
                switch (status){
                    default:
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        showProgress(false);
                        errorMessage("Can't Connect Please Check Internet Connection");
                        break;
                }
                break;
            case LOGIN : setLogin(response);
                break;
        }
    }
}
