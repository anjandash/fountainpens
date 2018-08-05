package com.example.dellxps15.roomwordsample2;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private ProductViewModel mWordViewModel;
    public static String SHARED_PREFS_FILE_NAME = "fontana_shared_prefs";
    private static final String TAG = "MainActivity";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

}
