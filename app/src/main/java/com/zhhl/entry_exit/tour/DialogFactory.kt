package com.zhhl.entry_exit.tour

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.dialog_success.view.*

class DialogFactory {

    companion object {
        fun success(context: Context, message: String): AlertDialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_success, null);
            val dialog = AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(view)
                .create()
            view.message.text = message

            return dialog
        }


        fun progressToSuccess(dialog: AlertDialog, text: String) {

            dialog.window?.let {
                it.decorView.su.visibility = View.VISIBLE
                it.decorView.failed.visibility=View.GONE
                it.decorView.pr.visibility = View.GONE
                it.decorView.message.text = text
            }

        }
        fun toProgress(dialog: AlertDialog, text: String) {
            dialog.window?.let {
                it.decorView.pr.visibility = View.VISIBLE
                it.decorView.failed.visibility=View.GONE
                it.decorView.su.visibility = View.GONE
                it.decorView.message.text = text
            }

        }

        fun dialogResize(dialog: AlertDialog, context: Context, width: Int, height: Int) {
            val params = dialog.window.attributes
            params.width =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 100f, context.resources.displayMetrics).toInt()
            params.height =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, 100f, context.resources.displayMetrics).toInt()
            dialog.window.attributes = params

        }

        fun progressToFailed(dialog: AlertDialog, msg: String) {
            dialog.window?.let {

                it.decorView.su.visibility = View.GONE
                it.decorView.failed.visibility = View.VISIBLE
                it.decorView.pr.visibility = View.GONE
                it.decorView.message.text = msg
            }
        }
    }
}