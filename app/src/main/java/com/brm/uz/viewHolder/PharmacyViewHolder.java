package com.brm.uz.viewHolder;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brm.uz.R;
import com.brm.uz.adapter.PharmacyOrdersAdapter;

public class PharmacyViewHolder extends RecyclerView.ViewHolder {
    public  View view;
    public  TextView medicationsText;
    public  EditText medicationsEdit;
    public PharmacyOrdersAdapter.MyCustomEditTextListener myCustomEditTextListener;
    public PharmacyViewHolder(@NonNull View itemView, PharmacyOrdersAdapter.MyCustomEditTextListener myCustomEditTextListener) {
        super(itemView);
        view = itemView;
        medicationsText = view.findViewById(R.id.pharmacy_orders_medications_name_text_view);
        medicationsEdit = view.findViewById(R.id.pharmacy_orders_medications_edit_text);
        this.myCustomEditTextListener = myCustomEditTextListener;
        this.medicationsEdit.addTextChangedListener(myCustomEditTextListener);
    }
}
