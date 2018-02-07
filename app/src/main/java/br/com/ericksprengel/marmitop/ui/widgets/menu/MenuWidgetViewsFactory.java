package br.com.ericksprengel.marmitop.ui.widgets.menu;

import android.content.Context;
import android.widget.RemoteViews;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.data.MtopMenuItem;
import br.com.ericksprengel.marmitop.utils.MenuUtils;


public class MenuWidgetViewsFactory implements MenuWidgetService.RemoteViewsFactory {

    private Context mContext;
    private List<MtopMenuItem> mMtopMenuItems;

    public MenuWidgetViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mMtopMenuItems = new ArrayList<>();

        DatabaseReference menuDatabaseReference = FirebaseDatabase.getInstance().getReference("menus")
                .child(MenuUtils.getMenuOfTheDay());

        menuDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                    MtopMenuItem mtopMenuItem = dataSnapshotChild.getValue(MtopMenuItem.class);
                    mtopMenuItem.setKey(dataSnapshot.getKey());
                    mMtopMenuItems.add(mtopMenuItem);
                }
                countDownLatch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mMtopMenuItems == null ? 0 : mMtopMenuItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(),
                R.layout.menu_widget_item);

        MtopMenuItem mtopMenuItem = mMtopMenuItems.get(position);
        row.setTextViewText(R.id.menu_wd_menu_item_name_textview, mtopMenuItem.getName());
        row.setTextViewText(R.id.menu_wd_menu_item_description_textview, mtopMenuItem.getDescription());
        return (row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
