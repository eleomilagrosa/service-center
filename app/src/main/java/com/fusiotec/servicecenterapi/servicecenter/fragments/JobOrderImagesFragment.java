package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.ClosedActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.DiagnosisActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ForPickUpActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ForReturnActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.NewJobOrderActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.RepairStatusActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ShippingActivity;
import com.fusiotec.servicecenterapi.servicecenter.adapters.JobOrderImagesAdapter;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

/**
 * Created by Owner on 8/8/2017.
 */

public class JobOrderImagesFragment extends BaseFragment{
    View rootView;
    Button btn_next,btn_cancel;
    JobOrderImagesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_job_order_images, container, false);
        initUI();
        return rootView;
    }

    JobOrders jobOrder;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }
    public void addTempHolder(){
        jobOrder.getJobOrderImageslist().add(new JobOrderImages());
    }
    public void refreshImageList(){
        adapter.notifyDataSetChanged();
    }
    public void initUI(){
        RecyclerView rv_images = rootView.findViewById(R.id.rv_images);
        rv_images.setLayoutManager(new GridLayoutManager(getActivity(),2));
        adapter = new JobOrderImagesAdapter(getActivity(),jobOrder.getJobOrderImageslist());
        rv_images.setAdapter(adapter);

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
                if(jobOrder.getJobOrderImageslist().size() > 1){
                    switch (jobOrder.getStatus_id()){
                        case JobOrders.ACTION_PROCESSING:
                            mListener.switchFragment(NewJobOrderActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            break;
                        case JobOrders.ACTION_DIAGNOSED:
                            mListener.switchFragment(DiagnosisActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            break;
                        case JobOrders.ACTION_FORWARDED:
                            mListener.switchFragment(ShippingActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            break;
                        case JobOrders.ACTION_RECEIVE_AT_MAIN:
                            mListener.switchFragment(RepairStatusActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            break;
                        case JobOrders.ACTION_FOR_RETURN:
                            mListener.switchFragment(ForReturnActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            break;
                        case JobOrders.ACTION_RECEIVE_AT_SC:
                            if(getActivity() instanceof ForPickUpActivity){
                                jobOrder.setStatus_id(JobOrders.ACTION_PICK_UP);
                                mListener.switchFragment(ForPickUpActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            }
                            break;
                        case JobOrders.ACTION_PICK_UP:
                            if(getActivity() instanceof ClosedActivity){
                                jobOrder.setStatus_id(JobOrders.ACTION_CLOSED);
                                mListener.switchFragment(ClosedActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            }else if(getActivity() instanceof ForPickUpActivity){
                                mListener.switchFragment(ForPickUpActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            }
                            break;
                        case JobOrders.ACTION_CLOSED:
                            mListener.switchFragment(ClosedActivity.FRAGMENT_JOB_ORDER_SUMMARY);
                            break;
                    }
                }else{
                    Utils.errorMessage(getActivity(), "Attach at least 1 image", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                }
            }
        });
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof JobOrderImagesFragmentListener){
            mListener = (JobOrderImagesFragmentListener) context;
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
    JobOrderImagesFragmentListener mListener;
    public interface JobOrderImagesFragmentListener {
        void switchFragment(int fragment);
        void onBackPressed();
    }

}
