package ir.lifeplus.gamenethelper.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.robinhood.spark.SparkAdapter
import ir.lifeplus.gamenethelper.databinding.FragmentSettingBinding
import ir.lifeplus.gamenethelper.model.StatisticItem
import ir.lifeplus.gamenethelper.model.database
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class SettingFragment : Fragment() {
    lateinit var binding: FragmentSettingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi", "WeekBasedYear")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val table = database.getdb(view.context).StatisticDao
        val nowDate = SimpleDateFormat("yyyy-MM-dd", Locale("en", "IR"))
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, -7)  // 7 روز از تاریخ فعلی کم می‌کنیم
        val date7DaysAgo = calendar.time

        //val simpledata = StatisticItem(date=nowDate.format(Date()),0,0,0)
        //val simpleData = listOf<StatisticItem>(simpledata,simpledata)
        if( table.getWeekStatistic(nowDate.format(date7DaysAgo),nowDate.format(Date())).isEmpty() ){
            Log.e("amar","nemayesh namovafagh")
            Log.e("amar",table.getWeekStatistic(nowDate.format(Date()),nowDate.format(date7DaysAgo)).toString())
            //val adapter = ChartAdapter(simpleData)
            //binding.Chart.adapter = adapter
        } else {
            val adapter = ChartAdapter(
                table.getWeekStatistic(
                    nowDate.format(date7DaysAgo),
                    nowDate.format(Date())
                )
            )
            binding.Chart.adapter = adapter
            binding.txtLast.text = table.getStatisticSortInTop().last().toString()
        }
        val sdf = SimpleDateFormat("Y/M/d", Locale("en", "IR")).format(Date())
        binding.txtPrice.text = ""
        binding.txtOperators.setOnClickListener {
            val bottomSheet = ManageOperator()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

    }
}