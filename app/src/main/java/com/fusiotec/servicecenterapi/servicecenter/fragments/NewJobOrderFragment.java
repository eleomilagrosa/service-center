package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.NewJobOrderActivity;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Owner on 8/8/2017.
 */

public class NewJobOrderFragment extends BaseFragment {
    View rootView;
    Button btn_next,btn_cancel;
    TextView tv_job_order_number;
    EditText et_unit,et_model,et_date,et_dealer,et_serial_number,et_complaint;
    AppCompatSpinner sp_warranty_label;
    String tbday;

    private int birthYearCustomerInfo, birthMonthCustomerInfo, birthDayCustomerInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_new_job_order, container, false);
        initUI();
        setValues();
        return rootView;
    }

    JobOrders jobOrder;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }
    public void initUI(){
        et_unit = rootView.findViewById(R.id.et_unit);
        et_model = rootView.findViewById(R.id.et_model);
        et_date = rootView.findViewById(R.id.et_date);
        et_dealer = rootView.findViewById(R.id.et_dealer);
        et_serial_number = rootView.findViewById(R.id.et_serial_number);
        sp_warranty_label = rootView.findViewById(R.id.sp_warranty_label);
        et_complaint = rootView.findViewById(R.id.et_complaint);

        et_serial_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    et_date.performClick();
                }
                return false;
            }
        });

        et_date.setKeyListener(null);
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                birthYearCustomerInfo = c.get(Calendar.YEAR);
                birthMonthCustomerInfo = c.get(Calendar.MONTH);
                birthDayCustomerInfo = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        String _birthMonthCustomerInfo = "";
                        if (monthOfYear < 10) {
                            _birthMonthCustomerInfo = "0" + (monthOfYear + 1);
                        } else {
                            _birthMonthCustomerInfo = "" + (monthOfYear + 1);
                        }
                        String _birthDayCustomerInfo = "";
                        if (dayOfMonth < 10) {
                            _birthDayCustomerInfo = "0" + dayOfMonth;
                        } else {
                            _birthDayCustomerInfo = "" + dayOfMonth;
                        }
                        try {
                            tbday = year + "-" + _birthMonthCustomerInfo + "-" + _birthDayCustomerInfo;
                            String formated_date = new SimpleDateFormat("MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(tbday));
                            et_date.setText(formated_date);
                        } catch (Exception e) {
                            Log.e("date parsing error", e.getMessage());
                            tbday = "";
                            et_date.setText("");
                        }
                        et_dealer.requestFocus();
                    }
                }, birthYearCustomerInfo, birthMonthCustomerInfo, birthDayCustomerInfo);
                dpd.show();
            }
        });

        tv_job_order_number = rootView.findViewById(R.id.tv_job_order_number);
        btn_cancel = rootView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.switchFragment(NewJobOrderActivity.FRAGMENT_CUSTOMER_INFO);
            }
        });
        btn_next = rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                boolean save = saveCustomer();
                if(save) mListener.switchFragment(NewJobOrderActivity.FRAGMENT_JOB_ORDER_IMAGES);
            }
        });
    }
    public void setValues(){
        tv_job_order_number.setText(jobOrder.getId());

        et_unit.setText(jobOrder.getUnit());
        et_model.setText(jobOrder.getModel());
        et_date.setText(Utils.dateToString(jobOrder.getDate_of_purchased(),"MMMM dd, yyyy"));
        et_dealer.setText(jobOrder.getDealer());
        et_serial_number.setText(jobOrder.getSerial_number());
        et_complaint.setText(jobOrder.getComplaint());

        if(jobOrder.getWarranty_label() != null){
            switch (jobOrder.getWarranty_label()){
                case "Intact":
                    sp_warranty_label.setSelection(0);
                    break;
                case "Tampered":
                    sp_warranty_label.setSelection(1);
                    break;
            }
        }else{
            sp_warranty_label.setSelection(0);
        }
    }


    public boolean saveCustomer(){
        boolean cancel = false;

        String unit = et_unit.getText().toString();
        String model = et_model.getText().toString();
        String date = et_date.getText().toString();
        String dealer = et_dealer.getText().toString();
        String searial_number = et_serial_number.getText().toString();
        String warranty_label = sp_warranty_label.getSelectedItem().toString();
        String complaint = et_complaint.getText().toString();

        if (TextUtils.isEmpty(unit)) {
            et_unit.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(model)) {
            et_model.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(date)) {
            et_date.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(dealer)) {
            et_dealer.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(searial_number)) {
            et_serial_number.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(complaint)) {
            et_complaint.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if(cancel){
            return false;
        }

        jobOrder.setUnit(unit);
        jobOrder.setModel(model);
        jobOrder.setDate_of_purchased(Utils.stringToDate(date,"MMMM d, yyyy"));
        jobOrder.setDealer(dealer);
        jobOrder.setSerial_number(searial_number);
        jobOrder.setWarranty_label(warranty_label);
        jobOrder.setComplaint(complaint);
        return true;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof NewJobOrderFragmentListener){
            mListener = (NewJobOrderFragmentListener) context;
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
    NewJobOrderFragmentListener mListener;
    public interface NewJobOrderFragmentListener {
        void switchFragment(int fragment);
    }

}
