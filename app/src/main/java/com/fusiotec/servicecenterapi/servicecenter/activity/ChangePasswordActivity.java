package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;

import io.realm.Realm;

/**
 * Created by Owner on 9/10/2017.
 */

public class ChangePasswordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Change Password");

        initUI();
    }

    EditText et_oldpassword, et_newpassword, et_cppassword;
    Button btn_next, btn_cancel;

    public void initUI() {
        et_oldpassword = (EditText) findViewById(R.id.et_oldpassword);
        et_newpassword = (EditText) findViewById(R.id.et_newpassword);
        et_cppassword = (EditText) findViewById(R.id.et_cppassword);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changePass()){
                    Toast.makeText(ChangePasswordActivity.this, "Change Pass Success", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }

    public boolean changePass(){
        final String oldpassword = et_oldpassword.getText().toString();
        final String newpassword = et_newpassword.getText().toString();
        final String cppassword = et_cppassword.getText().toString();

        View error_edit_text = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(newpassword)){
            et_newpassword.setError(getString(R.string.error_field_required));
            cancel = true;
            error_edit_text = et_newpassword;
        }
        if (!ls.getString(LocalStorage.ACCOUNT_PASSWORD,"").equals(oldpassword)){
            et_oldpassword.setError("Should be your Old Password");
            cancel = true;
            error_edit_text = et_oldpassword;
        }
        if (!cppassword.equals(newpassword)){
            et_newpassword.setError("Password Mismatch");
            et_cppassword.setError("Password Mismatch");
            cancel = true;
            error_edit_text = et_newpassword;
        }
        if(cancel){
            if(error_edit_text != null){
                error_edit_text.requestFocus();
            }
            return false;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                accounts = realm.where(Accounts.class).equalTo("id",accounts.getId()).findFirst();
                accounts.setPassword(newpassword);
            }
        });
        ls.saveStringOnLocalStorage(LocalStorage.ACCOUNT_PASSWORD,newpassword);
        return true;
    }
    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
