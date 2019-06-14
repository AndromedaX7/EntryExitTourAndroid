package com.zhhl.entry_exit.tour

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import c.feature.autosize.AutoAdaptSize
import c.feature.autosize.ComplexUnit
import com.google.android.material.tabs.TabLayout
import com.zhhl.entry_exit.tour.adapter.TakeAdapter
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*

class HistoryActivity : AppCompatActivity(), AutoAdaptSize {

    override fun complexUnit() = ComplexUnit.PT
    override fun designSize() = 320

    var dialog: AlertDialog? = null;
    private var endFlag = false
    private var startDate = ""
    private var endDate = ""
    private var datePickerDialog: DatePickerDialog? = null;
    private val adapter = TakeAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        transparentStatus(Color.WHITE)
        if (dialog == null) {
            dialog = DialogFactory.success(this, "正在获取数据...")
        }
        AppCache.waterMark(this)
        val instance = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                if (endFlag) {
                    endDate = DateUtil.format("yyyy/MM/dd", GregorianCalendar(year, month, dayOfMonth).time.time)
                    dateEndContent.text = endDate
                } else {
                    startDate = DateUtil.format("yyyy/MM/dd", GregorianCalendar(year, month, dayOfMonth).time.time)
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
                queryCodeData(searchOperationCode.text.toString())
            }
            return@setOnEditorActionListener true
        }

        searchOperationCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.isEmpty()) {
                        scanResult.isErrorEnabled = true
                        scanResult.error = "请输入业务编码"
                    } else if (it.length != 15) {
                        scanResult.isErrorEnabled = true
                        scanResult.error = "业务编码长度不正确"
                    } else {
                        scanResult.isErrorEnabled = false
                    }
                }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        adapter.toFileCallback = {
            dialog?.show()
            val okHttpDSL = HttpDSL()
            okHttpDSL {
                requestDescription {
                    uri = "http://192.168.20.228:7098/crjInterface/ReloudImghx.do"
                    body = "ywbh=$it&sbid=${AppCache.loginBean?.userInfo?.code}"
                    mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
                    method = Method.POST
                    Log.e("body", body)
                }
                callType(ReloadResult::class.java, {
                    if (it.success == 1) {
                        DialogFactory.progressToSuccess(dialog!!, "数据已更新")
                        mBack.postDelayed({
                            DialogFactory.toProgress(dialog!!, "正在获取数据...")
                            dialog?.dismiss()
                        }, 3000)
                    } else {
                        DialogFactory.progressToFailed(dialog!!, "数据更新失败")
                        mBack.postDelayed({
                            DialogFactory.toProgress(dialog!!, "正在获取数据...")
                            dialog?.dismiss()
                        }, 3000)
                    }
                }, {
                    it.printStackTrace()
                    DialogFactory.progressToFailed(dialog!!, "数据更新失败")
                    mBack.postDelayed({
                        DialogFactory.toProgress(dialog!!, "正在获取数据...")
                        dialog?.dismiss()
                    }, 3000)
                })
            }
        }

        toFilter.setOnClickListener {
            filterContent.visibility = View.VISIBLE
            mList.visibility = View.GONE
            tabs.setSelectedTabIndicatorHeight(0)
        }

        filterCancel.setOnClickListener {
            filterContent.visibility = View.GONE
            mList.visibility = View.VISIBLE
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
                when (p0?.position) {
                    0 -> queryAllData()
                    1 -> queryTodayData()
                }
                p0?.let {
                    tip.text = "${it.text}共${adapter.itemCount}条"
                    filterContent.visibility = View.GONE
                    mList.visibility = View.VISIBLE
                }
                tabs.setSelectedTabIndicatorHeight(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_PT,
                        2f,
                        resources.displayMetrics
                    ).toInt()
                )

                filterContent.visibility = View.GONE
                mList.visibility = View.VISIBLE
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                filterContent.visibility = View.GONE
                mList.visibility = View.VISIBLE
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> queryAllData()
                    1 -> queryTodayData()
                }
                p0?.let {
                    tip.text = "${it.text}共${adapter.itemCount}条"
                    filterContent.visibility = View.GONE
                    mList.visibility = View.VISIBLE
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
            if (DateUtil.parseDate("yyyy/MM/dd", startDate) - DateUtil.parseDate("yyyy/MM/dd", endDate) > 0) {
                toast("起始日期不可以早于截止日期")
                clearDateInfo()
                return@setOnClickListener
            }
            if (DateUtil.parseDate("yyyy/MM/dd", startDate) - System.currentTimeMillis() > 0) {
                toast("起始日期不可以超过今天")
                clearDateInfo()
                return@setOnClickListener
            }
            if (DateUtil.parseDate("yyyy/MM/dd", endDate) - System.currentTimeMillis() > 0) {
                toast("截止日期不可以超过今天")
                clearDateInfo()
                return@setOnClickListener
            }
            filterContent.visibility = View.GONE
            mList.visibility = View.VISIBLE
            queryDataRange()
            clearDateInfo()
        }
        queryAllData()
        mList.adapter = adapter
        mList.layoutManager = LinearLayoutManager(this)
        tip.text = "全部共${adapter.itemCount}条"
    }

    private fun queryCodeData(code: String) {
        dialog?.show()
        val okHttpDSL = HttpDSL()
        okHttpDSL {
            requestDescription {
                uri = "http://192.168.20.228:7098/crjInterface/selecRyxx.do"
                method = Method.POST
                body = "ywbh=$code&sbid=${AppCache.loginBean?.userInfo?.code}"
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }


            callType(SearchData::class.java, {

                if (it.code == "1")
                    adapter.clearAdd(it.data as ArrayList<SearchData.DataBean>)
                else {
                    adapter.clear()
                }
                tip.text = "共${adapter.itemCount}条"
                searchOperationCode.setText("")
                dialog?.dismiss()
                empty()
            }, {
                searchOperationCode.setText("")
                it.printStackTrace()
                dialog?.dismiss()
                adapter.clear()
                empty()
            })
        }

    }

    private fun queryDataRange() {
        dialog?.show()
        val okHttpDSL = HttpDSL()
        okHttpDSL {
            requestDescription {
                uri = "http://192.168.20.228:7098/crjInterface/selecRyxxbytime.do"
                method = Method.POST
                body = "sbid=${AppCache.loginBean?.userInfo?.code}&strtime=$startDate&endtime=$endDate"
                Log.e("tag,", body)
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
            }
            callType(SearchData::class.java, {
                if (it.code == "1")
                    adapter.clearAdd(it.data as ArrayList<SearchData.DataBean>)
                else
                    adapter.clear()
                tip.text = "共${adapter.itemCount}条"
                dialog?.dismiss()
                empty()
            }, {
                it.printStackTrace()
                dialog?.dismiss()
                adapter.clear()
                empty()
            })
        }

    }

    private fun queryAllData() {
        dialog?.show()
        val okHttpDSL = HttpDSL()
        okHttpDSL {
            requestDescription {
                uri = "http://192.168.20.228:7098/crjInterface/selecRyxxbytime.do"
                method = Method.POST
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
                body = "sbid=${AppCache.loginBean?.userInfo?.code}"
                Log.e("tag,", body)
            }
            callType(SearchData::class.java, {
                if (it.code == "1")
                    adapter.clearAdd(it.data as ArrayList<SearchData.DataBean>)
                else
                    adapter.clear()
                tip.text = "全部共${adapter.itemCount}条"
                dialog?.dismiss()
                empty()
            }, {
                it.printStackTrace()
                dialog?.dismiss()
                adapter.clear()
                empty()
            })
        }

    }

    private fun queryTodayData() {

        dialog?.show()
        val okHttpDSL = HttpDSL()
        okHttpDSL {
            requestDescription {
                body = "sbid=${AppCache.loginBean?.userInfo?.code}&strtime=${DateUtil.format(
                    "yyyy/MM/dd",
                    System.currentTimeMillis()
                )}&endtime=${DateUtil.format("yyyy/MM/dd", System.currentTimeMillis())}"
                uri = "http://192.168.20.228:7098/crjInterface/selecRyxxbytime.do"
                method = Method.POST
                mimeType = MimeType.APPLICATION_X_FORM_URLENCODED
                Log.e("tag,", body)
            }
            callType(SearchData::class.java, {
                if (it.code == "1")
                    adapter.clearAdd(it.data as ArrayList<SearchData.DataBean>)
                else
                    adapter.clear()
                tip.text = "今日办理共${adapter.itemCount}条"
                dialog?.dismiss()
                empty()
            }, {
                it.printStackTrace()
                dialog?.dismiss()
                adapter.clear()
                empty()
            })
        }

    }


    fun empty() {
        if (adapter.itemCount <= 0) {
            place_holder.visibility = View.VISIBLE
            mList.visibility = View.GONE
        } else {
            place_holder.visibility = View.GONE
            mList.visibility = View.VISIBLE
        }
    }


    private fun clearDateInfo() {
        startDate = ""
        endDate = ""
        dateStartContent.text = "起始日期"
        dateEndContent.text = "截止日期"
    }
}
