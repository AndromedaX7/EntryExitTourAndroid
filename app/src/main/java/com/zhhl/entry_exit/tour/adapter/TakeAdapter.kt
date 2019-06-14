package com.zhhl.entry_exit.tour.adapter

import android.util.Base64
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.zhhl.entry_exit.tour.R
import com.zhhl.entry_exit.tour.SearchData
import kotlinx.android.synthetic.main.item_take.view.*

class TakeAdapter : BaseRecyclerViewAdapter<SearchData.DataBean, TakeAdapter.TakeViewHolder>() {


    override fun createViewHolder(view: View): TakeViewHolder {
        return TakeViewHolder(this, view)
    }


    override fun onBindViewHolder(vh: TakeViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        vh.name.text = "${data[position].name}/${data[position].sfzh}"
        Log.e("name", data[position].name)
        vh.idCode.text = data[position].ywbh
        vh.time.text = data[position].createtime
        Glide.with(vh.icon).load(Base64.decode(data[position].zzzp, Base64.DEFAULT)).into(vh.icon)
    }

    override fun layoutId() = R.layout.item_take

    class TakeViewHolder(adapter: TakeAdapter, view: View) : BaseRecyclerViewAdapter.RecyclerViewHolder(adapter, view) {
        val name = view.name
        val idCode = view.idCode
        val time = view.time
        val icon = view.icon
        val toFile = view.toFile

        init {
            toFile.setOnClickListener {
                adapter.toFileCallback?.let {
                    it(adapter.data[layoutPosition].ywbh)
                }
            }
        }
    }

    var toFileCallback: ((String) -> Unit)? = null
}