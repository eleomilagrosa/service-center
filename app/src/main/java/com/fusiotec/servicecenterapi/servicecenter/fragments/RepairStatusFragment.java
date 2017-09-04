package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
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

public class RepairStatusFragment extends BaseFragment{

    View rootView;
    Button btn_next,btn_cancel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_repair, container, false);

        initUI();
        setValues();
        return rootView;
    }
    JobOrders jobOrder;
    EditText et_repair_note;
    AppCompatSpinner sp_repair_status;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }

    public void initUI(){
        et_repair_note = rootView.findViewById(R.id.et_repair_note);
        sp_repair_status = rootView.findViewById(R.id.sp_repair_status);
        btn_cancel = rootView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.onBackPressed();
            }
        });
        btn_next = rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                save(sp_repair_status.getSelectedItemPosition() == 0 ? 2 : 3,et_repair_note.getText().toString());
                mListener.switchFragment(ShippingActivity.FRAGMENT_JOB_ORDER_IMAGES);
            }
        });
    }
    public void setValues(){
        et_repair_note.setText(jobOrder.getJobOrderRepairStatus().getRepair_note());
        switch (jobOrder.getJobOrderRepairStatus().getRepair_status()){
            case 2:
                sp_repair_status.setSelection(0);
                break;
            case 3:
                sp_repair_status.setSelection(1);
                break;
        }
    }
    public void save(int repair_status, String repair_notes){
        jobOrder.setRepair_status(repair_status);
        jobOrder.getJobOrderRepairStatus().setRepair_status(repair_status);
        jobOrder.getJobOrderRepairStatus().setRepair_note(repair_notes);
        jobOrder.getJobOrderRepairStatus().setAccount_id(accounts.getId());
        jobOrder.getJobOrderRepairStatus().setDate_created(Utils.getServerDate(ls));
        jobOrder.getJobOrderRepairStatus().setDate_modified(Utils.getServerDate(ls));
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof RepairStatusFragmentListener){
            mListener = (RepairStatusFragmentListener) context;
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
    RepairStatusFragmentListener mListener;
    public interface RepairStatusFragmentListener{
        void switchFragment(int fragment);
        void onBackPressed();
    }
}
