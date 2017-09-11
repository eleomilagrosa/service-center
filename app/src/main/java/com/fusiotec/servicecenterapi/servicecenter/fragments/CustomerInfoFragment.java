package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.CustomerInfoActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.NewJobOrderActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

import io.realm.Realm;

/**
 * Created by Owner on 8/8/2017.
 */

public class CustomerInfoFragment extends BaseFragment{
    View rootView;
    Button btn_next,btn_cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_customer_info, container, false);
        initUI();
        if(jobOrder != null){
            setValues();
            fieldIsEditable(jobOrder.getCustomer().getId() == 0);
        }
        return rootView;
    }

    JobOrders jobOrder;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }
    EditText et_fname,et_lname,et_address,et_mobile_number,et_email;
    public void initUI(){
        et_fname = rootView.findViewById(R.id.et_fname);
        et_lname = rootView.findViewById(R.id.et_lname);
        et_address = rootView.findViewById(R.id.et_address);
        et_mobile_number = rootView.findViewById(R.id.et_mobile_number);
        et_email = rootView.findViewById(R.id.et_email);

        btn_cancel = rootView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.onBackPressed();
            }
        });
        btn_next = rootView.findViewById(R.id.btn_next);

        if(getActivity() instanceof CustomerInfoActivity){
            btn_next.setText("Save");
        }
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                boolean save = saveCustomer();
                if(getActivity() instanceof NewJobOrderActivity){
                    if(save) mListener.switchFragment(NewJobOrderActivity.FRAGMENT_NEW_JOB_ORDER);
                }else if(getActivity() instanceof CustomerInfoActivity){
                    if(save) successfullyAdded();
                }
            }
        });
    }
    public void fieldIsEditable(boolean editable){
        et_fname.setEnabled(editable);
        et_lname.setEnabled(editable);
        et_address.setEnabled(editable);
        et_mobile_number.setEnabled(editable);
        et_email.setEnabled(editable);
    }
    public void setValues(){
        et_fname.setText(jobOrder.getCustomer().getFirst_name());
        et_lname.setText(jobOrder.getCustomer().getLast_name());
        et_address.setText(jobOrder.getCustomer().getAddress());
        et_mobile_number.setText(jobOrder.getCustomer().getPhone_no());
        et_email.setText(jobOrder.getCustomer().getEmail());
    }
    public boolean saveCustomer(){
        boolean cancel = false;
        final String fname = et_fname.getText().toString();
        final String lname = et_lname.getText().toString();
        final String address = et_address.getText().toString();
        final String mobile_number = et_mobile_number.getText().toString();
        final String email = et_email.getText().toString();

        if (TextUtils.isEmpty(fname)) {
            et_fname.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(lname)) {
            et_lname.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(address)) {
            et_address.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(mobile_number)) {
            et_mobile_number.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if(cancel){
            return false;
        }

        if(getActivity() instanceof NewJobOrderActivity){
            if(jobOrder.getCustomer().getId() == 0){
                setCustomer(jobOrder.getCustomer(),fname,lname,address,mobile_number,email);
            }
            jobOrder.setCustomer_id(jobOrder.getCustomer().getId());
        }else if(getActivity() instanceof CustomerInfoActivity){
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Customers new_customer = new Customers();
                    setCustomer(new_customer,fname,lname,address,mobile_number,email);
                    realm.copyToRealmOrUpdate(new_customer);
                }
            });
        }
        return true;
    }
    public void setCustomer(Customers customer,String fname,String lname,String address,String mobile_number,String email){
        long getMax = Utils.getMax(realm,Customers.class,"id");
        customer.setId((int)getMax - 1);
        customer.setFirst_name(fname);
        customer.setLast_name(lname);
        customer.setAddress(address);
        customer.setPhone_no(mobile_number);
        customer.setEmail(email);
        customer.setStation_id(accounts.getStation().getId());
        customer.setDate_created(Utils.getServerDate(ls));
        customer.setDate_modified(Utils.getServerDate(ls));
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof CustomerInfoFragmentListener){
            mListener = (CustomerInfoFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    CustomerInfoFragmentListener mListener;
    public interface CustomerInfoFragmentListener {
        void switchFragment(int fragment);
        void onBackPressed();
    }

    public void successfullyAdded(){
        new AlertDialog.Builder(getActivity())
                .setTitle("Success")
                .setMessage("Successfully added Customer")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.switchFragment(CustomerInfoActivity.CLOSE_ACTIVITY);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mListener.switchFragment(CustomerInfoActivity.CLOSE_ACTIVITY);
                    }
                })
                .show();
    }
}
