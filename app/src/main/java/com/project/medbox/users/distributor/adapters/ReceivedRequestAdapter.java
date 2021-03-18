package com.project.medbox.users.distributor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.medbox.R;
import com.project.medbox.databasegettersetter.NewRequest;
import com.project.medbox.databasegettersetter.NewUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceivedRequestAdapter extends RecyclerView.Adapter<ReceivedRequestAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<NewRequest> receivedRequestList;
    String userKey;
    public ReceivedRequestAdapter(Context c, ArrayList<NewRequest> arrayList,String key) {
        mContext = c;
        receivedRequestList = arrayList;
        userKey = key;
    }

    @NonNull
    @Override
    public ReceivedRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_received_request, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedRequestAdapter.MyViewHolder holder, int position) {
        holder.requestFrom.setText(receivedRequestList.get(position).getRequestFrom());
        if (receivedRequestList.get(position).getRequestStatus().equals("requested")){
            holder.requestAccept.setVisibility(View.VISIBLE);
            holder.requestReject.setVisibility(View.VISIBLE);
            holder.requestRemove.setVisibility(View.GONE);
        } else {
            holder.requestAccept.setVisibility(View.GONE);
            holder.requestReject.setVisibility(View.GONE);
            holder.requestRemove.setVisibility(View.VISIBLE);
        }
        holder.requestAccept.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
            Query childKey = databaseReference.orderByChild("requestFrom").equalTo(receivedRequestList.get(position).getRequestFrom());
            childKey.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String dbRequestFrom = ds.child("requestFrom").getValue(String.class);
                        assert dbRequestFrom != null;
                        if (dbRequestFrom.equals(receivedRequestList.get(position).getRequestFrom())) {
                            String key = ds.getKey();
                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("requestStatus", "accepted");
                            assert key != null;
                            databaseReference.child(key).updateChildren(updateMap);
                        }
                    }
                    searchForCustomer(receivedRequestList.get(position).getRequestFrom());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
        holder.requestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
                Query childKey = databaseReference.orderByChild("requestFrom").equalTo(receivedRequestList.get(position).getRequestFrom());
                childKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String dbRequestFrom = ds.child("requestFrom").getValue(String.class);
                            assert dbRequestFrom != null;
                            if (dbRequestFrom.equals(receivedRequestList.get(position).getRequestFrom())) {
                                String key = ds.getKey();
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("requestStatus", "rejected");
                                assert key != null;
                                databaseReference.child(key).updateChildren(updateMap);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        holder.requestRemove.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
            Query childKey = databaseReference.orderByChild("requestFrom").equalTo(receivedRequestList.get(position).getRequestFrom());
            childKey.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String dbRequestFrom = ds.child("requestFrom").getValue(String.class);
                        assert dbRequestFrom != null;
                        if (dbRequestFrom.equals(receivedRequestList.get(position).getRequestFrom())) {
                            String key = ds.getKey();
                            assert key != null;
                            databaseReference.child(key).removeValue();
                            receivedRequestList.remove(position);
                            notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


    }


    @Override
    public int getItemCount() {
        return receivedRequestList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView requestFrom;
        Button requestAccept;
        Button requestReject;
        ImageView requestRemove;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            requestFrom = itemView.findViewById(R.id.received_request_adapter_request_from);
            requestAccept = itemView.findViewById(R.id.received_request_adapter_accept_button);
            requestReject = itemView.findViewById(R.id.received_request_adapter_reject_button);
            requestRemove = itemView.findViewById(R.id.request_request_adapter_remove_button);

        }
    }

    public void updateList(ArrayList<NewRequest> arrayList){
        this.receivedRequestList = arrayList;
        notifyDataSetChanged();

    }

    private void searchForCustomer(String requestFrom) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Retailer");
        Query childKey = databaseReference.orderByChild("gstinNumber").equalTo(requestFrom);
        childKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dbKey = ds.getKey();
                    NewUser dbCustomerDetails = ds.getValue(NewUser.class);
                    assert dbCustomerDetails != null;
                    if (dbCustomerDetails.getGstinNumber().equals(requestFrom)) {
                        addCustomer(dbCustomerDetails,dbKey);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addCustomer(NewUser customerDetails,String retailerKey) {

        DatabaseReference supplierDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Distributor");
        Query childKey = supplierDatabaseReference.orderByChild("gstinNumber");
        childKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dbKey = ds.getKey();
                    NewUser dbSupplierDetails = ds.getValue(NewUser.class);
                    assert dbSupplierDetails != null;
                    assert dbKey != null;
                    if (dbKey.equals(userKey)) {
                        DatabaseReference customerDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Distributor")
                                .child(userKey)
                                .child("Customer");
                        customerDatabaseReference.push().setValue(customerDetails);
                        addSupplier(dbSupplierDetails,retailerKey);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addSupplier(NewUser supplierDetails,String retailerKey) {
        DatabaseReference supplierDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Retailer")
                .child(retailerKey)
                .child("Supplier");
        supplierDatabaseReference.push().setValue(supplierDetails);

    }

}
