package com.zhhl.entry_exit.tour.adapter

import android.app.AlertDialog
import android.view.View
import c.feature.recyclerview.swipemenu.BaseRecyclerViewAdapter
import com.zhhl.entry_exit.tour.DialogFactory
import com.zhhl.entry_exit.tour.R
import kotlinx.android.synthetic.main.item_take.view.*

class TakeAdapter : BaseRecyclerViewAdapter<String, TakeAdapter.TakeViewHolder>() {

    private  var dialog :AlertDialog?=null
    override fun createViewHolder(view: View):TakeViewHolder{
        if (dialog==null){
            dialog=DialogFactory.success(view.context,"数据归档中...")
        }
        return TakeViewHolder(this, view)
    }

    override fun layoutId() = R.layout.item_take

    class TakeViewHolder(adapter: TakeAdapter, view: View) : BaseRecyclerViewAdapter.RecyclerViewHolder(adapter, view) {
        val history_file=view.history_file
        init {
            history_file.setOnClickListener {
                adapter.dialog?.let {
                    it.show()
                    history_file.postDelayed({
                        DialogFactory.progressToSuccess(it,"数据已归档")
                        history_file.postDelayed({
                            DialogFactory.toProgress(it,"数据归档中...")
                            it.dismiss()
                        },3000)
                    },3000)

                }
            }
        }
    }
}