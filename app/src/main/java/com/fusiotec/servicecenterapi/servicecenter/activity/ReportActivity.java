package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrderImages;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.JobOrders;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Stations;
import com.fusiotec.servicecenterapi.servicecenter.models.serialize_object.JobOrderSerialize;
import com.fusiotec.servicecenterapi.servicecenter.network.RetrofitRequestManager;
import com.fusiotec.servicecenterapi.servicecenter.utilities.Utils;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;



/**
 * Created by Owner on 10/1/2017.
 */

public class ReportActivity extends BaseActivity{
    DrawerLayout drawer;
    EditText et_date_start,et_date_end;
    AppCompatSpinner sp_branch;

    final public static int REQUEST_GET_JOB_ORDERS = 302;

    String tbday = "";
    private int birthYearCustomerInfo, birthMonthCustomerInfo, birthDayCustomerInfo;
    String tbday1 = "";
    private int birthYearCustomerInfo1, birthMonthCustomerInfo1, birthDayCustomerInfo1;


    PDFView pdf_viewer;
    public Activity getActivity(){
        return this;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initUI();
        if(accounts.isMainBranch()){
            defaultViewSpinner();
        }
        File file = new File(path_file);
        pdf_viewer.fromFile(file);
    }
    public void initUI(){
        Button btn_generate = (Button) findViewById(R.id.btn_generate);
        btn_generate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getJobOrders();
            }
        });

        pdf_viewer = (PDFView) findViewById(R.id.pdf_viewer);
        sp_branch = (AppCompatSpinner) findViewById(R.id.sp_branch);
        sp_branch.setVisibility(accounts.isMainBranch() ? View.VISIBLE : View.GONE);
        et_date_start = (EditText) findViewById(R.id.et_date_start);
        et_date_end = (EditText) findViewById(R.id.et_date_end);

        et_date_start.setFocusable(false);
        et_date_start.setClickable(true);
        et_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                birthYearCustomerInfo1 = c.get(Calendar.YEAR);
                birthMonthCustomerInfo1 = c.get(Calendar.MONTH);
                birthDayCustomerInfo1 = c.get(Calendar.DAY_OF_MONTH);

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
                            tbday1 = year + "-" + _birthMonthCustomerInfo + "-" + _birthDayCustomerInfo;
                            String formated_date = new SimpleDateFormat("MMMM dd, yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(tbday1));
                            et_date_start.setText(formated_date);
                        } catch (Exception e) {
                            Log.e("date parsing error", e.getMessage());
                            tbday1 = "";
                            et_date_start.setText("");
                        }
                    }
                }, birthYearCustomerInfo1, birthMonthCustomerInfo1, birthDayCustomerInfo1);
                dpd.show();
            }
        });

        et_date_end.setFocusable(false);
        et_date_end.setClickable(true);
        et_date_end.setOnClickListener(new View.OnClickListener() {
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
                            et_date_end.setText(formated_date);
                        } catch (Exception e) {
                            Log.e("date parsing error", e.getMessage());
                            tbday = "";
                            et_date_end.setText("");
                        }
                    }
                }, birthYearCustomerInfo, birthMonthCustomerInfo, birthDayCustomerInfo);
                dpd.show();
            }
        });
    }

    ArrayList<Stations> stations = new ArrayList<>();
    public void defaultViewSpinner(){
        stations.clear();
        ArrayList<String> sp_branch_populate = new ArrayList<>();
        stations.addAll(realm.copyFromRealm(realm.where(Stations.class).equalTo("is_deleted",0).findAllSorted("station_name")));
        sp_branch_populate.add("All");
        for(Stations temp:stations){
            sp_branch_populate.add(temp.getStation_name());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.simple_spinner_lookup2, sp_branch_populate);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_lookup2);
        sp_branch.setAdapter(dataAdapter);
    }

    public void setReceiver(String response,int process,int status){
        showProgress(false);
        switch (process){
            case REQUEST_GET_JOB_ORDERS:
                setJobOrder(response);
                break;
        }
    }
    public void getJobOrders(){
        et_date_end.setError(null);
        et_date_end.setError(null);
        if(tbday.isEmpty()){
            et_date_end.setError(getString(R.string.error_field_required));
            return;
        }
        if(tbday1.isEmpty()){
            et_date_start.setError(getString(R.string.error_field_required));
            return;
        }
        showProgress(true);
        int station_id = accounts.getStation_id();
        if(accounts.isMainBranch()){
            Stations station = realm.where(Stations.class).equalTo("station_name",sp_branch.getSelectedItem().toString()).findFirst();
            if(station != null){
                station_id = station.getId();
            }else{
                station_id = 0;
            }
        }
        requestManager.setRequestAsync(requestManager.getApiService().get_closed_job_order_reports(tbday1,tbday,station_id),REQUEST_GET_JOB_ORDERS);
    }
    public boolean setJobOrder(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getInt(RetrofitRequestManager.SUCCESS) == 1){
                JSONArray jsonArray = jsonObject.getJSONArray(JobOrders.TABLE_NAME);
                final ArrayList<JobOrders> jobOrders = new GsonBuilder()
                        .registerTypeAdapter(JobOrders.class,new JobOrderSerialize())
                        .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
                        .fromJson(jsonArray.toString(), new TypeToken<List<JobOrders>>(){}.getType());
                if(!jobOrders.isEmpty()){
                    realm.executeTransaction(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm){
                            realm.delete(JobOrders.class);
                            realm.copyToRealmOrUpdate(jobOrders);
                        }
                    });
                    createReport(realm.where(JobOrders.class).findAllSorted("date_time_closed", Sort.DESCENDING));
                }else{
                    return false;
                }
            }else{
                Toast.makeText(this, "No Available Results", Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    String path_file = Environment.getExternalStorageDirectory()+"/serviceapp/pdf/pdf_sample1.pdf";
    public void createReport(RealmResults<JobOrders> jobOrders) throws IOException, DocumentException{
        File file = new File(path_file);
        file.getParentFile().mkdirs();
        createPdf(jobOrders,file,path_file);
    }
    public void createPdf(RealmResults<JobOrders> jobOrders,File file,String dest) throws IOException, DocumentException {
        float left = 20;
        float right = 20;
        float top = 30;
        float bottom = 0;
        Document document = new Document(PageSize.A3, left, right, top, bottom);
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.BLACK);
        Paragraph preface = new Paragraph(18,"Closed Job Orders",font);
        preface.setAlignment(Element.ALIGN_CENTER);
        document.add(preface);
        document.add(new Chunk("\n"));

        PdfPTable table = new PdfPTable(accounts.isMainBranch() ? 7 : 6);
        table.setTotalWidth(PageSize.A3.getWidth() - 40);
        table.setLockedWidth(true);
        Font font_header = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.BOLD, BaseColor.BLACK);
        table.addCell(createHeaderCell("Job Order #",font_header));
        table.addCell(createHeaderCell("Serial",font_header));
        table.addCell(createHeaderCell("Diagnosis",font_header));
        table.addCell(createHeaderCell("Status",font_header));
        table.addCell(createHeaderCell("Date Create",font_header));
        table.addCell(createHeaderCell("Date Closed",font_header));
        if(accounts.isMainBranch()) table.addCell(createHeaderCell("Branch",font_header));

        Font font_content = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.NORMAL, BaseColor.BLACK);
        for(JobOrders temp : jobOrders){
            table.addCell(createHeaderCell(temp.getId(),font_content));
            table.addCell(createHeaderCell(temp.getSerial_number(),font_content));
            table.addCell(createHeaderCell(temp.getJobOrderDiagnosis().getDiagnosis(),font_content));
            table.addCell(createHeaderCell(getStatus(temp.getRepair_status()),font_content));
            table.addCell(createHeaderCell(Utils.dateToString(temp.getDate_created(),"MM/dd/yyyy"),font_content));
            table.addCell(createHeaderCell(Utils.dateToString(temp.getDate_time_closed(),"MM/dd/yyyy"),font_content));
            if(accounts.isMainBranch()) table.addCell(createHeaderCell(getBranch(temp.getStation_id()),font_content));
        }
        document.add(table);
        document.close();
        pdf_viewer.fromFile(file).load();
    }
    private PdfPCell createHeaderCell(String string,Font font_header){
        PdfPCell pcell = new PdfPCell(new Phrase(string,font_header));
        pcell.setMinimumHeight(50);
        pcell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        pcell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
        return pcell;
    }
    
    public String getStatus(int status_id){
        switch (status_id){
            case 1:
                return ("PENDING");
            case 2:
                return ("REPAIRED");
            case 3:
                return ("PULLED-OUT");
        }
        return "";
    }
    public String getBranch(int branch_id){
        Stations station = realm.where(Stations.class).equalTo("id",branch_id).findFirst();
        return station != null ? station.getStation_name() : "";
    }
}
