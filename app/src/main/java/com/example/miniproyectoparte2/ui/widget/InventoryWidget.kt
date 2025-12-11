package com.example.miniproyectoparte2.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.miniproyectoparte2.R
import com.example.miniproyectoparte2.data.repository.AuthRepository
import com.example.miniproyectoparte2.data.repository.ProductRepository
import com.example.miniproyectoparte2.di.WidgetEntryPoint
import com.example.miniproyectoparte2.ui.home.HomeActivity
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class InventoryWidget : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val ACTION_TOGGLE = "com.example.miniproyectoparte2.ACTION_TOGGLE"
        private const val ACTION_MANAGE = "com.example.miniproyectoparte2.ACTION_MANAGE"
        private const val PREFS_NAME = "InventoryWidgetPrefs"
        private const val PREF_HIDDEN = "hidden"

        fun updateWidget(context: Context) {
            val intent = Intent(context, InventoryWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, InventoryWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE -> {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val currentHidden = prefs.getBoolean(PREF_HIDDEN, false)
                prefs.edit().putBoolean(PREF_HIDDEN, !currentHidden).apply()

                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, InventoryWidget::class.java)
                )
                for (id in ids) {
                    updateAppWidget(context, appWidgetManager, id)
                }
            }
            ACTION_MANAGE -> {
                val openAppIntent = Intent(context, HomeActivity::class.java)
                openAppIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(openAppIntent)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isHidden = prefs.getBoolean(PREF_HIDDEN, false)

        if (isHidden) {
            views.setTextViewText(R.id.widget_balance_text, "$ ••••••••")
            views.setImageViewResource(R.id.widget_toggle_visibility_icon, R.drawable.ic_eye_closed)
        } else {
            views.setImageViewResource(R.id.widget_toggle_visibility_icon, R.drawable.ic_eye_open)
            loadInventoryTotal(context, views, appWidgetManager, appWidgetId)
        }

        val toggleIntent = Intent(context, InventoryWidget::class.java)
        toggleIntent.action = ACTION_TOGGLE
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_toggle_visibility_icon, togglePendingIntent)

        val manageIntent = Intent(context, InventoryWidget::class.java)
        manageIntent.action = ACTION_MANAGE
        val managePendingIntent = PendingIntent.getBroadcast(
            context, 1, manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_label, managePendingIntent)
        views.setOnClickPendingIntent(R.id.widget_manage_icon, managePendingIntent)
        views.setOnClickPendingIntent(R.id.widget_logo, managePendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun loadInventoryTotal(
        context: Context,
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        scope.launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    WidgetEntryPoint::class.java
                )

                val authRepository = entryPoint.authRepository()
                val productRepository = entryPoint.productRepository()

                val userId = authRepository.getCurrentUserId()
                if (userId == null) {
                    views.setTextViewText(R.id.widget_balance_text, "$ 0,00")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                    return@launch
                }

                productRepository.getProducts(userId).collect { products ->
                    val total = productRepository.calculateTotalInventory(products)
                    val formattedTotal = formatPrice(total)
                    views.setTextViewText(R.id.widget_balance_text, formattedTotal)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                android.util.Log.e("InventoryWidget", "Error al cargar total: ${e.message}")
                views.setTextViewText(R.id.widget_balance_text, "$ 0,00")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun formatPrice(price: Double): String {
        val parts = String.format("%.2f", price).split(".")
        val integerPart = parts[0]
        val decimalPart = parts.getOrNull(1) ?: "00"

        if (integerPart == "0") {
            return "$ 0,00"
        }

        val reversed = integerPart.reversed()
        val withSeparators = reversed.chunked(3).joinToString(".").reversed()

        return "$ $withSeparators,$decimalPart"
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        scope.cancel()
    }
}