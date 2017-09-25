package com.fusiotec.servicecenterapi.servicecenter.manager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fusiotec.servicecenterapi.servicecenter.models.db_classes.Printers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;


/**
 * Created by Owner on 6/2/2016.
 */
public class PrintingManager extends IntentService {
    public static final String TAG = "PrintingManager";

    private RequestQueue queue;

    public static final String URL = "url";
    public static final String METHOD = "method";
    public static final String PARAMS = "params";
    public static final String SOURCE = "source";

    public static final String ARRAY_STRING = "array_string";
    boolean is_from_customer = false;

    public static final String PROCESS = "process";
    public static final String RESPONSE = "response";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

    public static final int PROCESS_OPEN_DB = 501;
    public static final int PROCESS_CLOSE_DB = 503;
    public static final int PROCESS_OPEN_PRINTER = 504;
    public static final int PROCESS_PRINTER_TEMPLATE = 505;
    public static final int PROCESS_PRINTER_STATIC_DATA = 506;
    public static final int PROCESS_PRINTER_DATASET = 507;
    public static final int PROCESS_PRINTING = 508;
    public static final int PROCESS_PRINTING_IMAGE = 509;
    public static final int PROCESS_PRINTER_CLOSE = 510;

    public static final int PROCESS_PRINTER_LIST = 401;
    public static final int PROCESS_TEST_PRINT = 402;

    public static final int RESULT_PRINTER_LIST = 601;
    public static final int RESULT_GET_ORDERED_MENUS = 602;

    String host,printer_port,printer_key,printer_name,webservice;
    private String url, method = GET;
    private String src;
    private int process;
    HashMap<String,String> urlParameters;
    ArrayList<String> array_string;

    LocalStorage ls;
    String current_date,raw_current_date;
    String requestBody = "";

    public void setProgress(int process,final String result){
        this.process = process;

        switch (process){
            //Process
            case PROCESS_OPEN_DB:
                break;
            case PROCESS_CLOSE_DB:
                break;
            case PROCESS_OPEN_PRINTER:
                String printer_name2 = ls.getString(LocalStorage.PRINTER_NAME,"").replace(" ","%20");
                url = "http://"+host+":"+printer_port+"/printer/"+printer_key+"/open/?printer_name="+printer_name2;
                method = PrintingManager.GET;
                connect();
                break;
            case PROCESS_PRINTER_TEMPLATE:
                url = "http://"+host+":"+printer_port+"/printer/template/service"+raw_current_date;
                method = PrintingManager.POST;
                sendTemplate();
                connect();
                break;
            case PROCESS_PRINTER_STATIC_DATA:
                url = "http://"+host+":"+printer_port+"/printer/staticdata";
                method = PrintingManager.POST;
                convertStaticDatatoJson();
                connect();
                break;
            case PROCESS_PRINTER_DATASET:
                url = "http://"+host+":"+printer_port+"/printer/dataset";
                method = PrintingManager.POST;
                connect();
                break;
            case PROCESS_PRINTING:
                url = "http://"+host+":"+printer_port+"/printer/print";
                method = PrintingManager.GET;
                connect();
                break;
            case PROCESS_PRINTING_IMAGE:
                break;
            case PROCESS_PRINTER_CLOSE:
                url = "http://"+host+":"+printer_port+"/printer/close";
                method = PrintingManager.DELETE;
                connect();
                break;
            //Printer Test
            case PROCESS_PRINTER_LIST:
                url = "http://"+host+":"+printer_port+"/printer/list";
                method = PrintingManager.GET;
                connect();
                break;
            case PROCESS_TEST_PRINT:
                String printer_name = result.replace(" ","%20");
                url = "http://"+host+":"+printer_port+"/printer/test/?printer_name="+printer_name;
                method = PrintingManager.GET;
                connect();
                break;
            //Results
            case RESULT_PRINTER_LIST:

                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            try {
                                ArrayList<Printers> printers = new ArrayList<>();
                                JSONArray jsonarray = new JSONArray(result);
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    printers.add(new Printers(jsonarray.get(i).toString()));
                                }
                                realm.copyToRealmOrUpdate(printers);
                            }catch (Exception e){
                            }
                        }
                    });
                    realm.close();
                break;
            case RESULT_GET_ORDERED_MENUS:
                setProgress(PROCESS_OPEN_PRINTER,"");
                break;
            default:
                break;
        }
    }
    public void sendTemplate(){
//    requestBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//            "<Report Name=\"Large( "+full_width+", 0)\" Width=\""+full_width+"\" Height=\"0\">\n" +
//            "  <Text Text=\"Check#:\" Width=\"7\" />\n" +
//            "  <Value Value=\"CheckID\" Width=\""+( (full_width/2) - 12)+"\" />\n" +
//            "  <Text Text=\"Terminal:\" Width=\"9\" />\n" +
//            "  <Value Value=\"TerminalID\" Width=\""+( (full_width/2) - 4)+"\" />\n" +
//            "  <Separator Width=\""+full_width+"\" />\n" +
//            "  <Text Text=\"Table:\" Width=\"6\" />\n" +
//            "  <Value Value=\"TableID\" Width=\""+((full_width/2) - 6)+"\" />\n" +
//            "  <Text Text=\"Guest:\" Width=\"6\" />\n" +
//            "  <Value Value=\"GuestNo\" Width=\""+((full_width/2) - 6)+"\" />\n" +
//            "  <Separator Width=\""+full_width+"\" />\n" +
//            "  <Text Alignment=\"LEFT\" Text=\"Qty.\" Width=\"10\" />\n" +
//            "  <Text Text=\"Item Name\" Width=\""+(full_width / 2)+"\" />\n" +
//            "  <Separator Width=\""+full_width+"\" />\n" +
//            "  <DataSection Group=\"OrderedList\" Width=\""+full_width+"\">\n" +
//            "    <Value Alignment=\"LEFT\" Group=\"OrderedList\" Value=\"Quantity\"  Width=\"10\" />\n" +
//            "    <Value Group=\"OrderedList\" Value=\"Name\" Width=\""+(full_width - 10)+"\" />\n" +
//            "  </DataSection>\n" +
//            "  <Separator Width=\""+full_width+"\" />\n" +
//            "  <Value Alignment=\"CENTER\" Value=\"OrderSent"+"\" Width=\""+full_width+"\" />\n" +
//            "  <Value Alignment=\"CENTER\" Value=\"PrinterName\" Width=\""+full_width+"\" />\n" +
//            "  <Value Alignment=\"CENTER\" Value=\"TransactionID\" Width=\""+full_width+"\" />\n" +
//            "  <Value Alignment=\"CENTER\" Value=\"PrintDate\" Width=\""+full_width+"\" />\n" +
//            "</Report>";
        int printer_size;
        try {
            printer_size = Integer.parseInt(ls.getString(LocalStorage.PRINTER_PAPER_SIZE,"48"));
        }catch (Exception e){
            printer_size = 48;
        }

        requestBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<Report Name=\"Large( "+printer_size+", 0)\" Width=\""+printer_size+"\" Height=\"0\">\n" +

                "<Text Alignment=\"CENTER\" Text=\"UNITS COPY\" Width=\"" + printer_size + "\" />\n"+
                "<Value Alignment=\"CENTER\" Value=\"Barcode Id\" Width=\""+printer_size+"\" />\n" +
                "<Barcode Alignment=\"CENTER\" Value=\"Item Barcode\" Transform=\"BIG\" HRIPosition=\"\" Type=\"ITF\" Width=\""+printer_size+"\" />\n"+

                "<Value Value=\"Dealer\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"DateCreated\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Unit\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Model\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Serial\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Warranty\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Complaint\" Width=\""+printer_size+"\" />\n" +

                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "<Value Value=\"Name\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Phone\" Width=\""+printer_size+"\" />\n" +
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+

                "<Text Text=\"Notes:\" Width=\""+printer_size+"\" />\n"+
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+

                "<Separator Width=\""+printer_size+"\" />\n" +
                "<Text Alignment=\"CENTER\" Text=\"CLAIM STUB\" Width=\"" + printer_size + "\" />\n"+
                "<Value Alignment=\"CENTER\" Value=\"Barcode Id2\" Width=\""+printer_size+"\" />\n" +
                "<Barcode Alignment=\"CENTER\" Value=\"Item Barcode2\" Transform=\"BIG\" HRIPosition=\"\" Type=\"ITF\" Width=\""+printer_size+"\" />\n"+

                "<Value Value=\"DateCreated2\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Name2\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Phone2\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Unit2\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Model2\" Width=\""+printer_size+"\" />\n" +
                "<Value Value=\"Serial2\" Width=\""+printer_size+"\" />\n" +
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "<Text Alignment=\"CENTER\" Text=\"\" Width=\""+printer_size+"\" />\n"+
                "</Report>";
    }
    String barcode_params;
    public PrintingManager(){
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent){
        ls = new LocalStorage(this);
        host = ls.getString(LocalStorage.PRINTER_IP, "");
        printer_port = ls.getString(LocalStorage.PRINTER_PORT, "");
        printer_key = ls.getString(LocalStorage.PRINTER_KEY, "");
        printer_name = ls.getString(LocalStorage.PRINTER_NAME, "");

        Bundle extra = intent.getExtras();
        String method = extra.getString(METHOD);
        String source = extra.getString(SOURCE);
        is_from_customer = false;
        int process = extra.getInt(PROCESS);

        String params = extra.getString(PARAMS);
        array_string = extra.getStringArrayList(ARRAY_STRING);

        barcode_params = params;
        this.method = method;
        this.src = source;
        this.process = process;

        DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss a");
        DateFormat dateFormat2 = new SimpleDateFormat("mmddyyyyhhmmss");
        Calendar cal = Calendar.getInstance();
        current_date = dateFormat.format(cal.getTime());
        raw_current_date = dateFormat2.format(cal.getTime());

        setProgress(process,params);
    }

    public void connect(){
        if( hasInternetConnection() ) {
            queue = ServiceApp.getInstance().getRequestQueue();
            final HashMap<String, String> params = urlParameters;
            final int prc = process;

            int method = -1;

            if (this.method.equalsIgnoreCase(GET)){
                method = Request.Method.GET;
                if (params != null) {
                    url += "?";
                    try {
                        for (Map.Entry<String, String> value : params.entrySet())
                            url += value.getKey() + "=" + URLEncoder.encode(value.getValue(), "UTF-8") + "&";
                    } catch (UnsupportedEncodingException uee) {
                        System.out.println("UnsupportedEncodingException");
                    }
                    url = url.substring(0, url.length() - 1);
                }
            }
            if (this.method.equalsIgnoreCase(POST))
                method = Request.Method.POST;

            if (this.method.equalsIgnoreCase(DELETE))
                method = Request.Method.DELETE;
            StringRequest stringRequest = new StringRequest(method, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            switch (process){
                                case PROCESS_PRINTER_LIST:
                                    setProgress(RESULT_PRINTER_LIST,response);
                                    break;
                                case PROCESS_OPEN_PRINTER:
                                    setProgress(PROCESS_PRINTER_TEMPLATE,response);
                                    break;
                                case PROCESS_PRINTER_TEMPLATE:
                                    setProgress(PROCESS_PRINTER_STATIC_DATA,response);
                                    break;
                                case PROCESS_PRINTER_STATIC_DATA:
                                    setProgress(PROCESS_PRINTING,response);
                                    break;
                                case PROCESS_PRINTER_DATASET:
                                    setProgress(PROCESS_PRINTING,response);
                                    break;
                                case PROCESS_PRINTING:
                                    setProgress(PROCESS_PRINTER_CLOSE,"");
                                    break;
                                case PROCESS_PRINTER_CLOSE:
                                    break;
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("PrintingManager", "Error connecting " + prc + error.getMessage());
                    switch (process){
                        case PROCESS_PRINTER_CLOSE:
                            break;
                        default:
                            setProgress(PROCESS_PRINTER_CLOSE,"");
                            break;
                    }
                }
            }
            ) {

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    if (response.headers == null) {
                        // cant just set a new empty map because the member is final.
                        response = new NetworkResponse(
                                response.statusCode,
                                response.data,
                                Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                                response.notModified,
                                response.networkTimeMs);
                    }
                    return super.parseNetworkResponse(response);
                }
                @Override
                public String getBodyContentType(){
                    return "application/x-www-form-urlencoded; charset=" +
                            getParamsEncoding();
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    String postData = requestBody;
                    switch (process){
                        case PROCESS_PRINTER_TEMPLATE:
                        case PROCESS_PRINTER_STATIC_DATA:
                        case PROCESS_PRINTER_DATASET:
                        try {
                            return postData == null ? null :
                                    postData.getBytes(getParamsEncoding());
                        } catch (UnsupportedEncodingException uee) {
                            return null;
                        }
                    }
                    return null;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(stringRequest);

        }else{
            Toast.makeText(this, "No Connection", Toast.LENGTH_SHORT).show();
        }

    }
    public boolean hasInternetConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    public void convertStaticDatatoJson(){
        try {
            JSONObject jObject = new JSONObject();
            jObject.put("Item Barcode",array_string.get(0)+"");
            jObject.put("Barcode Id",array_string.get(0)+"");
            jObject.put("Dealer","Dealer: "+array_string.get(1)+"");
            jObject.put("DateCreated","Date: "+array_string.get(2)+"");
            jObject.put("Unit","Unit: "+array_string.get(3)+"");
            jObject.put("Model","Model: "+array_string.get(4)+"");
            jObject.put("Serial","Serial: "+array_string.get(5)+"");
            jObject.put("Warranty","Warranty Label: "+array_string.get(6)+"");
            jObject.put("Complaint","Complaint: "+ array_string.get(7)+"");
            jObject.put("Name","Name: "+array_string.get(8)+"");
            jObject.put("Phone","Phone: "+array_string.get(9)+"");

            jObject.put("Item Barcode2",array_string.get(0)+"");
            jObject.put("Barcode Id2",array_string.get(0)+"");
            jObject.put("DateCreated2","Date: "+array_string.get(2)+"");
            jObject.put("Name2","Name: "+array_string.get(8)+"");
            jObject.put("Phone2","Phone: "+array_string.get(9)+"");
            jObject.put("Unit2","Unit: "+array_string.get(3)+"");
            jObject.put("Model2","Model: "+array_string.get(4)+"");
            jObject.put("Serial2","Serial: "+array_string.get(5)+"");

            requestBody = jObject.toString();

        }catch (Exception e){
            Log.e("error ",""+e.getMessage());
        }
    }
}
