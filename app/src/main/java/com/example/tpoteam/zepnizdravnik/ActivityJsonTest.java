package com.example.tpoteam.zepnizdravnik;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListAdapter;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ActivityJsonTest extends AppCompatActivity {
    JSONArray jsonZdravniki, jsonDomovi;
    ArrayList<Zdravnik> listaZdravnikov = new ArrayList<>();
    ArrayList<Dom> listaDomov = new ArrayList<>();
    private ProgressWheel pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_test);

        pw = (ProgressWheel)findViewById(R.id.progress_wheel);
        pw.spin();

        Bundle b = this.getIntent().getExtras();
        if(!b.getBoolean("Izbira")){
            prikazZdravstvenihDomov();
        }
        else{
            prikazZdravnikov();

        }
    }


    private void prikazZdravstvenihDomov(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://zepnizdravnik.azurewebsites.net/index.php/homeInfoJSON")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }

            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    jsonDomovi = new JSONArray(responseData);

                    for(int i=0; i<jsonDomovi.length(); i++) {
                        JSONObject zdrDom = (JSONObject) jsonDomovi.get(i);

                        Dom d = new Dom(zdrDom.getString("Ime_doma"), zdrDom.getString("Naslov"), zdrDom.getString("Kraj"), zdrDom.getString("Mail_dom"), zdrDom.getString("Postna_stevilka"), zdrDom.getString("Telefon_dom"));

                        listaDomov.add(d);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ActivityJsonTest.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            pw.stopSpinning();
                            pw.setVisibility(View.GONE);


                            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                            RecyclerView.Adapter mAdapter = new MyRecyclerViewDomAdapter(ActivityJsonTest.this, listaDomov);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityJsonTest.this));
                            mRecyclerView.setAdapter(mAdapter);

//                                ZdravnikiExpandableAdapter zdrAdapter = new ZdravnikiExpandableAdapter(ActivityJsonTest.this,listaZdravnikov);
//                                ExpandableListView elv = (ExpandableListView)findViewById(R.id.expandableListView);
//                                elv.setAdapter(zdrAdapter);




                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }








    private void prikazZdravnikov(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                //spremeniti na naslov, ki bo na azure
                .url("http://zepnizdravnik.azurewebsites.net/index.php/doctorInfoJSON")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

            }


            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    //Log.e("vrne", responseData.toString());
                    jsonZdravniki = new JSONArray(responseData);

                    for(int i=0; i<jsonZdravniki.length(); i++) {
                        JSONObject zdr = (JSONObject) jsonZdravniki.get(i);


                        Zdravnik t = new Zdravnik(zdr.getString("Ime"), zdr.getString("Priimek"), zdr.getString("Ime_doma"),
                                zdr.getString("Mail_zdravnik"),
                                zdr.getString("Telefon_zdravnik"),
                                zdr.getString("Naziv"),
                                zdr.getString("ID_urnika"));

                        ArrayList<Dan> dneviUrnik = new ArrayList<>();
                        String[] dnevi = {"Ponedeljek", "Torek", "Sreda", "Cetrtek", "Petek", "Sobota", "Nedelja"};
                        for (int j = 0; j < dnevi.length; j++) {
                            dneviUrnik.add(new Dan(dnevi[j], zdr.getString(dnevi[j])));
                        }
                        t.setUrnik(dneviUrnik);
                        listaZdravnikov.add(t);



                    }
                    //Log.e("doctor", listaZdravnikov.get(0).ime);

                    ActivityJsonTest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                pw.stopSpinning();
                                pw.setVisibility(View.GONE);


                                RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

                                RecyclerView.Adapter mAdapter = new MyRecyclerViewZdravnikiAdapter(ActivityJsonTest.this,listaZdravnikov);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(ActivityJsonTest.this));
                                mRecyclerView.setAdapter(mAdapter);



//                                ZdravnikiExpandableAdapter zdrAdapter = new ZdravnikiExpandableAdapter(ActivityJsonTest.this,listaZdravnikov);
//                                ExpandableListView elv = (ExpandableListView)findViewById(R.id.expandableListView);
//                                elv.setAdapter(zdrAdapter);




                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }



}