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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Owner on 9/17/2017.
 */

public class PrinterSettingsActivity extends AppCompatActivity{

    final public static String TAG = PrinterSettingsActivity.class.getSimpleName();

    LocalStorage ls;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ls = new LocalStorage(this, new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals(LocalStorage.PRINTER_LIST)){
                    Log.e("changes","----");
                    loadPrinters();
                }
            }
        });

        initViews();
        setValues();
        loadPrinters();
    }
    EditText et_printer,et_printer_port,et_printer_ip;
    AppCompatSpinner spinner_printer_list;
    public void initViews(){
        et_printer = (EditText) findViewById(R.id.et_printer);
        et_printer_port = (EditText) findViewById(R.id.et_printer_port);
        et_printer_ip = (EditText) findViewById(R.id.et_printer_ip);
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
                    connectPrinter(spinner_printer_list.getSelectedItem().toString(), PrintingManager.PROCESS_TEST_PRINT);
                }
            }
        });
    }
    public void setValues() {
        et_printer.setText(ls.getString(LocalStorage.PRINTER_KEY, "1234"));
        et_printer_port.setText(ls.getString(LocalStorage.PRINTER_PORT, "4"));
        et_printer_ip.setText(ls.getString(LocalStorage.PRINTER_IP, "192.168.0.1"));
    }
    public void requestPrinterList(){
        connectPrinter("", PrintingManager.PROCESS_PRINTER_LIST);
    }
    public void loadPrinters(){
        ArrayList<String> printers = new ArrayList<>();
        try {
            JSONArray jsonarray = new JSONArray(ls.getString(LocalStorage.PRINTER_LIST,""));
            for (int i = 0; i < jsonarray.length(); i++) {
                printers.add(jsonarray.get(i).toString());
            }
        }catch (Exception e){
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
}
