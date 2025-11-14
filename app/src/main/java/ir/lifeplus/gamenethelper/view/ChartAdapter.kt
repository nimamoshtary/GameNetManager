package ir.lifeplus.gamenethelper.view

import com.robinhood.spark.SparkAdapter
import ir.lifeplus.gamenethelper.model.StatisticItem

class ChartAdapter(val data:List<StatisticItem>):SparkAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(index: Int): Any {
        return data[index]
    }

    override fun getY(index: Int): Float {
        return data[index].Pricereceived.toFloat()
    }
}