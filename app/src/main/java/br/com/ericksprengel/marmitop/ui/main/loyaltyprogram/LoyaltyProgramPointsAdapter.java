package br.com.ericksprengel.marmitop.ui.main.loyaltyprogram;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.ericksprengel.marmitop.R;

public class LoyaltyProgramPointsAdapter extends RecyclerView.Adapter<LoyaltyProgramPointsAdapter.ViewHolder> {

    private static final int LOYALTY_POINTS_TOTAL = 12;

    private int mDoneCounter = 0;

    class ViewHolder extends RecyclerView.ViewHolder {
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
        return LOYALTY_POINTS_TOTAL;
    }

}
