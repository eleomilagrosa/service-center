package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.ClosedActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.DiagnosisActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ForPickUpActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ForReturnActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.RepairStatusActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ShippingActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ViewJobOrderActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

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

public class JobOrdersAdapter extends RecyclerView.Adapter<JobOrdersAdapter.ViewHolder>{
    private RealmResults<JobOrders> jobOrders;
    private Activity mContext;
    private Accounts accounts;
    public JobOrdersAdapter(Activity c, RealmResults<JobOrders> jobOrders){
        this.mContext = c;
        this.jobOrders = jobOrders;
        setChangeListener();
    }
    public void setData(RealmResults<JobOrders> jobOrders){
        this.jobOrders = jobOrders;
        setChangeListener();
    }
    private void setChangeListener(){
        jobOrders.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<JobOrders>>() {
            @Override
            public void onChange(RealmResults<JobOrders> jobOrderses, OrderedCollectionChangeSet changeSet) {
                notifyDataSetChanged();
            }
        });
    }
    public void setAccounts(Accounts accounts){
        this.accounts = accounts;
    }

    @Override
    public int getItemCount() {
        return jobOrders.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_orders, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_job_order_number,tv_customer,tv_unit,tv_model,tv_status,tv_created;
        public ViewHolder(View convertView){
            super(convertView);
            tv_job_order_number = convertView.findViewById(R.id.tv_job_order_number);
            tv_customer = convertView.findViewById(R.id.tv_customer);
            tv_unit = convertView.findViewById(R.id.tv_unit);
            tv_model = convertView.findViewById(R.id.tv_model);
            tv_status = convertView.findViewById(R.id.tv_status);
            tv_created = convertView.findViewById(R.id.tv_created);
            convertView.setOnClickListener(this);
        }
        JobOrders jobOrder;
        public void setJobOrder(JobOrders jobOrder){
            this.jobOrder = jobOrder;
        }
        @Override
        public void onClick(View view){
            if(accounts.getAccount_type_id() == 1){
                if(jobOrder.getStatus_id() == JobOrders.ACTION_PROCESSING){
                    startIntentActivity(new Intent(mContext, DiagnosisActivity.class));
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_DIAGNOSED){
                    startIntentActivity(new Intent(mContext, ShippingActivity.class));
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_FORWARDED ||
                        jobOrder.getStatus_id() == JobOrders.ACTION_RECEIVE_AT_MAIN||
                        jobOrder.getStatus_id() == JobOrders.ACTION_FOR_RETURN ||
                        jobOrder.getStatus_id() == JobOrders.ACTION_CLOSED){
                    startIntentActivity(new Intent(mContext, ViewJobOrderActivity.class));
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_RECEIVE_AT_SC){
                    startIntentActivity(new Intent(mContext, ForPickUpActivity.class));
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_PICK_UP){
                    startIntentActivity(new Intent(mContext, ClosedActivity.class));
                }
            }else if(accounts.getAccount_type_id() == 2){
                if(jobOrder.getStatus_id() == JobOrders.ACTION_FORWARDED){
                    startIntentActivity(new Intent(mContext, ViewJobOrderActivity.class));
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_RECEIVE_AT_MAIN){
                    if(jobOrder.getRepair_status() > 1){
                        startIntentActivity(new Intent(mContext, ForReturnActivity.class));
                    }else{
                        startIntentActivity(new Intent(mContext, RepairStatusActivity.class));
                    }
                }else if(jobOrder.getStatus_id() == JobOrders.ACTION_FOR_RETURN||
                        jobOrder.getStatus_id() == JobOrders.ACTION_PROCESSING ||
                        jobOrder.getStatus_id() == JobOrders.ACTION_DIAGNOSED||
                        jobOrder.getStatus_id() == JobOrders.ACTION_PICK_UP){
                    startIntentActivity(new Intent(mContext, ViewJobOrderActivity.class));
                }
            }else if(accounts.getAccount_type_id() == 3){
                startIntentActivity(new Intent(mContext, ShippingActivity.class));
            }
        }
        private void startIntentActivity(Intent in){
            in.putExtra(JobOrders.JOB_ORDER_ID,jobOrder.getId());
            mContext.startActivity(in);
            mContext.overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        JobOrders jobOrder = jobOrders.get(position);
        holder.setJobOrder(jobOrder);
        holder.tv_job_order_number.setText(jobOrder.getId());
        holder.tv_customer.setText(jobOrder.getCustomer().getLast_name()+", " +jobOrder.getCustomer().getFirst_name());
        holder.tv_unit.setText(jobOrder.getUnit());
        holder.tv_model.setText(jobOrder.getModel());
        holder.tv_created.setText(Utils.dateToString(jobOrder.getDate_created(),"MMMM dd, yyyy"));

        switch (jobOrder.getStatus_id()){
            case JobOrders.ACTION_PROCESSING:
                holder.tv_status.setText("PROCCESSING");
                break;
            case JobOrders.ACTION_DIAGNOSED:
                holder.tv_status.setText("DIAGNOSED");
                break;
            case JobOrders.ACTION_FORWARDED:
                holder.tv_status.setText("FORWARDED");
                break;
            case JobOrders.ACTION_RECEIVE_AT_MAIN:
                switch (jobOrder.getRepair_status()){
                    case 1:
                        holder.tv_status.setText("RECEIVE AT MAIN - PENDING");
                        break;
                    case 2:
                        holder.tv_status.setText("RECEIVE AT MAIN  - REPAIRED");
                        break;
                    case 3:
                        holder.tv_status.setText("RECEIVE AT MAIN - GIVEN UP");
                        break;
                }
                break;
            case JobOrders.ACTION_FOR_RETURN:
                holder.tv_status.setText("FOR RETURN");
                break;
            case JobOrders.ACTION_RECEIVE_AT_SC:
                holder.tv_status.setText("RECEIVE AT SC");
                break;
            case JobOrders.ACTION_PICK_UP:
                holder.tv_status.setText("For Pick Up");
                break;
            case JobOrders.ACTION_CLOSED:
                holder.tv_status.setText("CLOSED");
                break;
            default:
                holder.tv_status.setText("N/A");
                break;
        }
    }
}
