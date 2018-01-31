package br.com.ericksprengel.marmitop.ui.main.menu;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.ericksprengel.mamitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.R;

public class MenuAdapter  extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public interface OnMtopMenuItemClickListener {
        void onMtopMenuItemClick(MtopMenuItem mtopMenuItemtem);
    }

    private List<MtopMenuItem> mMtopMenuItems;
    private OnMtopMenuItemClickListener mOnClickMtopMenuItemListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MtopMenuItem mMtopMenuItem;
        TextView mName;
        TextView mDescription;

        ViewHolder(View v) {
            super(v);
            mName = v.findViewById(R.id.menu_ac_item_name_textview);
            mDescription = v.findViewById(R.id.menu_ac_item_description_textview);
            v.setOnClickListener(this);
        }

        void updateData(MtopMenuItem item) {
            mMtopMenuItem = item;
            mName.setText(mMtopMenuItem.getName());
            mDescription.setText(mMtopMenuItem.getDescription());
//            Picasso.with(mPoster.getContext())
//                    .load(mMtopMenuItem.getImage())
//                    .placeholder(R.drawable.mtopMenuItem_poster_thumbnail_placeholder)
//                    .into(mPoster);
        }

        @Override
        public void onClick(View view) {
            MenuAdapter.this.mOnClickMtopMenuItemListener.onMtopMenuItemClick(mMtopMenuItem);
        }
    }

    MenuAdapter(OnMtopMenuItemClickListener listener) {
        this.mOnClickMtopMenuItemListener = listener;
        this.mMtopMenuItems = new ArrayList<>();
    }

    void clear() {
        mMtopMenuItems.clear();
        notifyDataSetChanged();
    }

    void add(MtopMenuItem mtopMenuItem) {
        this.mMtopMenuItems.add(mtopMenuItem);
        notifyItemInserted(mMtopMenuItems.size()-1);
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MtopMenuItem mtopMenuItem = mMtopMenuItems.get(position);

        holder.updateData(mtopMenuItem);
    }

    @Override
    public int getItemCount() {
        return mMtopMenuItems == null ? 0 : mMtopMenuItems.size();
    }

}
