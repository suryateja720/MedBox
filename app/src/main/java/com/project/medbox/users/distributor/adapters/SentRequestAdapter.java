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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SentRequestAdapter extends RecyclerView.Adapter<SentRequestAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<NewRequest> sentRequestsList;

    public SentRequestAdapter(Context c, ArrayList<NewRequest> arrayList) {
        mContext = c;
        sentRequestsList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.adapter_sent_request, parent, false);
            return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SentRequestAdapter.MyViewHolder holder, int position) {
        holder.requestTo.setText(sentRequestsList.get(position).getRequestTo());
        holder.requestStatus.setText(sentRequestsList.get(position).getRequestStatus());

        if (!sentRequestsList.get(position).getRequestStatus().equals("requested")) {

            holder.requestCancelButton.setVisibility(View.GONE);
            holder.requestRemoveButton.setVisibility(View.VISIBLE);
        }
        else {
            holder.requestRemoveButton.setVisibility(View.GONE);
            holder.requestCancelButton.setVisibility(View.VISIBLE);
        }

        holder.requestRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
                Query childKey = databaseReference.orderByChild("requestTo").equalTo(sentRequestsList.get(position).getRequestTo());
                childKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String dbRequestTo = ds.child("requestTo").getValue(String.class);
                            assert dbRequestTo != null;
                            if (dbRequestTo.equals(sentRequestsList.get(position).getRequestTo())) {
                                String key = ds.getKey();
                                assert key != null;
                                databaseReference.child(key).removeValue();
                                sentRequestsList.remove(position);
                                notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        holder.requestCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");
                Query childKey = databaseReference.orderByChild("requestTo").equalTo(sentRequestsList.get(position).getRequestTo());
                childKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String dbRequestTo = ds.child("requestTo").getValue(String.class);
                            assert dbRequestTo != null;
                            if (dbRequestTo.equals(sentRequestsList.get(position).getRequestTo())) {
                                String key = ds.getKey();
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("requestStatus", "cancelled");
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
    }

    @Override
    public int getItemCount() {
        return sentRequestsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView requestTo;
        TextView requestStatus;
        Button requestCancelButton;
        ImageView requestRemoveButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            requestTo = itemView.findViewById(R.id.sent_request_adapter_request_to);
            requestStatus = itemView.findViewById(R.id.sent_request_adapter_request_status);
            requestCancelButton = itemView.findViewById(R.id.sent_request_adapter_cancel_button);
            requestRemoveButton = itemView.findViewById(R.id.sent_request_adapter_remove_button);
        }
    }

    public void updateList(ArrayList<NewRequest> updatesArrayList){
        this.sentRequestsList = updatesArrayList;
    }
}
