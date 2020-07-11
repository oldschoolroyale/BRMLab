package com.brm.uz.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.models.PharmacyOrdersPOJO;
import com.brm.uz.R;
import com.brm.uz.viewHolder.PharmacyViewHolder;

import java.util.List;

public class PharmacyOrdersAdapter extends RecyclerView.Adapter<PharmacyViewHolder> {
    private Context context;
    private List<PharmacyOrdersPOJO> pharmacyOrdersPOJOS;


    public PharmacyOrdersAdapter(Context context, List<PharmacyOrdersPOJO> pharmacyOrdersPOJOS) {
        this.context = context;
        this.pharmacyOrdersPOJOS = pharmacyOrdersPOJOS;
    }

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pharmacy_orders_item_list, null);
        PharmacyViewHolder holder = new PharmacyViewHolder (view, new MyCustomEditTextListener());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.medicationsText.setText(pharmacyOrdersPOJOS.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return pharmacyOrdersPOJOS.size();
    }


    public class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("")){
                pharmacyOrdersPOJOS.get(position).setQuantity(0);
            }
            else {
                pharmacyOrdersPOJOS.get(position).setQuantity(Integer.parseInt(s.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
