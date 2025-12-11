package com.example.miniproyectoparte2.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.miniproyectoparte2.R
import com.example.miniproyectoparte2.ui.auth.LoginActivity
import com.example.miniproyectoparte2.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_SALDO = "ACTION_TOGGLE_SALDO"
        private const val ACTION_GESTIONAR = "ACTION_GESTIONAR"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId, false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, InventoryWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(component)

        when (intent.action) {
            ACTION_TOGGLE_SALDO -> {
                val showSaldo = intent.getBooleanExtra("showSaldo", false)
                for (id in appWidgetIds) {
                    updateWidget(context, appWidgetManager, id, showSaldo)
                }
            }
            ACTION_GESTIONAR -> {
                // Más adelante podemos reactivar openScreenFromWidget
            }
        }
    }


    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        showSaldo: Boolean
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        val isLogged = FirebaseAuth.getInstance().currentUser != null
        val saldoTotal = 0.0 // luego lo conectamos a Firestore

        val saldoText = if (showSaldo && isLogged) {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            format.format(saldoTotal)
        } else {
            "$ ****"
        }
        views.setTextViewText(R.id.txtSaldo, saldoText)

        val eyeIcon = if (showSaldo && isLogged) R.mipmap.ic_launcher else R.mipmap.ic_launcher
        views.setImageViewResource(R.id.imgToggleSaldo, eyeIcon)


        // Click en ojo
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_SALDO
            putExtra("showSaldo", !(showSaldo && isLogged))
        }
        val togglePending = PendingIntent.getBroadcast(
            context, 0, toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.imgToggleSaldo, togglePending)

        // Click en Gestionar inventario + icono
        val gestionarIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_GESTIONAR
        }
        val gestionarPending = PendingIntent.getBroadcast(
            context, 1, gestionarIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.txtGestionar, gestionarPending)
        views.setOnClickPendingIntent(R.id.imgGestionar, gestionarPending)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

}
