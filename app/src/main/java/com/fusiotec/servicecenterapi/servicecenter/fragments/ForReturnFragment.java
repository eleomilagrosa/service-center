package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.ShippingActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

/**
 * Created by Owner on 8/13/2017.
 */

public class ForReturnFragment extends BaseFragment{

    View rootView;
    Button btn_next,btn_cancel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_job_order_shipping, container, false);

        initUI();
        setValues();
        return rootView;
    }
    JobOrders jobOrder;
    EditText et_shopping,et_shopping_note;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }

    public void initUI(){
        et_shopping = (EditText) rootView.findViewById(R.id.et_shipping);
        et_shopping_note = (EditText) rootView.findViewById(R.id.et_shipping_note);
        btn_cancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.onBackPressed();
            }
        });
        btn_next = (Button) rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveShipping(et_shopping.getText().toString(),et_shopping_note.getText().toString());
                mListener.switchFragment(ShippingActivity.FRAGMENT_JOB_ORDER_IMAGES);
            }
        });
    }
    public void setValues(){
        et_shopping.setText(jobOrder.getJobOrderForReturn().getShipping_no());
        et_shopping_note.setText(jobOrder.getJobOrderForReturn().getShipping_note());
    }
    public void saveShipping(String shipping,String shipping_no){
        jobOrder.setStatus_id(JobOrders.ACTION_FOR_RETURN);
        jobOrder.getJobOrderForReturn().setShipping_no(shipping);
        jobOrder.getJobOrderForReturn().setShipping_note(shipping_no);
        jobOrder.getJobOrderForReturn().setAccount_id(accounts.getId());
        jobOrder.getJobOrderForReturn().setDate_created(Utils.getServerDate(ls));
        jobOrder.getJobOrderForReturn().setDate_modified(Utils.getServerDate(ls));
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof ShippingFragmentListener){
            mListener = (ShippingFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }
    ShippingFragmentListener mListener;
    public interface ShippingFragmentListener{
        void switchFragment(int fragment);
        void onBackPressed();
    }
}
