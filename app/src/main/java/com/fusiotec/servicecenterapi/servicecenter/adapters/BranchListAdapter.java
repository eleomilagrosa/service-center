package com.fusiotec.servicecenterapi.servicecenter.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.RegisterBranchActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.RegistrationActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;

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

public class BranchListAdapter extends RecyclerView.Adapter<BranchListAdapter.ViewHolder>{
    private Activity mContext;
    RealmResults<Stations> stations;
    public BranchListAdapter(Activity c, RealmResults<Stations> stations){
        this.mContext = c;
        this.stations = stations;
        setChangeListener();
    }
    public void setData(RealmResults<Stations> stations){
        this.stations = stations;
        setChangeListener();
    }
    private void setChangeListener(){
        stations.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Stations>>() {
            @Override
            public void onChange(RealmResults<Stations> stations, OrderedCollectionChangeSet changeSet) {
                notifyDataSetChanged();
            }
        });
    }
    @Override
    public int getItemCount() {
        return stations.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branches, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_branch_name,tv_branch_prefix,tv_mobile_number,tv_address;
        public ViewHolder(View convertView){
            super(convertView);
            tv_branch_name = convertView.findViewById(R.id.tv_branch_name);
            tv_branch_prefix = convertView.findViewById(R.id.tv_branch_prefix);
            tv_mobile_number = convertView.findViewById(R.id.tv_mobile_number);
            tv_address = convertView.findViewById(R.id.tv_address);
            convertView.setOnClickListener(this);
        }
        Stations station;
        public void setBranch(Stations station){
            this.station = station;
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, RegisterBranchActivity.class);
            intent.putExtra(RegisterBranchActivity.BRANCH_ID,station.getId());
            mContext.startActivity(intent);
            mContext.overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        Stations station = stations.get(position);
        holder.setBranch(station);
        holder.tv_branch_name.setText(station.getStation_name());
        holder.tv_branch_prefix.setText(station.getStation_prefix());
        holder.tv_mobile_number.setText(station.getStation_number());
        holder.tv_address.setText(station.getStation_address());
    }
}
