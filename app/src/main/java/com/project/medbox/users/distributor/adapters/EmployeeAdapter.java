package com.project.medbox.users.distributor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
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
import com.project.medbox.databasegettersetter.NewEmployee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<NewEmployee> employeeArrayList;
    private String userKey;

    public EmployeeAdapter(Context c, ArrayList<NewEmployee> arrayList, String key) {
        mContext = c;
        employeeArrayList = arrayList;
        userKey = key;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_employee, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.MyViewHolder holder, int position) {
        holder.employeeName.setText(employeeArrayList.get(position).getEmployeeName());
        holder.employeeMobile.setText(employeeArrayList.get(position).getEmployeeMobile());


        if (employeeArrayList.get(position).getEmployeeActiveStatus().equals("active")) {
            holder.employeeActiveStatus.setChecked(true);
        } else if (employeeArrayList.get(position).getEmployeeActiveStatus().equals("inactive")) {
            holder.employeeActiveStatus.setChecked(false);
        }

        holder.employeeActiveStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Distributor")
                    .child(userKey)
                    .child("Employee");
            Query childKey = databaseReference.orderByChild("employeeName")
                    .equalTo(employeeArrayList.get(position).getEmployeeName());
            childKey.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String employeeName = ds.child("employeeName").getValue(String.class);
                        assert employeeName != null;
                        if (employeeName.equals(employeeArrayList.get(position).getEmployeeName())) {
                            String key = ds.getKey();
                            if (isChecked) {
                                assert key != null;
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("employeeActiveStatus", "active");
                                databaseReference.child(key).updateChildren(updateMap);
                            } else {
                                Map<String, Object> updateMap = new HashMap<>();
                                updateMap.put("employeeActiveStatus", "inactive");
                                assert key != null;
                                databaseReference.child(key).updateChildren(updateMap);
                            }
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
        return employeeArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView employeeName;
        TextView employeeMobile;
        Switch employeeActiveStatus;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.adapter_employee_name);
            employeeMobile = itemView.findViewById(R.id.adapter_employee_mobile);
            employeeActiveStatus = itemView.findViewById(R.id.adapter_employee_active_status);

        }
    }

    public void filterList(ArrayList<NewEmployee> filterArray){
        this.employeeArrayList = filterArray;
        notifyDataSetChanged();

    }
}
