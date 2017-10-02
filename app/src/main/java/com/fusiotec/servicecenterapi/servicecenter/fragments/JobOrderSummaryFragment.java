package com.fusiotec.servicecenterapi.servicecenter.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.activity.ClosedActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.DiagnosisActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ForPickUpActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ForReturnActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.NewJobOrderActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.RepairStatusActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ShippingActivity;
import com.fusiotec.servicecenterapi.servicecenter.activity.ViewJobOrderActivity;
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.manager.PrintingManager;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Accounts;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.utilities.BarCodeUtils;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Owner on 8/8/2017.
 */

public class JobOrderSummaryFragment extends BaseFragment {
    final public static String TAG = JobOrderSummaryFragment.class.getSimpleName();

    View rootView;
    Button btn_save,btn_cancel;
    TextView tv_job_order_number,tv_name,
            tv_mobile_number,tv_email,tv_unit,
            tv_date,tv_dealer,tv_serial,
            tv_model,tv_warranty,tv_complaint,
            tv_status,tv_diagnosis,tv_shipping,tv_show_images,tv_repair_status;
    ImageView iv_barcode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_job_order_summary, container, false);
        initUI();
        if(jobOrder != null){
            setValues();
        }else{
            btn_save.setVisibility(View.GONE);
        }
        return rootView;
    }

    JobOrders jobOrder;
    public void setJobOrder(JobOrders jobOrder){
        this.jobOrder = jobOrder;
    }
    public void initUI(){
        iv_barcode = rootView.findViewById(R.id.iv_barcode);
        tv_job_order_number = rootView.findViewById(R.id.tv_job_order_number);
        tv_name = rootView.findViewById(R.id.tv_name);
        tv_mobile_number = rootView.findViewById(R.id.tv_mobile_number);
        tv_email = rootView.findViewById(R.id.tv_email);
        tv_unit = rootView.findViewById(R.id.tv_unit);
        tv_date = rootView.findViewById(R.id.tv_date);
        tv_dealer = rootView.findViewById(R.id.tv_dealer);
        tv_serial = rootView.findViewById(R.id.tv_serial);
        tv_model = rootView.findViewById(R.id.tv_model);
        tv_warranty = rootView.findViewById(R.id.tv_warranty);
        tv_complaint = rootView.findViewById(R.id.tv_complaint);
        tv_status = rootView.findViewById(R.id.tv_status);
        tv_repair_status = rootView.findViewById(R.id.tv_repair_status);
        tv_diagnosis = rootView.findViewById(R.id.tv_diagnosis);
        tv_shipping = rootView.findViewById(R.id.tv_shipping);
        tv_show_images = rootView.findViewById(R.id.tv_show_images);

        tv_show_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(jobOrder != null){
                    if(jobOrder.getJobOrderImages().size() != 0 || jobOrder.getJobOrderImageslist().size()-1 != 0){
                        if(getActivity() instanceof NewJobOrderActivity){
                            mListener.switchFragment(NewJobOrderActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof DiagnosisActivity){
                            mListener.switchFragment(DiagnosisActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof ShippingActivity){
                            mListener.switchFragment(ShippingActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof ViewJobOrderActivity){
                            mListener.switchFragment(ViewJobOrderActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof RepairStatusActivity){
                            mListener.switchFragment(RepairStatusActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof ForReturnActivity){
                            mListener.switchFragment(ForReturnActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof ForPickUpActivity){
                            mListener.switchFragment(ForReturnActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }else if(getActivity() instanceof ClosedActivity){
                            mListener.switchFragment(ClosedActivity.FRAGMENT_JOB_ORDER_VIEW_IMGAGES);
                        }
                    }else{
                        Utils.errorMessage(getActivity(), "No Images Available", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i){
                            }
                        });
                    }
                }

            }
        });
        Button btn_print = rootView.findViewById(R.id.btn_print);
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectPrinter(jobOrder,PrintingManager.PROCESS_OPEN_PRINTER);
            }
        });

        btn_cancel = rootView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mListener.onBackPressed();
            }
        });
        btn_save = rootView.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switch (jobOrder.getStatus_id()){
                    case JobOrders.ACTION_PROCESSING:
                        if(getActivity() instanceof DiagnosisActivity){
                            mListener.switchFragment(DiagnosisActivity.FRAGMENT_JOB_ORDER_DIAGNOSIS);
                        }else if(getActivity() instanceof NewJobOrderActivity){
                            mListener.save();
                        }
                        break;
                    case JobOrders.ACTION_DIAGNOSED:
                        if(getActivity() instanceof DiagnosisActivity){
                            mListener.save();
                        }else if(getActivity() instanceof ShippingActivity){
                            mListener.switchFragment(ShippingActivity.FRAGMENT_JOB_ORDER_SHIPPING);
                        }
                        break;
                    case JobOrders.ACTION_FORWARDED:
                        if(mListener.getCurrentJobOrderStatus() == ViewJobOrderActivity.RECEIVED_IN_MAIN){
                            mListener.save();
                        }else{
                            mListener.save();
                        }
                        break;
                    case JobOrders.ACTION_RECEIVE_AT_MAIN:
                        if(getActivity() instanceof RepairStatusActivity){
                            if(jobOrder.getJobOrderRepairStatus().getRepair_status() > 1){
                                mListener.save();
                            }else{
                                mListener.switchFragment(RepairStatusActivity.FRAGMENT_JOB_ORDER_REPAIR_STATUS);
                            }
                        }else{
                            if(getActivity() instanceof ForReturnActivity){
                                mListener.switchFragment(ForReturnActivity.FRAGMENT_JOB_ORDER_SHIPPING);
                            }
                        }
                        break;
                    case JobOrders.ACTION_FOR_RETURN:
                        if(getActivity() instanceof ForReturnActivity){
                            mListener.save();
                        }else if(getActivity() instanceof ViewJobOrderActivity){
                            mListener.save();
                        }
                        break;
                    case JobOrders.ACTION_RECEIVE_AT_SC:
                        if(getActivity() instanceof ForPickUpActivity){
                            mListener.switchFragment(ForPickUpActivity.FRAGMENT_JOB_ORDER_IMAGES);
                        }
                        break;
                    case JobOrders.ACTION_PICK_UP:
                        if(getActivity() instanceof ForPickUpActivity){
                            mListener.save();
                        }else if(getActivity() instanceof ClosedActivity){
                            mListener.switchFragment(ClosedActivity.FRAGMENT_JOB_ORDER_IMAGES);
                        }
                        break;
                    case JobOrders.ACTION_CLOSED:
                        if(getActivity() instanceof ClosedActivity){
                            mListener.save();
                        }
                        break;
                }
            }
        });
    }
    public void setValues(){
        btn_save.setVisibility(View.VISIBLE);

        tv_job_order_number.setText(jobOrder.getId());
        tv_name.setText(jobOrder.getCustomer().getLast_name()+", " +jobOrder.getCustomer().getFirst_name());
        tv_mobile_number.setText(jobOrder.getCustomer().getPhone_no());
        tv_email.setText(jobOrder.getCustomer().getEmail());
        tv_unit.setText(jobOrder.getUnit());
        tv_date.setText(Utils.dateToString(jobOrder.getDate_of_purchased(),"MMMM dd, yyyy"));
        tv_dealer.setText(jobOrder.getDealer());
        tv_serial.setText(jobOrder.getSerial_number());
        tv_model.setText(jobOrder.getModel());
        tv_warranty.setText(jobOrder.getWarranty_label());
        tv_complaint.setText(jobOrder.getComplaint());

        switch (jobOrder.getStatus_id()){
            case JobOrders.ACTION_PROCESSING:
                tv_status.setText("PROCESSING");
                break;
            case JobOrders.ACTION_DIAGNOSED:
                tv_status.setText("DIAGNOSED");
                break;
            case JobOrders.ACTION_FORWARDED:
                tv_status.setText("FORWARDED");
                break;
            case JobOrders.ACTION_RECEIVE_AT_MAIN:
                tv_status.setText("RECEIVE AT MAIN");
                break;
            case JobOrders.ACTION_FOR_RETURN:
                tv_status.setText("FOR RETURN");
                break;
            case JobOrders.ACTION_RECEIVE_AT_SC:
                tv_status.setText("RECEIVE AT SC");
                break;
            case JobOrders.ACTION_PICK_UP:
                tv_status.setText("FOR PICK UP");
                break;
            case JobOrders.ACTION_CLOSED:
                tv_status.setText("CLOSED");
                break;
            default:
                tv_status.setText("N/A");
                break;
        }
        switch (jobOrder.getRepair_status()){
            case 1:
                tv_repair_status.setText("PENDING");
                break;
            case 2:
                tv_repair_status.setText("REPAIRED");
                break;
            case 3:
                tv_repair_status.setText("PULLED OUT");
                break;
            default:
                tv_repair_status.setText("N/A");
                break;
        }
        tv_diagnosis.setText(jobOrder.getStatus_id() >= JobOrders.ACTION_DIAGNOSED ? jobOrder.getJobOrderDiagnosis().getDiagnosis() : "N/A");
        tv_shipping.setText(jobOrder.getStatus_id() >= JobOrders.ACTION_FORWARDED ?
                jobOrder.getJobOrderShipping().getShipping_no()+"-"+jobOrder.getJobOrderShipping().getShipping_note() : "NO");

        switch (jobOrder.getStatus_id()){
            case JobOrders.ACTION_PROCESSING:
                if(getActivity() instanceof NewJobOrderActivity){
                    if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
                        btn_save.setText("Save");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }else if(getActivity() instanceof DiagnosisActivity){
                    if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
                        btn_save.setText("Diagnose");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }else{
                    btn_save.setVisibility(View.GONE);
                }
                break;
            case JobOrders.ACTION_DIAGNOSED:
                if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
                    if(getActivity() instanceof DiagnosisActivity){
                        btn_save.setText("Save");
                    }else if(getActivity() instanceof ShippingActivity){
                        btn_save.setText("Shipping");
                    }
                }else{
                    btn_save.setVisibility(View.GONE);
                }
                break;
            case JobOrders.ACTION_FORWARDED:
                if(getActivity() instanceof ViewJobOrderActivity) {
                    if(mListener.getCurrentJobOrderStatus() == ViewJobOrderActivity.RECEIVED_IN_MAIN){
                        btn_save.setText("Receive");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }else{
                    btn_save.setText("Save");
                }
                break;
            case JobOrders.ACTION_RECEIVE_AT_MAIN:
                if(getActivity() instanceof RepairStatusActivity){
                    if(jobOrder.getJobOrderRepairStatus().getRepair_status() > 1){
                        btn_save.setText("Save");
                    }else{
                        btn_save.setText("Repair Status");
                    }
                }else{
                    if(getActivity() instanceof ForReturnActivity){
                        btn_save.setText("For Return");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }
                break;
            case JobOrders.ACTION_FOR_RETURN:
                if(getActivity() instanceof ViewJobOrderActivity){
                    if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
                        btn_save.setText("Receive");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }else{
                    if(getActivity() instanceof ForReturnActivity){
                        btn_save.setText("Save");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }
            case JobOrders.ACTION_RECEIVE_AT_SC:
                if(getActivity() instanceof ForPickUpActivity){
                    if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
                        btn_save.setText("For Pick Up");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }else{
                    btn_save.setVisibility(View.GONE);
                }
                break;
            case JobOrders.ACTION_PICK_UP:
                if(getActivity() instanceof ClosedActivity){
                    if(accounts.getAccount_type_id() == Accounts.SERVICE_CENTER){
                        btn_save.setText("Close Job Order");
                    }else{
                        btn_save.setVisibility(View.GONE);
                    }
                }else{
                    btn_save.setVisibility(View.GONE);
                }
                break;
            case JobOrders.ACTION_CLOSED:
                if(getActivity() instanceof ClosedActivity){
                    btn_save.setText("Save");
                }else if(getActivity() instanceof ViewJobOrderActivity){
                    btn_save.setVisibility(View.GONE);
                }else{
                    btn_save.setVisibility(View.GONE);
                }
                break;
        }
        setBarcodeImage(jobOrder.getId());
    }
    public void setBarcodeImage(String barcode){
        try{
            Bitmap bitmap = BarCodeUtils.encodeAsBitmap(barcode, BarcodeFormat.ITF, 400, 100);
            iv_barcode.setImageBitmap(bitmap);
        }catch(WriterException e){
            Log.e("Barcode Image",""+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof JobOrderSummaryFragmentListener){
            mListener = (JobOrderSummaryFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }
    JobOrderSummaryFragmentListener mListener;
    public interface JobOrderSummaryFragmentListener{
        void switchFragment(int fragment);
        int getCurrentJobOrderStatus();
        void onBackPressed();
        void save();
    }

    public void connectPrinter(JobOrders jobOrder, int process){
        Intent i = new Intent(getActivity(), PrintingManager.class);
        i.putExtra(PrintingManager.METHOD, PrintingManager.POST);
        i.putExtra(PrintingManager.ARRAY_STRING,createArrayFromJobOrder(jobOrder));
        i.putExtra(PrintingManager.SOURCE, TAG);
        i.putExtra(PrintingManager.PROCESS, process);
        getActivity().startService(i);
    }
    public ArrayList<String> createArrayFromJobOrder(JobOrders jobOrder){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(jobOrder.getId());
        arrayList.add(jobOrder.getDealer());
        arrayList.add(Utils.dateToString(jobOrder.getDate_created() == null ? Utils.getServerDate(ls) : jobOrder.getDate_created(),"yyyy-MM-dd HH:mm:ss"));
        arrayList.add(jobOrder.getUnit());
        arrayList.add(jobOrder.getModel());
        arrayList.add(jobOrder.getSerial_number());
        arrayList.add(jobOrder.getWarranty_label());
        arrayList.add(jobOrder.getComplaint());
        arrayList.add(jobOrder.getCustomer().getLast_name()+", "+jobOrder.getCustomer().getFirst_name());
        arrayList.add(jobOrder.getCustomer().getPhone_no());
        return arrayList;
    }
}
