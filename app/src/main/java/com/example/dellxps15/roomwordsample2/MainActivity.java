package com.example.dellxps15.roomwordsample2;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private ProductViewModel mWordViewModel;
    public static String SHARED_PREFS_FILE_NAME = "fontana_shared_prefs";
    private static final String TAG = "MainActivity";

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPTY = "";

    private String products_url = "http://pakango.it/member/getproducts.php"; // ******

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // IF THERE IS INTERNET CALL getProd() else do nothing

        if(isNetworkAvailable()){
            getProd();
            updateCartList();
        }


        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ProductListAdapter adapter = new ProductListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        final SharedPreferences prefx = getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefx.edit();
        //editor.clear().commit();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        SharedPreferences prefs = MainActivity.this.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
                        int count = prefs.getInt("count", 0); //0 is the default value.


                        for(int i =0; i< count; i++){
                            int idName = prefs.getInt("idName"+(i+1), -1);

                            if(idName == position){
                                return;
                            }
                        }

                        Toast.makeText(MainActivity.this, "Item added to cart: "+position, Toast.LENGTH_SHORT).show();
                        TextView tvc = (TextView) findViewById(R.id.textViewCart);
                        tvc.setBackgroundResource(R.drawable.removebutton);
                        tvc.setText("ITEM ADDED");

                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                        editor.putInt("count", (count+1));
                        editor.putInt("idName"+(count+1), position);
                        editor.apply();
                    }
                })
        );


        mWordViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        mWordViewModel.getAllProducts().observe(this, new Observer<List<Products>>() {
            @Override
            public void onChanged(@Nullable final List<Products> products) {
                // Update the cached copy of the words in the adapter.
                adapter.setProducts(products);
            }
        });



    }

    private void getProd() {

        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(KEY_USERNAME, "username");
            request.put(KEY_PASSWORD, "password");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, products_url, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //Check if user got logged in successfully

                            if (true) {

                                // CALL DELETEALL() HERE

                                oldPop();

                                JSONObject obj = new JSONObject(response.toString());
                                int len = obj.length();

                                for(int i = 0; i < len; i++){
                                    JSONObject prod = obj.getJSONObject(Integer.toString(i));

                                    Products x = new Products(Integer.parseInt(prod.getString("id")), prod.getString("product"), prod.getString("description"), Integer.parseInt(prod.getString("price")), prod.getString("image"));
                                    newPop(x);

                                }


                            }else{
                                Toast.makeText(getApplicationContext(),
                                        response.getString(KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        RequestQueue queue = Volley.newRequestQueue(this);
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    public void newPop(Products p){

        ProductViewModel c = new ProductViewModel(this.getApplication());
        c.insert(p);
    }

    public void oldPop(){

        ProductViewModel c = new ProductViewModel(this.getApplication());
        Products x = new Products(0,"CHECK1", "Gold-tip Mont Blanc (Black and Gold) Made in Switzerland", 678, "a1");

        c.delAll(x);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MainActivity.this, DashboardActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {
            Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
            MainActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkout(View view){
        Intent myIntent = new Intent(MainActivity.this, CheckoutActivity.class);
        MainActivity.this.startActivity(myIntent);

    }

    public void updateCartList(){

    }

}
