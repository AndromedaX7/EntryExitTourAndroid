package com.zhhl.entry_exit.tour

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import c.feature.autosize.AutoAdaptSize
import c.feature.autosize.ComplexUnit
import c.feature.dsl.okhttp.Method
import c.feature.dsl.okhttp.MimeType
import c.feature.dsl.okhttp.OkHttpDSL
import c.feature.extension.toast
import c.feature.extension.transparentStatus
import com.google.android.material.tabs.TabLayout
import com.zhhl.entry_exit.tour.adapter.TakeAdapter
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*

class HistoryActivity : AppCompatActivity(), AutoAdaptSize {

    override fun complexUnit() = ComplexUnit.PT
    override fun designSize() = 320

    private var endFlag = false
    private var startDate = ""
    private var endDate = ""
    private var datePickerDialog: DatePickerDialog? = null;
    private val adapter = TakeAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        transparentStatus(Color.WHITE)
        val instance = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                if (endFlag) {
                    endDate = DateUtil.format("yyyy-MM-dd", GregorianCalendar(year, month, dayOfMonth).time.time)
                    dateEndContent.text = endDate
                } else {
                    startDate = DateUtil.format("yyyy-MM-dd", GregorianCalendar(year, month, dayOfMonth).time.time)
                    dateStartContent.text = startDate
                }
            }, instance[Calendar.YEAR], instance[Calendar.MONTH], instance[Calendar.DATE]
        )
        mBack.setOnClickListener {
            finish()
        }
        mBack2.setOnClickListener {
            finish()
        }
        searchOperationCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            }
            return@setOnEditorActionListener true
        }

        toFilter.setOnClickListener {
            filterContent.visibility = View.VISIBLE
            mList.visibility=View.GONE
            tabs.setSelectedTabIndicatorHeight(0)
        }

        filterCancel.setOnClickListener {
            filterContent.visibility = View.GONE
            mList.visibility=View.VISIBLE
            tabs.setSelectedTabIndicatorHeight(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PT,
                    2f,
                    resources.displayMetrics
                ).toInt()
            )
        }

        tabs.tabMode = TabLayout.MODE_FIXED
        tabs.addTab(tabs.newTab().setText("全部"))
        tabs.addTab(tabs.newTab().setText("今日办理"))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                tabs.setSelectedTabIndicatorHeight(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PT,
                        2f,
                        resources.displayMetrics
                    ).toInt()
                )
                filterContent.visibility = View.GONE
                mList.visibility=View.VISIBLE
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                filterContent.visibility = View.GONE
                mList.visibility=View.VISIBLE
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.let {
                    tip.text = "${it.text}共${adapter.itemCount}条"
                    filterContent.visibility = View.GONE
                    mList.visibility=View.VISIBLE
                }
                tabs.setSelectedTabIndicatorHeight(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PT,
                        2f,
                        resources.displayMetrics
                    ).toInt()
                )
            }

        })


        dateStart.setOnClickListener {
            endFlag = false
            datePickerDialog!!.show()
        }
        dateEnd.setOnClickListener {
            endFlag = true
            datePickerDialog!!.show()
        }

        filter.setOnClickListener {
            if (startDate.isEmpty() || endDate.isEmpty()) {
                toast("请设置起止日期")
                return@setOnClickListener
            }
            if (DateUtil.parseDate("yyyy-MM-dd", startDate) - DateUtil.parseDate("yyyy-MM-dd", endDate) > 0) {
                toast("起始日期不可以早于截止日期")
                clearDateInfo()
                return@setOnClickListener
            }
            if (DateUtil.parseDate("yyyy-MM-dd", startDate) - System.currentTimeMillis() > 0){
                toast("起始日期不可以超过今天")
                clearDateInfo()
                return@setOnClickListener
            }
            if (DateUtil.parseDate("yyyy-MM-dd", endDate) - System.currentTimeMillis() > 0){
                toast("截止日期不可以超过今天")
                clearDateInfo()
                return@setOnClickListener
            }
            filterContent.visibility = View.GONE
            mList.visibility=View.VISIBLE
            clearDateInfo()
        }

        mList.adapter = adapter
        mList.layoutManager = LinearLayoutManager(this)
        adapter.clearAdd(arrayListOf("1", "2", "3", "4"))

        tip.text = "全部共${adapter.itemCount}条"
    }


    private fun clearDateInfo() {
        startDate = ""
        endDate = ""
        dateStartContent.text = "起始日期"
        dateEndContent.text = "截止日期"
    }

    private fun searchOperationCode() {
        val okhttp = OkHttpDSL()
        okhttp {
            requestDescription {
                uri = ""
                method = Method.POST
                body = ""
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }
            callString({}, {})
        }
    }
}
