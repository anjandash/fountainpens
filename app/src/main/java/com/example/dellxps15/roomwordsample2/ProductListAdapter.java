package com.example.dellxps15.roomwordsample2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.dellxps15.roomwordsample2.MainActivity.SHARED_PREFS_FILE_NAME;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;
        private final ImageView imageItemView;
        private final TextView descItemView;
        private final TextView priceItemView;
        private final TextView buttonItemView;

        private ProductViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
            imageItemView = itemView.findViewById(R.id.imageView);
            descItemView = itemView.findViewById(R.id.textViewDesc);
            priceItemView = itemView.findViewById(R.id.textViewPrice);
            buttonItemView = itemView.findViewById(R.id.textViewCart);
        }
    }

    private final LayoutInflater mInflater;
    private List<Products> mProducts; // Cached copy of products
    private Context context;

    ProductListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        itemView.setClickable(false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        if (mProducts != null) {
            Products current = mProducts.get(position);
            holder.wordItemView.setText(current.getProduct());

            String imgName = current.getImage();
            int resID = context.getResources().getIdentifier(imgName , "drawable", context.getPackageName());
            holder.imageItemView.setImageResource(resID);

            holder.descItemView.setText(current.getDescription());
            holder.priceItemView.setText("Price: EUR " + String.valueOf(current.getPrice()));

            Context cx = holder.priceItemView.getContext();

            SharedPreferences prefs = cx.getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);
            int count = prefs.getInt("count", 0); //0 is the default value.


            for(int i =0; i< count; i++){
                int idName = prefs.getInt("idName"+(i+1), -1);

                if(idName == position){
                    holder.buttonItemView.setText("ITEM ADDED");
                    holder.buttonItemView.setBackgroundResource(R.drawable.removebutton);
                }
            }


        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Word");
        }
    }

    void setProducts(List<Products> products){
        mProducts = products;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mProducts != null)
            return mProducts.size();
        else return 0;
    }
}