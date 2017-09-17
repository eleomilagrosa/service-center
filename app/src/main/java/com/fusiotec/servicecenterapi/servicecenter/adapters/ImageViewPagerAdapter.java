package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.customviews.TouchImageView;
import com.fusiotec.servicecenterapi.servicecenter.manager.ImageManager;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Constants;

import java.util.ArrayList;


/**
 * Created by Owner on 5/18/2016.
 */
public class ImageViewPagerAdapter extends PagerAdapter{

    private Activity mContext;
    ArrayList<JobOrderImages> images;
    boolean isZoomable;

    public ImageViewPagerAdapter(Activity mContext, ArrayList<JobOrderImages> images, boolean isZoomable) {
        this.mContext = mContext;
        this.images = images;
        this.isZoomable = isZoomable;
    }
    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
    @Override
    public Object instantiateItem(ViewGroup container,final int position){
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_image, container, false);
        JobOrderImages temp = images.get(position);
        TextView tv_title = itemView.findViewById(R.id.tv_title);
        tv_title.setText(temp.getLabel());
        TouchImageView imageView = itemView.findViewById(R.id.iv_menu_image);
        imageView.setIsZoombale(isZoomable);
        ImageManager.PicassoLoadThumbnail(mContext, Constants.webservice_address,temp.getImage(),imageView,R.drawable.add_picture);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((RelativeLayout) object);
    }
}
