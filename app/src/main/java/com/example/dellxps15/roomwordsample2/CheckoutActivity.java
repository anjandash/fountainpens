package com.example.dellxps15.roomwordsample2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.model.Card;
import com.stripe.android.view.CardInputWidget;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CheckoutActivity extends AppCompatActivity {

    public static String SHARED_PREFS_FILE_NAME = "fontana_shared_prefs";
    Context context;
    private ProductViewModel mWordViewModel;
    int totalPrice;
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        session = new SessionHandler(getApplicationContext());

        context = CheckoutActivity.this;
        String name="xxx";


        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
        String restoredText = "scdsc";


            name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            int count = prefs.getInt("count", 0);
            totalPrice = 0;


            for(int i =0; i < count; i++){
                int idName = prefs.getInt("idName"+(i+1), 0);

                if(idName < 0){
                    continue;
                }

                mWordViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
                Products p = mWordViewModel.findItem(idName);

                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.cart_item, null);

                String imgName = p.getImage();
                int resID = context.getResources().getIdentifier(imgName , "drawable", context.getPackageName());

                ImageView imageItemView = (ImageView) view.findViewById(R.id.imageView4);
                imageItemView.setImageResource(resID);
                TextView textView4 = (TextView) view.findViewById(R.id.textView4);
                textView4.setText(p.getProduct());
                TextView textView5 = (TextView) view.findViewById(R.id.textView5);
                textView5.setText("EUR "+String.valueOf(p.getPrice()));

                TextView textView11 = (TextView) view.findViewById(R.id.textView11);
                textView11.setText("Item:");
                textView11.setTag(p.getId());

                ///////

                Button myButton = new Button(this);
                myButton.setId(p.getId());
                myButton.setWidth(100);
                myButton.setOnClickListener(getOnClickDoSomething(myButton));
                myButton.setTextColor(Color.parseColor("#ffffff"));
                myButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                myButton.setPadding(10, 10, 10, 10);
                myButton.setText("Remove item from cart");

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.height = 90;
                params.setMargins(0, 10, 0, 50);
                myButton.setLayoutParams(params);

                ///////

                totalPrice = totalPrice + p.getPrice();

                ViewGroup main = (ViewGroup) findViewById(R.id.insert_point);
                main.addView(view, -1);
                main.addView(myButton, -1);
            }

        LayoutInflater inflater = getLayoutInflater();

        if(session.isLoggedIn()){
            View viewx = inflater.inflate(R.layout.pay_item, null);



            if(totalPrice > 1){
                TextView tx = (TextView) viewx.findViewById(R.id.textView3);
                tx.setText("TOTAL PRICE: EUR "+ String.valueOf(totalPrice));

                ViewGroup main = (ViewGroup) findViewById(R.id.insert_point);
                main.addView(viewx, -1);
            } else {
                TextView tx = (TextView) viewx.findViewById(R.id.textView3);
                tx.setText("You have no items in your cart!");


                TextView bv4 = (TextView) viewx.findViewById(R.id.buttonView4);
                bv4.setVisibility(View.GONE);

                ViewGroup main = (ViewGroup) findViewById(R.id.insert_point);
                main.addView(viewx, -1);
            }


        } else {

            View viewlogin = inflater.inflate(R.layout.loginbutton_item, null);

            ViewGroup main2 = (ViewGroup) findViewById(R.id.insert_point);
            main2.addView(viewlogin, -1);

            Button loginBtn = findViewById(R.id.loginBtn);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CheckoutActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            });

        }


    }

    public void makeCharge(View view){
        Intent myIntent = new Intent(CheckoutActivity.this, PaymentActivity.class);
        myIntent.putExtra("TOTALPR", String.valueOf(totalPrice));
        CheckoutActivity.this.startActivity(myIntent);
    }

    public void removeFromCart(View view){


    }

    View.OnClickListener getOnClickDoSomething(final Button button)  {
        return new View.OnClickListener() {
            public void onClick(View v) {

            int item = v.getId();
            Toast.makeText(CheckoutActivity.this, String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = CheckoutActivity.this.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
                int count = prefs.getInt("count", 0); //0 is the default value.

                int newcount = count - 1;
                int breakposition;

                for(int i =0; i< count; i++){
                    int idName = prefs.getInt("idName"+(i+1), -1);


                    if(idName == item){
                        // remove item from pr
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                        editor.putInt("count", (i+1));
                        editor.putInt("idName"+(i+1), -2);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE).edit();
                        editor.putInt("count", (i+1));
                        editor.putInt("idName"+(i+1), idName);
                        editor.apply();

                    }
                }

                Intent myIntent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
                CheckoutActivity.this.startActivity(myIntent);


            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(CheckoutActivity.this, MainActivity.class);
        CheckoutActivity.this.startActivity(myIntent);
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
            Intent myIntent = new Intent(CheckoutActivity.this, DashboardActivity.class);
            CheckoutActivity.this.startActivity(myIntent);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {
            Intent myIntent = new Intent(CheckoutActivity.this, MainActivity.class);
            CheckoutActivity.this.startActivity(myIntent);
        }

        return super.onOptionsItemSelected(item);
    }


}
