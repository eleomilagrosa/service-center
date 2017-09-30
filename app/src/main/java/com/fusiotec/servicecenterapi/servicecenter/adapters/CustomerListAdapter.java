package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.CustomerInfoActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.JobOrderListActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ShippingActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;
import com.fusiotec.servicecenterapi.servicecenter.utilities.AnimatorUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;


/**
 * This is adapter Menu list
 * @author eleom
 * @author Eleojasmil Milagrosa
 * @version %I% %G%
 * @since 1.0
 */

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ViewHolder>{
    private RealmResults<Customers> customers;
    private Activity mContext;
    boolean isStillAnimating = false;
    public CustomerListAdapter(Activity c, RealmResults<Customers> customers){
        this.mContext = c;
        this.customers = customers;
        setChangeListener();
    }
    public void setData(RealmResults<Customers> customers){
        this.customers = customers;
        setChangeListener();
    }
    private void setChangeListener(){
        customers.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Customers>>() {
            @Override
            public void onChange(RealmResults<Customers> customers, OrderedCollectionChangeSet changeSet) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customers, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_customer,tv_mobile_number,tv_email,tv_address,btn_view,btn_edit;
        LinearLayout rl_options;
        RelativeLayout rl_view,rl_edit;
        public ViewHolder(View convertView){
            super(convertView);
            tv_customer = convertView.findViewById(R.id.tv_customer);
            tv_mobile_number = convertView.findViewById(R.id.tv_mobile_number);
            tv_email = convertView.findViewById(R.id.tv_email);
            tv_address = convertView.findViewById(R.id.tv_address);

            rl_options = convertView.findViewById(R.id.rl_options);
            btn_view = convertView.findViewById(R.id.btn_view);
            btn_edit = convertView.findViewById(R.id.btn_edit);

            rl_edit = convertView.findViewById(R.id.rl_edit);
            rl_view = convertView.findViewById(R.id.rl_view);
            btn_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, JobOrderListActivity.class);
                    intent.putExtra(JobOrderListActivity.SHOW,JobOrderListActivity.SHOW_JOB_ORDERS_BY_CUSTOMER);
                    intent.putExtra(JobOrderListActivity.CUSTOMER_ID,selected_customer.getId());
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }
            });
            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CustomerInfoActivity.class);
                    intent.putExtra(CustomerInfoActivity.SHOW_CUSTOMER_ID,selected_customer.getId());
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }
            });
            convertView.setOnClickListener(this);
        }
        Customers selected_customer;
        public void setCustomer(Customers selected_customer){
            this.selected_customer = selected_customer;
        }

        @Override
        public void onClick(final View view){
            if(!isStillAnimating){
                isStillAnimating = true;
                if(view.isSelected()){
                    List<Animator> animList = new ArrayList<>();
                    animList.add(createHideLeftItemAnimator(rl_view));
                    animList.add(createHideRightItemAnimator(rl_edit));
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.playTogether(animList);
                    animSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setSelected(false);
                            rl_options.setVisibility(View.INVISIBLE);
                            isStillAnimating = false;
                        }
                    });
                    animSet.start();
                }else{
                    rl_options.setVisibility(View.VISIBLE);
                    List<Animator> animList = new ArrayList<>();
                    animList.add(createShowLeftItemAnimator(rl_view));
                    animList.add(createShowRightItemAnimator(rl_edit));
                    AnimatorSet animSet = new AnimatorSet();
                    animSet.playTogether(animList);
                    animSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setSelected(true);
                            isStillAnimating = false;
                        }
                    });
                    animSet.start();
                }
            }
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        holder.rl_options.setVisibility(View.INVISIBLE);
        Customers customer = customers.get(position);
        holder.setCustomer(customer);
        holder.tv_customer.setText(customer.getLast_name() + ", " + customer.getFirst_name());
        holder.tv_mobile_number.setText(customer.getPhone_no());
        holder.tv_email.setText(customer.getEmail());
        holder.tv_address.setText(customer.getAddress());
    }

    float initial_left_distance = 0;
    private Animator createShowLeftItemAnimator(View item){
        if(initial_left_distance == 0){
            initial_left_distance = item.getX();
        }
        float dx =  (initial_left_distance) - (item.getWidth());

        item.setTranslationX(dx);
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item, AnimatorUtils.translationX(dx, 0f)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        return anim;
    }
    private Animator createHideLeftItemAnimator(View item){
        float dx =  initial_left_distance;

        item.setTranslationX(dx);
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item, AnimatorUtils.translationX(0f, (initial_left_distance) - (item.getWidth()))
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        return anim;
    }

    float initial_right_distance = 0;
    private Animator createShowRightItemAnimator(View item){
        if(initial_right_distance == 0){
            initial_right_distance = item.getX();
        }
        float dx =  (initial_right_distance) + (item.getWidth());

        item.setTranslationX(dx);
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,  AnimatorUtils.translationX(dx, 0f)
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        return anim;
    }
    private Animator createHideRightItemAnimator(View item){
        float dx =  initial_right_distance;

        item.setTranslationX(dx);
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item, AnimatorUtils.translationX(0f, (initial_right_distance) + (item.getWidth()))
        );

        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        return anim;
    }

}
