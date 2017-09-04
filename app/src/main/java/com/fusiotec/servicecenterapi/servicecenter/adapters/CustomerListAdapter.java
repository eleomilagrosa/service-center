package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.JobOrderListActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ShippingActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Customers;

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
        TextView tv_customer,tv_mobile_number,tv_email,tv_address;
        public ViewHolder(View convertView){
            super(convertView);
            tv_customer = convertView.findViewById(R.id.tv_customer);
            tv_mobile_number = convertView.findViewById(R.id.tv_mobile_number);
            tv_email = convertView.findViewById(R.id.tv_email);
            tv_address = convertView.findViewById(R.id.tv_address);
            convertView.setOnClickListener(this);
        }
        Customers selected_customer;
        public void setCustomer(Customers selected_customer){
            this.selected_customer = selected_customer;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, JobOrderListActivity.class);
            intent.putExtra(JobOrderListActivity.SHOW,JobOrderListActivity.SHOW_JOB_ORDERS_BY_CUSTOMER);
            intent.putExtra(JobOrderListActivity.CUSTOMER_ID,selected_customer.getId());
            mContext.startActivity(intent);
            mContext.overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        Customers customer = customers.get(position);
        holder.setCustomer(customer);
        holder.tv_customer.setText(customer.getLast_name() + ", " + customer.getFirst_name());
        holder.tv_mobile_number.setText(customer.getPhone_no());
        holder.tv_email.setText(customer.getEmail());
        holder.tv_address.setText(customer.getAddress());
    }
}
