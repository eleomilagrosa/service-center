package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.DiagnosisActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

/**
 * Created by Owner on 8/13/2017.
 */

public class DiagnosisFragment extends BaseFragment{

    View rootView;
    Button btn_next,btn_cancel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_job_order_diagnosis, container, false);

        initUI();
        setValues();
        return rootView;
    }
    JobOrders jobOrder;
    EditText et_diagnosis;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }

    public void initUI(){
        et_diagnosis = rootView.findViewById(R.id.et_diagnosis);
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
                saveDiagnosis(et_diagnosis.getText().toString());
                mListener.switchFragment(DiagnosisActivity.FRAGMENT_JOB_ORDER_IMAGES);
            }
        });
    }
    public void setValues(){
        et_diagnosis.setText(jobOrder.getJobOrderDiagnosis().getDiagnosis());
    }
    public void saveDiagnosis(String diagnosis){
        jobOrder.setStatus_id(JobOrders.ACTION_DIAGNOSED);
        jobOrder.getJobOrderDiagnosis().setDiagnosis(diagnosis);
        jobOrder.getJobOrderDiagnosis().setAccount_id(accounts.getId());
        jobOrder.getJobOrderDiagnosis().setDate_created(Utils.getServerDate(ls));
        jobOrder.getJobOrderDiagnosis().setDate_modified(Utils.getServerDate(ls));
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof DiagnosisFragmentListener){
            mListener = (DiagnosisFragmentListener) context;
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
    DiagnosisFragmentListener mListener;
    public interface DiagnosisFragmentListener{
        void switchFragment(int fragment);
        void onBackPressed();
    }
}
