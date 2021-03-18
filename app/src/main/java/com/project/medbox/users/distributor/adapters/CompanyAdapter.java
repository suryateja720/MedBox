package com.project.medbox.users.distributor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.medbox.R;
import com.project.medbox.databasegettersetter.NewCompany;
import com.project.medbox.databasegettersetter.NewProduct;
import com.project.medbox.users.distributor.entries.NewProductActivity;

import java.util.ArrayList;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.MyViewHolder> {
    private Context mContext;
    private String userKey;
    private ArrayList<NewCompany> companyDetails;
    private ArrayList<NewProduct> productDetails;
    private int expanded_card_position = -1;

    public CompanyAdapter(Context c, ArrayList<NewCompany> arrayCompany, ArrayList<NewProduct> arrayProduct, String key) {
        mContext = c;
        companyDetails = arrayCompany;
        productDetails = arrayProduct;
        userKey = key;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_company, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.MyViewHolder holder, int position) {
        ArrayList<NewProduct> productArrayList = new ArrayList<>();

        for (int i = 0; i < productDetails.size(); i++) {
            if (productDetails.get(i).getProductCompanyName().equals(companyDetails.get(position).getCompanyName())) {
                productArrayList.add(productDetails.get(i));
            }
        }

        ProductAdapter productAdapter = new ProductAdapter(mContext, productArrayList);
        RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                int action = e.getAction();
                if (action == MotionEvent.ACTION_MOVE) {
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        };
        holder.productRecyclerView.addOnItemTouchListener(mScrollTouchListener);
        holder.productRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.productRecyclerView.setAdapter(productAdapter);

        if (productArrayList.size() == 0) {
            holder.productListTitle.setVisibility(View.GONE);
            holder.productRecyclerView.setVisibility(View.GONE);
            holder.noProductText.setVisibility(View.VISIBLE);
            holder.indexIcon.setTextColor(mContext.getResources().getColor(R.color.colorOrange500));
            holder.companyProductCount.setBackgroundColor(mContext.getResources().getColor(R.color.colorOrange300));
            holder.companyProductCount.setText(String.valueOf((productArrayList.size())));
        } else {
            holder.productListTitle.setVisibility(View.VISIBLE);
            holder.productRecyclerView.setVisibility(View.VISIBLE);
            holder.productListTitle.setBackgroundColor(mContext.getResources().getColor(R.color.colorTeal300));
            holder.productRecyclerView.setBackground(mContext.getResources().getDrawable(R.drawable.form_gradient_grey_solid_teal));
            holder.noProductText.setVisibility(View.GONE);
            holder.indexIcon.setTextColor(mContext.getResources().getColor(R.color.colorTeal500));
            holder.companyProductCount.setBackgroundColor(mContext.getResources().getColor(R.color.colorTeal300));
            holder.companyProductCount.setText(String.valueOf((productArrayList.size())));
        }

        ViewGroup.LayoutParams p = holder.companyAdapterCardView.getLayoutParams();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) p;
        if (expanded_card_position == position) {
            lp.setMargins(30,40,30,40);
            holder.companyAdapterHeader.setCardElevation(4);
            holder.companyAdapterCardView.setCardElevation(8);
            holder.indexIcon.setElevation(1.5f);
            holder.companyAdapterHeader.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.companyDropDown.setImageResource(R.drawable.ic_arrow_drop_up_grey_24dp);
            holder.companyProductLayout.setVisibility(View.VISIBLE);
        } else {
            lp.setMargins(8,2,8,2);
            holder.indexIcon.setElevation(2);
            holder.companyAdapterHeader.setCardElevation(2);
            holder.companyAdapterCardView.setCardElevation(2);
            holder.companyAdapterHeader.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holder.companyDropDown.setImageResource(R.drawable.ic_arrow_drop_down_grey_24dp);
            holder.companyProductLayout.setVisibility(View.GONE);
        }

        holder.companyName.setText(companyDetails.get(position).getCompanyName());

        if (!companyDetails.get(position).getCompanyEmailId().equals("")) {
            holder.companyEmailId.setText(companyDetails.get(position).getCompanyEmailId());
        } else {
            holder.companyEmailId.setText("N/A");
        }

        if (!companyDetails.get(position).getCompanyRepresentativeName().equals("")) {
            holder.companyRepresentativeName.setText(companyDetails.get(position).getCompanyRepresentativeName());
        } else {
            holder.companyRepresentativeName.setText("N/A");
        }

        String[] companyName = companyDetails.get(position).getCompanyName().split(" ");
        StringBuilder indexString = new StringBuilder();
        indexString.append(companyName[0].toUpperCase().charAt(0));
        holder.indexIcon.setText(indexString);

        holder.companyAdapterHeader.setOnClickListener(v -> {
            if (expanded_card_position != holder.getAdapterPosition()) {
                expanded_card_position = holder.getAdapterPosition();
                notifyDataSetChanged();
            } else if (expanded_card_position == holder.getAdapterPosition()) {
                expanded_card_position = -1;
                notifyDataSetChanged();
            }
        });
        holder.addProductButton.setOnClickListener(v -> {

            Intent intent = new Intent(mContext, NewProductActivity.class);
            intent.putExtra("userKey", userKey);
            intent.putExtra("companyName", companyDetails.get(position).getCompanyName());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return companyDetails.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView indexIcon;
        TextView companyName;
        ImageView companyDropDown;
        RelativeLayout companyProductLayout;
        CardView companyAdapterHeader;
        TextView companyRepresentativeName;
        TextView companyEmailId;
        TextView companyProductCount;
        Button addProductButton;
        TextView noProductText;
        RecyclerView productRecyclerView;
        TextView productListTitle;
        CardView companyAdapterCardView;


        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            indexIcon = itemView.findViewById(R.id.company_index_icon);
            companyName = itemView.findViewById(R.id.company_name);
            companyDropDown = itemView.findViewById(R.id.company_image_drop_down);
            companyProductLayout = itemView.findViewById(R.id.company_adapter_products_layout);
            companyAdapterHeader = itemView.findViewById(R.id.company_adapter_header);
            companyRepresentativeName = itemView.findViewById(R.id.company_adapter_representative_name);
            companyEmailId = itemView.findViewById(R.id.company_adapter_email_id);
            companyProductCount = itemView.findViewById(R.id.company_product_count);
            addProductButton = itemView.findViewById(R.id.new_product_button);
            noProductText = itemView.findViewById(R.id.no_products_text);
            productRecyclerView = itemView.findViewById(R.id.product_list_holder);
            productListTitle = itemView.findViewById(R.id.company_adapter_products_title);
            companyAdapterCardView = itemView.findViewById(R.id.company_adapter_card_view);

        }
    }

    public void filterList(ArrayList<NewCompany> filteredList) {
        this.companyDetails = filteredList;
        notifyDataSetChanged();
        expanded_card_position = -1;
    }

}
