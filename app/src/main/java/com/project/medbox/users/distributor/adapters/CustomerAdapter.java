package com.project.medbox.users.distributor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.medbox.R;
import com.project.medbox.databasegettersetter.NewUser;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<NewUser> customerDetails;

    private int expanded_card_position = -1;

    public CustomerAdapter(Context c, ArrayList<NewUser> arrayList) {
        mContext = c;
        customerDetails = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_customer, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (expanded_card_position == position){
            holder.customerInfoLayout.setVisibility(View.VISIBLE);
        } else {
            holder.customerInfoLayout.setVisibility(View.GONE);
        }
        holder.customerBusinessName.setText(customerDetails.get(position).getBusinessName());
        holder.customerGstin.setText(customerDetails.get(position).getGstinNumber());
        holder.customerOwnerName.setText(customerDetails.get(position).getOwnerName());
        holder.customerMobile.setText(customerDetails.get(position).getMobileNumber());
        holder.customerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded_card_position != holder.getAdapterPosition()) {
                    expanded_card_position = holder.getAdapterPosition();
                    notifyDataSetChanged();
                } else if (expanded_card_position == holder.getAdapterPosition()) {
                    expanded_card_position = -1;
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return customerDetails.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView customerBusinessName;
        TextView customerGstin;
        TextView customerMobile;
        TextView customerOwnerName;
        RelativeLayout customerInfoLayout;
        CardView customerHeader;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            customerBusinessName = itemView.findViewById(R.id.customer_adapter_business_name);
            customerGstin = itemView.findViewById(R.id.customer_adapter_gstin);
            customerMobile = itemView.findViewById(R.id.customer_adapter_mobile);
            customerOwnerName = itemView.findViewById(R.id.customer_adapter_owner);
            customerInfoLayout = itemView.findViewById(R.id.customer_info_layout);
            customerHeader = itemView.findViewById(R.id.customer_details_header);

        }
    }

}
