package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.fragments.CustomerInfoFragment;

/**
 * Created by Owner on 9/3/2017.
 */

public class CustomerInfoActivity extends BaseActivity implements
        CustomerInfoFragment.CustomerInfoFragmentListener {

    public final static int CLOSE_ACTIVITY = 0;
    public final static int FRAGMENT_ADD_CUSTOMER_INFO = 1;
    int current_fragment = FRAGMENT_ADD_CUSTOMER_INFO;

    public final static String SHOW_FRAGMENT = "show_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_template);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        current_fragment = getIntent().getExtras().getInt(SHOW_FRAGMENT,current_fragment);

        switch (current_fragment){
            case FRAGMENT_ADD_CUSTOMER_INFO:
                CustomerInfoFragment customerInfoFragment = new CustomerInfoFragment();
                setFragment(customerInfoFragment,0,current_fragment,false);
                break;
        }
    }
    public void switchFragment(int fragment){
        if(current_fragment == fragment){
            return;
        }
        int before_fragment = current_fragment;
        current_fragment = fragment;
        switch (fragment){
            case FRAGMENT_ADD_CUSTOMER_INFO:
                CustomerInfoFragment customerInfoFragment = new CustomerInfoFragment();
                setFragment(customerInfoFragment,before_fragment,current_fragment);
                break;

            case CLOSE_ACTIVITY:
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }
    public void setFragment(Fragment fragment, int before, int after){
        setFragment(fragment,before,after,true);
    }
    public void setFragment(Fragment fragment, int before, int after, boolean no_animation){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(no_animation){
            if(before > after){
                fragmentTransaction.setCustomAnimations(
                        R.anim.right_in,
                        R.anim.left_out);
            }else{
                fragmentTransaction.setCustomAnimations(
                        R.anim.left_in,
                        R.anim.right_out);
            }
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        alertToExit();
    }
    public void alertToExit(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switchFragment(CLOSE_ACTIVITY);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){

                    }
                })
                .show();
    }
}
