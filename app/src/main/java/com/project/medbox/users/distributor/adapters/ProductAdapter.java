package com.project.medbox.users.distributor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.medbox.R;
import com.project.medbox.databasegettersetter.NewProduct;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<NewProduct> productDetails;

    ProductAdapter(Context c, ArrayList<NewProduct> arrayList) {

        mContext = c;
        productDetails = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_product, parent, false);
        return new ProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
       if (!productDetails.isEmpty()){
           holder.productName.setText(productDetails.get(position).getProductName());
           holder.productCode.setText(productDetails.get(position).getProductHsnCode());
           holder.productPack.setText(productDetails.get(position).getProductPack());

       }
    }

    @Override
    public int getItemCount() {
        return productDetails.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productCode;
        TextView productPack;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_adapter_product_name);
            productCode = itemView.findViewById(R.id.product_adapter_product_code);
            productPack = itemView.findViewById(R.id.product_adapter_product_pack);
        }
    }
}
