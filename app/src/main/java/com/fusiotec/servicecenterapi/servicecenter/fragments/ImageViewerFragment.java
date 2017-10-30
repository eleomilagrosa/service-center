package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.adapters.ImageViewPagerAdapter;
import com.fusiotec.servicecenterapi.servicecenter.customviews.CirclePageIndicator;
import com.fusiotec.servicecenterapi.servicecenter.customviews.HackyViewPager;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;

import java.util.ArrayList;


/**
 * Created by Owner on 5/26/2016.
 */
public class ImageViewerFragment extends BaseFragment {

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        initUI();
        return rootView;
    }
    JobOrders jobOrder;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }
    public void initUI(){
        ArrayList<JobOrderImages> image = new ArrayList<>();
        image.addAll(jobOrder.getJobOrderImageslist());
        if(!image.isEmpty()) image.remove(jobOrder.getJobOrderImageslist().size() - 1);
        image.addAll(jobOrder.getJobOrderImages());

        ImageViewPagerAdapter menuViewPagerAdapter = new ImageViewPagerAdapter(getActivity(),image,true);
        HackyViewPager mPager = rootView.findViewById(R.id.pager);
        mPager.setAdapter(menuViewPagerAdapter);

        CirclePageIndicator mIndicator = rootView.findViewById(R.id.indicator);
        if(image.size() > 1){
            mIndicator.setViewPager(mPager);
        }else{
            mIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof ImageViewerActivityListener){
            mListener = (ImageViewerActivityListener) context;
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
    ImageViewerActivityListener mListener;
    public interface ImageViewerActivityListener {
        void switchFragment(int fragment);
    }
}
