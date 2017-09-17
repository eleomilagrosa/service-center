package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.AccountsSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Owner on 9/9/2017.
 */

public class RegistrationActivity extends BaseActivity{
    EditText et_fname,et_lname,et_username,et_password,et_mobile_number,et_email,et_cppassword;
    Button btn_next,btn_cancel,btn_admin;
    AppCompatSpinner sp_branch,sp_account_type;

    Accounts selected_account;
    TextView tv_username,tv_password,tv_cppassword,tv_branch,tv_account;


    final public static int REQUEST_REGISTER_ACCOUNT = 201;
    final public static int REQUEST_UPDATE_ACCOUNT = 202;
    final public static int REQUEST_APPROVED_ACCOUNT = 203;
    final public static int REQUEST_ASSIGNED_ADMIN = 204;
    final public static int REQUEST_REMOVED_ADMIN = 205;
    final public static int REQUEST_DELETE_ACCOUNT = 206;
    final public static int REQUEST_GET_STATIONS = 207;

    final public static String ACCOUNT_ID = "account_id";
    final public static String IS_PROFILE = "is_profile";
    boolean isUpdate = false;
    boolean isToBeApproved = false;
    boolean isProfile = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        int account_id = getIntent().getIntExtra(ACCOUNT_ID,0);
        isProfile = getIntent().getBooleanExtra(IS_PROFILE,false);
        isUpdate = account_id != 0;
        if(!isUpdate){
            getStations();
            selected_account = new Accounts();
            setTitle("Registration");
        }else{
            selected_account = realm.copyFromRealm(realm.where(Accounts.class).equalTo("id",account_id).findFirst());
            isToBeApproved = selected_account.getDate_approved() == null;
            setTitle(isToBeApproved ? "Account Approval": "Update");
        }

        initUI();

        if(isUpdate){
            setValues();
        }
    }

    public void setReceiver(String response,int process,int status){
        showProgress(false);
        switch (process){
            case REQUEST_REGISTER_ACCOUNT:
                if(setAccount(response)) {
                    Toast.makeText(RegistrationActivity.this, "Success!, Wait for the Registration approval before you can use the app", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case REQUEST_UPDATE_ACCOUNT:
                if(setAccount(response)) {
                    Toast.makeText(RegistrationActivity.this, "Update Success!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_APPROVED_ACCOUNT:
                if(setAccount(response)){
                    Toast.makeText(RegistrationActivity.this, "Approved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_REMOVED_ADMIN:
                if(setAccount(response)){
                    Toast.makeText(RegistrationActivity.this, "Successfully Removed As Admin!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case REQUEST_ASSIGNED_ADMIN:
                if(setAccount(response)){
                    Toast.makeText(RegistrationActivity.this, "Successfully Assigned!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case REQUEST_DELETE_ACCOUNT:
                if(setAccount(response)){
                    Toast.makeText(RegistrationActivity.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case REQUEST_GET_STATIONS:
                setStations(response);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_delete:
                if(selected_account.getIs_main_branch() == 1){
                    removeAsAdmin();
                }else{
                    actionDelete();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void removeAsAdmin(){
        selected_account.setIs_main_branch(0);
        update_account_status_id(selected_account,REQUEST_REMOVED_ADMIN);
    }
    public void actionDelete(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are sure you want to delete this Account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selected_account.setIs_deleted(1);
                        delete_account(selected_account);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        if(isUpdate && !isProfile){
            getMenuInflater().inflate(R.menu.action_delete, menu);
        }
        return true;
    }

    public void setValues(){
        et_fname.setText(selected_account.getFirst_name());
        et_lname.setText(selected_account.getLast_name());
        et_username.setText(selected_account.getUsername());
        et_password.setText(selected_account.getPassword());
        et_cppassword.setText(selected_account.getPassword());
        et_mobile_number.setText(selected_account.getPhone_no());
        et_email.setText(selected_account.getEmail());

        et_lname.setNextFocusForwardId(et_mobile_number.getId());
        et_username.setVisibility(View.GONE);
        et_password.setVisibility(View.GONE);
        et_cppassword.setVisibility(View.GONE);
        tv_username.setVisibility(View.GONE);
        tv_password.setVisibility(View.GONE);
        tv_cppassword.setVisibility(View.GONE);

        int selected_position = 0;
        for(int i = 0; i < stations.size(); i++){
            if(stations.get(i).getId() == selected_account.getStation_id()){
                selected_position = i;
                break;
            }
        }
        sp_branch.setSelection(selected_position);
        sp_account_type.setSelection(selected_account.getAccount_type_id() == 1 ? 0 : 1);

        if(isProfile){
            tv_branch.setVisibility(View.GONE);
            tv_account.setVisibility(View.GONE);
            sp_branch.setVisibility(View.GONE);
            sp_account_type.setVisibility(View.GONE);
        }
        btn_next.setText((!isUpdate) ? "Register": isToBeApproved ? "Approve" : "Update");

        if(isToBeApproved){
            et_fname.setEnabled(false);
            et_lname.setEnabled(false);
            et_mobile_number.setEnabled(false);
            et_email.setEnabled(false);
            sp_branch.setEnabled(false);
            sp_account_type.setEnabled(false);
        }

        btn_admin.setVisibility( (isUpdate && !isToBeApproved && accounts.getIs_main_branch() == 2 && selected_account.getIs_main_branch() == 0 ) ? View.VISIBLE : View.GONE);
    }
    public void initUI(){
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_cppassword = (TextView) findViewById(R.id.tv_cppassword);
        tv_branch = (TextView) findViewById(R.id.tv_branch);
        tv_account = (TextView) findViewById(R.id.tv_account);

        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_cppassword = (EditText) findViewById(R.id.et_cppassword);
        et_mobile_number = (EditText) findViewById(R.id.et_mobile_number);
        et_email = (EditText) findViewById(R.id.et_email);
        sp_branch = (AppCompatSpinner) findViewById(R.id.sp_branch);
        sp_account_type = (AppCompatSpinner) findViewById(R.id.sp_account_type);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showProgress(true);

               if(isToBeApproved){
                   selected_account.setDate_approved(new Date());
                   selected_account.setApproved_by(accounts.getId());
                   approved_account(selected_account);
               }else{
                   if(isProfile){
                       if(saveProfile()){
                           update_account(selected_account);
                       }else{
                           showProgress(false);
                       }
                   }else{
                       if(save()){
                           if(!isUpdate){
                               account_registration(selected_account);
                           }else{
                               update_account(selected_account);
                           }
                       }else{
                           showProgress(false);
                       }
                   }
               }
            }
        });

        defaultViewSpinner();

        btn_admin = (Button) findViewById(R.id.btn_admin);
        btn_admin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                assignAsAdmin();
            }
        });
    }
    public void assignAsAdmin(){
        selected_account.setIs_main_branch(1);
        update_account_status_id(selected_account,REQUEST_ASSIGNED_ADMIN);
    }

    ArrayList<Stations> stations = new ArrayList<>();
    public void defaultViewSpinner(){
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this,R.layout.simple_spinner_lookup2, getResources().getStringArray(R.array.account_type));
        dataAdapter1.setDropDownViewResource(R.layout.simple_spinner_lookup2);
        sp_account_type.setAdapter(dataAdapter1);
        stations.clear();
        ArrayList<String> sp_branch_populate = new ArrayList<>();
        stations.addAll(realm.copyFromRealm(realm.where(Stations.class).equalTo("is_deleted",0).findAllSorted("station_name")));
        for(Stations temp:stations){
            sp_branch_populate.add(temp.getStation_name());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.simple_spinner_lookup2, sp_branch_populate);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_lookup2);
        sp_branch.setAdapter(dataAdapter);

        sp_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });
    }
    public boolean saveProfile(){
        View error_edit_text = null;
        boolean cancel = false;
        final String fname = et_fname.getText().toString();
        final String lname = et_lname.getText().toString();
        final String mobile_number = et_mobile_number.getText().toString();
        final String email = et_email.getText().toString();

        if (TextUtils.isEmpty(fname)){
            et_fname.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_fname;
        }
        if (TextUtils.isEmpty(lname)){
            et_lname.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_lname;
        }
        if (TextUtils.isEmpty(mobile_number)){
            et_mobile_number.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_mobile_number;
        }
        if (TextUtils.isEmpty(email)){
            et_email.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_email;
        }

        if(cancel){
            if(error_edit_text != null){
                error_edit_text.requestFocus();
            }
            return false;
        }

        selected_account.setFirst_name(fname);
        selected_account.setLast_name(lname);
        selected_account.setPhone_no(mobile_number);
        selected_account.setEmail(email);
        return true;
    }

    public boolean save(){
        View error_edit_text = null;
        boolean cancel = false;
        final String fname = et_fname.getText().toString();
        final String lname = et_lname.getText().toString();
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();
        final String cppassword = et_cppassword.getText().toString();
        final String mobile_number = et_mobile_number.getText().toString();
        final String email = et_email.getText().toString();

        if(stations.isEmpty()){
            Toast.makeText(RegistrationActivity.this, "No Branches Available", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!cppassword.equals(password)){
            et_password.setError("Password Mismatch");
            et_cppassword.setError("Password Mismatch");
            cancel = true;
            error_edit_text = et_password;
        }

        if (TextUtils.isEmpty(fname)){
            et_fname.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_fname;
        }
        if (TextUtils.isEmpty(lname)){
            et_lname.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_lname;
        }
        if (TextUtils.isEmpty(username)){
            et_username.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_username;
        }
        if (TextUtils.isEmpty(password)){
            et_password.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_password;
        }
        if (TextUtils.isEmpty(mobile_number)){
            et_mobile_number.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_mobile_number;
        }
        if (TextUtils.isEmpty(email)){
            et_email.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_email;
        }

        if(cancel){
            if(error_edit_text != null){
                error_edit_text.requestFocus();
            }
            return false;
        }

        selected_account.setFirst_name(fname);
        selected_account.setLast_name(lname);
        selected_account.setUsername(username);
        selected_account.setPassword(password);
        selected_account.setPhone_no(mobile_number);
        selected_account.setEmail(email);
        selected_account.setStation_id(stations.get(sp_branch.getSelectedItemPosition()).getId());
        selected_account.setStation(realm.where(Stations.class).equalTo("id",stations.get(sp_branch.getSelectedItemPosition()).getId()).findFirst());
        selected_account.setAccount_type_id(sp_account_type.getSelectedItemPosition() == 0 ? Accounts.SERVICE_CENTER : Accounts.MAIN_BRANCH);

        return true;
    }
    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void account_registration(Accounts account){
        requestManager.setRequestAsync(requestManager.getApiService().create_account(account.getFirst_name(), account.getLast_name(),account.getUsername(),account.getPassword(),account.getEmail(),account.getPhone_no(), account.getAccount_type_id(), account.getStation_id()),REQUEST_REGISTER_ACCOUNT);
    }
    public void update_account(Accounts account){
        requestManager.setRequestAsync(requestManager.getApiService().update_account(account.getId(),account.getFirst_name(), account.getLast_name(),account.getEmail(),account.getPhone_no(),account.getAccount_type_id(),account.getStation_id()),REQUEST_UPDATE_ACCOUNT);
    }
    public void approved_account(Accounts account){
        requestManager.setRequestAsync(requestManager.getApiService().approved_account(account.getId(),account.getApproved_by()),REQUEST_APPROVED_ACCOUNT);
    }
    public void update_account_status_id(Accounts account,int process){
        requestManager.setRequestAsync(requestManager.getApiService().update_account_status_id(account.getId(),account.getIs_main_branch()),process);
    }
    public void delete_account(Accounts account){
        requestManager.setRequestAsync(requestManager.getApiService().delete_account(account.getId()),REQUEST_DELETE_ACCOUNT);
    }
    public void getStations(){
        requestManager.setRequestAsync(requestManager.getApiService().get_stations(),REQUEST_GET_STATIONS);
    }
    public boolean setAccount(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(Accounts.TABLE_NAME);
                final ArrayList<Accounts> accounts = new GsonBuilder()
                        .registerTypeAdapter(Accounts.class,new AccountsSerialize())
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<Accounts>>(){}.getType());
                if(!accounts.isEmpty()){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Stations station = realm.where(Stations.class).equalTo("id",accounts.get(0).getStation_id()).findFirst();
                            if(station != null){
                                accounts.get(0).setStation(station);
                                realm.copyToRealmOrUpdate(accounts.get(0));
                            }
                        }
                    });
                }else{
                    errorMessage("Account does not exist");
                    return false;
                }
            }else{
                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
                return false;
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public boolean setStations(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
//            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
            JSONArray jsonArray = jsonObject.getJSONArray(Stations.TABLE_NAME);
            final ArrayList<Stations> stations = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                    .fromJson(jsonArray.toString(), new TypeToken<List<Stations>>(){}.getType());
            if(!stations.isEmpty()){
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm){
                        realm.copyToRealmOrUpdate(stations);
                    }
                });
                defaultViewSpinner();
            }
//                else{
//                    errorMessage("Account does not exist");
//                    return false;
//                }
//            }else{
//                errorMessage(jsonObject.getString(RetrofitRequestManager.MESSAGE));
//                return false;
//            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
