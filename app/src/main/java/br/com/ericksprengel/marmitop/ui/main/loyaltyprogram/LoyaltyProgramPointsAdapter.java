package br.com.ericksprengel.marmitop.ui.main.loyaltyprogram;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.Order;
import br.com.ericksprengel.marmitop.data.OrderDay;

public class LoyaltyProgramPointsAdapter extends RecyclerView.Adapter<LoyaltyProgramPointsAdapter.ViewHolder> {

    private Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private int mDoneCounter = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        OrderDay mOrderDay;
        TextView mPoint;
        View mDone;

        ViewHolder(View v) {
            super(v);
            mPoint = v.findViewById(R.id.loyalty_program_fg_point_textview);
            mDone= v.findViewById(R.id.loyalty_program_fg_done_view);
        }

        void updateData(int position, boolean done) {
            mPoint.setText(String.valueOf(position + 1));
            mPoint.setVisibility(done ? View.INVISIBLE : View.VISIBLE);
            mDone.setVisibility(done ? View.VISIBLE : View.INVISIBLE);
        }
    }

    void clear() {
        mDoneCounter = 0;
        notifyDataSetChanged();
    }

    void add() {
        this.mDoneCounter++;
        notifyItemChanged(mDoneCounter-1);
    }

    @Override
    public LoyaltyProgramPointsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_loyalty_program_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.updateData(position, position < mDoneCounter);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

}
