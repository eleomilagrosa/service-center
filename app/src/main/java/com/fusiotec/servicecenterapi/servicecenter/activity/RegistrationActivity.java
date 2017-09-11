package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

import java.util.ArrayList;
import java.util.Date;

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
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm){
                Accounts accounts = realm.where(Accounts.class).equalTo("id",selected_account.getId()).findFirst();
                accounts.setIs_main_branch(0);
            }
        });
        Toast.makeText(RegistrationActivity.this, "Successfully Removed As Admin!", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
    public void actionDelete(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are sure you want to delete this Account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Accounts account = realm.where(Accounts.class).equalTo("id",selected_account.getId()).findFirst();
                                account.setIs_deleted(1);
                                onBackPressed();
                            }
                        });
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
               if(isToBeApproved){
                   realm.executeTransaction(new Realm.Transaction() {
                       @Override
                       public void execute(Realm realm) {
                           Accounts accounts = realm.where(Accounts.class).equalTo("id",selected_account.getId()).findFirst();
                           accounts.setDate_approved(new Date());
                           accounts.setApproved_by(accounts.getId());
                       }
                   });
                   Toast.makeText(RegistrationActivity.this, "Approved!", Toast.LENGTH_SHORT).show();
                   finish();
               }else{
                   if(isProfile){
                       if(saveProfile()){
                           Toast.makeText(RegistrationActivity.this, "Update Success!", Toast.LENGTH_SHORT).show();
                           finish();
                       }
                   }else{
                       if(save()){
                           if(isUpdate){
                               Toast.makeText(RegistrationActivity.this, "Update Success!", Toast.LENGTH_SHORT).show();
                           }else{
                               Toast.makeText(RegistrationActivity.this, "Success!, Wait for the Registration approval before you can use the app", Toast.LENGTH_LONG).show();
                           }
                           finish();
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
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm){
                Accounts accounts = realm.where(Accounts.class).equalTo("id",selected_account.getId()).findFirst();
                accounts.setIs_main_branch(1);
            }
        });
        Toast.makeText(RegistrationActivity.this, "Successfully Assigned!", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    ArrayList<Stations> stations = new ArrayList<>();
    public void defaultViewSpinner(){
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this,R.layout.simple_spinner_lookup2, getResources().getStringArray(R.array.account_type));
        dataAdapter1.setDropDownViewResource(R.layout.simple_spinner_lookup2);
        sp_account_type.setAdapter(dataAdapter1);

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
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                selected_account = realm.where(Accounts.class).equalTo("id",selected_account.getId()).findFirst();
                selected_account.setFirst_name(fname);
                selected_account.setLast_name(lname);
                selected_account.setPhone_no(mobile_number);
                selected_account.setEmail(email);
            }
        });
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

        Log.e("position",sp_branch.getSelectedItemPosition()+"");

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


        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                long getMaxImage = Utils.getMax(realm,Accounts.class,"id");
                getMaxImage--;
                if(!isUpdate){
                    selected_account.setId((int)getMaxImage);
                }else{
                    selected_account = realm.where(Accounts.class).equalTo("id",selected_account.getId()).findFirst();
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
                if(!isUpdate){
                    realm.copyToRealmOrUpdate(selected_account);
                }
            }
        });
        return true;
    }
    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
