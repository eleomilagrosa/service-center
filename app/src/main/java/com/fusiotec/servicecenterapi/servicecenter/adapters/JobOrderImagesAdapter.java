package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.manager.ImageManager;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Constants;

import java.util.ArrayList;


/**
 * This is adapter Menu list
 * @author eleom
 * @author Eleojasmil Milagrosa
 * @version %I% %G%
 * @since 1.0
 */

public class JobOrderImagesAdapter extends RecyclerView.Adapter<JobOrderImagesAdapter.ViewHolder>  {
    ArrayList<JobOrderImages> jobOrderImages = new ArrayList<>();
    Context mContext;

    public JobOrderImagesAdapter(Context c, ArrayList<JobOrderImages> jobOrderImages){
        this.mContext = c;
        this.jobOrderImages = jobOrderImages;
        try{
            listener = (JobOrderImagesAdapterListener) c;
        }catch(ClassCastException e){
            throw new ClassCastException(c.toString()
                    + " must implement OnAdapterInteractionListener");
        }
    }

    private JobOrderImagesAdapterListener listener;
    public interface JobOrderImagesAdapterListener{
        void addImage(JobOrderImages jobOrderImages);
    }

    @Override
    public int getItemCount() {
        return jobOrderImages.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_order_images, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        EditText et_title;
        ImageView iv_image;
        public ViewHolder(View convertView){
            super(convertView);
            et_title = (EditText) convertView.findViewById(R.id.et_title);
            iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        holder.et_title.setText(jobOrderImages.get(position).getLabel());
        holder.et_title.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
            }
            @Override
            public void afterTextChanged(Editable editable){
                jobOrderImages.get(holder.getAdapterPosition()).setLabel(editable.toString());
            }
        });
        ImageManager.PicassoLoadThumbnail(mContext, Constants.webservice_address,jobOrderImages.get(position).getImage(),holder.iv_image,R.drawable.add_picture);
        holder.iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.addImage(jobOrderImages.get(holder.getAdapterPosition()));
            }
        });
    }
}
