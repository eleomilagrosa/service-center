package com.fusiotec.servicecenterapi.servicecenter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.fusiotec.servicecenterapi.servicecenter.R;
import com.fusiotec.servicecenterapi.servicecenter.manager.LocalStorage;
import com.fusiotec.servicecenterapi.servicecenter.manager.PrintingManager;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Printers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Owner on 9/17/2017.
 */

public class PrinterSettingsActivity extends AppCompatActivity{

    final public static String TAG = PrinterSettingsActivity.class.getSimpleName();

    LocalStorage ls;
    Realm realm;
    RealmResults<Printers> printers;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ls = new LocalStorage(this);

        initViews();
        setValues();
        printers = realm.where(Printers.class).findAll();
        printers.addChangeListener(new RealmChangeListener<RealmResults<Printers>>() {
            @Override
            public void onChange(RealmResults<Printers> printers) {
                loadPrinters(printers);
            }
        });
        loadPrinters(printers);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    EditText et_printer,et_printer_port,et_printer_ip,et_printer_paper_size;
    AppCompatSpinner spinner_printer_list;
    public void initViews(){
        et_printer = (EditText) findViewById(R.id.et_printer);
        et_printer_port = (EditText) findViewById(R.id.et_printer_port);
        et_printer_ip = (EditText) findViewById(R.id.et_printer_ip);
        et_printer_paper_size = (EditText) findViewById(R.id.et_printer_paper_size);
        Button b_refresh = (Button) findViewById(R.id.b_refresh);
        Button b_test_print = (Button) findViewById(R.id.b_test_print);

        spinner_printer_list = (AppCompatSpinner) findViewById(R.id.spinner_printer_list);

        b_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPrinterPort = et_printer_port.getText().toString();
                ls.saveStringOnLocalStorage(LocalStorage.PRINTER_PORT, mPrinterPort);
                ls.saveStringOnLocalStorage(LocalStorage.PRINTER_IP, et_printer_ip.getText().toString());
                ls.saveStringOnLocalStorage(LocalStorage.PRINTER_KEY, et_printer.getText().toString());
                ls.saveStringOnLocalStorage(LocalStorage.PRINTER_PAPER_SIZE, et_printer_paper_size.getText().toString());
                requestPrinterList();
            }
        });
        b_test_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner_printer_list.getSelectedItem()!=null){
                    String mPrinterPort = et_printer_port.getText().toString();
                    ls.saveStringOnLocalStorage(LocalStorage.PRINTER_PORT, mPrinterPort);
                    ls.saveStringOnLocalStorage(LocalStorage.PRINTER_IP, et_printer_ip.getText().toString());
                    ls.saveStringOnLocalStorage(LocalStorage.PRINTER_KEY, et_printer.getText().toString());
                    ls.saveStringOnLocalStorage(LocalStorage.PRINTER_NAME, spinner_printer_list.getSelectedItem().toString());
                    ls.saveStringOnLocalStorage(LocalStorage.PRINTER_PAPER_SIZE, et_printer_paper_size.getText().toString());
                    connectPrinter(spinner_printer_list.getSelectedItem().toString(), PrintingManager.PROCESS_TEST_PRINT);
                }
            }
        });
    }
    public void setValues() {
        et_printer.setText(ls.getString(LocalStorage.PRINTER_KEY, "1234"));
        et_printer_port.setText(ls.getString(LocalStorage.PRINTER_PORT, "4"));
        et_printer_ip.setText(ls.getString(LocalStorage.PRINTER_IP, "192.168.0.1"));
        et_printer_paper_size.setText(ls.getString(LocalStorage.PRINTER_PAPER_SIZE, "48"));
    }
    public void requestPrinterList(){
        connectPrinter("", PrintingManager.PROCESS_PRINTER_LIST);
    }
    public void loadPrinters(RealmResults<Printers> printer_list){
        ArrayList<String> printers = new ArrayList<>();

        for(Printers temp:printer_list){
            printers.add(temp.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,R.layout.simple_spinner_lookup2, printers);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_lookup2);
        spinner_printer_list.setAdapter(dataAdapter);

        ArrayAdapter myAdap = (ArrayAdapter) spinner_printer_list.getAdapter();
        int spinnerPosition = myAdap.getPosition(ls.getString(LocalStorage.PRINTER_NAME, ""));
        spinner_printer_list.setSelection(spinnerPosition);
    }
    public void connectPrinter(String params, int process){
        Intent i = new Intent(this, PrintingManager.class);
        i.putExtra(PrintingManager.PARAMS, params);
        i.putExtra(PrintingManager.METHOD, PrintingManager.POST);
        i.putExtra(PrintingManager.SOURCE, TAG);
        i.putExtra(PrintingManager.PROCESS, process);
        startService(i);
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
