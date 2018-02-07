package br.com.ericksprengel.marmitop.ui.widgets.menu;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import br.com.ericksprengel.marmitop.R;
import br.com.ericksprengel.marmitop.utils.MenuUtils;

/**
 * Implementation of App Widget functionality.
 */
public class MenuWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.menu_widget);

        // Menu name (textview)
        views.setTextViewText(R.id.menu_wd_menu_name_textview, context.getString(R.string.menu_wd_menu_name, MenuUtils.getMenuOfTheDay()));

        // Menu items (list)
        Intent intent = MenuWidgetService.getRemoteAdapterIntent(context);
        views.setRemoteAdapter(R.id.menu_wd_menu_items_listview, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

