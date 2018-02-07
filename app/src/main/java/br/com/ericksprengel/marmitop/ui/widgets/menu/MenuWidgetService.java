package br.com.ericksprengel.marmitop.ui.widgets.menu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViewsService;


public class MenuWidgetService extends RemoteViewsService {

    @NonNull
    static Intent getRemoteAdapterIntent(Context context) {
        return new Intent(context, MenuWidgetService.class);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MenuWidgetViewsFactory(this.getApplicationContext());
    }
}
