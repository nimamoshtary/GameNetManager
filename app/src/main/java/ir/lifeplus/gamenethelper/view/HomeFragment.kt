package ir.lifeplus.gamenethelper.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.data.PieData
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.databinding.FragmentHomeBinding
import ir.lifeplus.gamenethelper.model.StatisticItem
import ir.lifeplus.gamenethelper.presenter.HomePresenter
import java.time.DayOfWeek
import java.time.LocalDate


class HomeFragment : Fragment() , ContractPV.HomeView {
    lateinit var binding: FragmentHomeBinding
    lateinit var presenter: ContractPV.HomePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        presenter = HomePresenter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.OnAttach(this, view)

        binding.TO.setOnClickListener {
            val bottomSheet = ManageOperator()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }
    }

    fun setTextViewValues(textViews: List<TextView>, values: List<String>) {
        textViews.forEachIndexed { index, textView ->
            textView.text = values.getOrNull(index) ?: "-"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun Run(item: StatisticItem, popularOP: List<Any>, mostCP: List<Any>, comfortableOP: List<Any>, mostTP: List<Any>) {
        //بهتره فرایند مدیریت شیفت و اتمام شیفت رو اضافه کنی
        val TodaysIncome = item.Pricereceived
        val TodaysProfit = TodaysIncome - item.PriceSpent
//        val TodaysExpenses = item.PriceSpent
        val RegisteredCustomerIncome = item.PriceWithPlayer

        binding.txtIncome.text = TodaysIncome.toString()
//        binding.txtExpenses.text = TodaysExpenses.toString()
        binding.txtRegisteredCustomerIncome.text = RegisteredCustomerIncome.toString()
        binding.txtCustomerCount.text = item.PlayerCount.toString()
        binding.txtFactureCount.text = item.FactureCount.toString()
        binding.txtProfit.text = TodaysProfit.toString()
        binding.txtDay.text = item.date

        binding.apply {
            setTextViewValues(
                listOf(txtBestCP1Operator, txtBestCP2Operator, txtBestCP3Operator),
                popularOP.map { it.toString() }
            )
            setTextViewValues(
                listOf(txtBestCP1Count, txtBestCP2Count, txtBestCP3Count),
                mostCP.map { it.toString() }
            )
            setTextViewValues(
                listOf(txtComfortable1, txtComfortable2, txtComfortable3),
                comfortableOP.map { it.toString() }
            )
            setTextViewValues(
                listOf(txtMostTimePlay1, txtMostTimePlay2, txtMostTimePlay3),
                mostTP.map { it.toString() }
            )
        }

        presenter.GetChart()
    }

    override fun SetChart(data: PieData) {
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = data.dataSet.label//"$Sood"
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.animateY(2000)
        binding.pieChart.animateX(1000)
        binding.pieChart.legend.isEnabled = false
    }

    override fun CheckNewWeek() {

    }
}