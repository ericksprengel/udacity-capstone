package br.com.ericksprengel.marmitop.ui.addtoorder;


import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {


    public interface OnOptionClickListener {
        void onOptionClick(MtopMenuItem.Option option);
    }

    private Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private List<Pair<String, MtopMenuItem.Option>> mOptions;

    private int mSelectedItem = 0;
    private String mPendingSelectedItem;
    private OnOptionClickListener mOnClickOptionListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mPosition;
        String mKey;
        MtopMenuItem.Option mOption;
        RadioButton mName;
        TextView mPrice;

        ViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.add_to_order_ac_item_name_radiobutton);
            mPrice = v.findViewById(R.id.add_to_order_ac_item_price_textview);
            mName.setOnClickListener(this);
        }

        void updateData(int position, String key, MtopMenuItem.Option option) {
            mPosition = position;
            mKey = key;
            mOption = option;
            mName.setText(mOption.getName());
            mName.setChecked(position == mSelectedItem);
            mPrice.setText(NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(mOption.getPrice()));
//            Picasso.with(mPoster.getContext())
//                    .load(mMtopMenuItem.getImage())
//                    .placeholder(R.drawable.mtopMenuItem_poster_thumbnail_placeholder)
//                    .into(mPoster);
        }

        @Override
        public void onClick(View view) {
            selectItem(mPosition);
            OptionsAdapter.this.mOnClickOptionListener.onOptionClick(mOption);
        }
    }

    private void selectItem(int position) {
        if(mSelectedItem == position) {
            return;
        }
        int oldPosition = mSelectedItem;
        mSelectedItem = position;

        notifyItemChanged(oldPosition);
        notifyItemChanged(mSelectedItem);
    }

    public String getSelectedOptionKey() {
        return mOptions.get(mSelectedItem).first;
    }

    public MtopMenuItem.Option getSelectedOption() {
        return mOptions.get(mSelectedItem).second;
    }

    public void setSelectedOption(String option) {
        if(mOptions == null) {
            mPendingSelectedItem = option;
            return;
        }
        for(int i = 0; i < mOptions.size(); i++) {
            Pair pair = mOptions.get(i);
            if(pair.first.equals(option)) {
                selectItem(i);
                return;
            }
        }
    }

    OptionsAdapter(OnOptionClickListener listener) {
        this.mOnClickOptionListener = listener;
    }

    void clear() {
        mOptions = null;
        notifyDataSetChanged();
    }

    void setOptions(List<Pair<String, MtopMenuItem.Option>> options) {
        this.mOptions = options;
        if(mPendingSelectedItem != null) {
            setSelectedOption(mPendingSelectedItem);
            mPendingSelectedItem = null;
        }
        notifyDataSetChanged();
    }

    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_add_to_order_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pair<String, MtopMenuItem.Option> optionPair = mOptions.get(position);
        holder.updateData(position, optionPair.first, optionPair.second);
    }

    @Override
    public int getItemCount() {
        return mOptions == null ? 0 : mOptions.size();
    }

}
