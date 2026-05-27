package ir.lifeplus.gamenethelper.view

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

//Fragment connected to the fragment_home.xml layout
class HomeFragment : Fragment() , ContractPV.HomeView {

    //Connecting the fragment to the layout (View) and the presenter (and then the model)
    lateinit var binding: FragmentHomeBinding
    lateinit var presenter: ContractPV.HomePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = HomePresenter()
        //Attach the presenter in order to start the process of displaying the contents of this fragment in the view
        presenter.OnAttach(this, view)

        //Show the ManageOperator BottomSheet
        binding.btnManageoperator.setOnClickListener {

            val bottomSheet = ManageOperator()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)

        }

    }

    //Receive data from presenter and display it with the items
    override fun Run(item: StatisticItem, popularOP: List<Any>, mostCP: List<Any>, comfortableOP: List<Any>, mostTP: List<Any>) {

        //Calculate profit and income generated from registered customers
        val TodaysIncome = item.Pricereceived
        val TodaysProfit = TodaysIncome - item.PriceSpent
        val RegisteredCustomerIncome = item.PriceWithPlayer

/*        val TodaysExpenses = item.PriceSpent فعلا مخارج ثبت نمیشه که ضرر هم بکنیم
        binding.txtExpenses.text = TodaysExpenses.toString() */

        // Setting data into the views and displaying them
        binding.apply {

            txtIncome.text = TodaysIncome.toString()
            txtRegisteredCustomerIncome.text = RegisteredCustomerIncome.toString()
            txtCustomerCount.text = item.PlayerCount.toString()
            txtFactureCount.text = item.FactureCount.toString()
            txtProfit.text = TodaysProfit.toString()
            txtDay.text = item.date

            setTextValues(
                listOf(txtBestCP1Operator, txtBestCP2Operator, txtBestCP3Operator),
                popularOP.map { it.toString() }
            )
            setTextValues(
                listOf(txtBestCP1Count, txtBestCP2Count, txtBestCP3Count),
                mostCP.map { it.toString() }
            )
            setTextValues(
                listOf(txtComfortable1, txtComfortable2, txtComfortable3),
                comfortableOP.map { it.toString() }
            )
            setTextValues(
                listOf(txtMostTimePlay1, txtMostTimePlay2, txtMostTimePlay3),
                mostTP.map { it.toString() }
            )
        }

        //Calculating the required data for rendering the chart
        presenter.GetChart()

        //Note: در نسخه ی بعدی مدیریت شیفت را راه اندازی کن
    }

    // Rendering the chart based on the input 'datas' received from the presenter
    override fun SetChart(datas: PieData) {

        binding.pieChart.apply {
            data = datas
            description.isEnabled = false
            centerText = datas.dataSet.label
            setCenterTextSize(16f)
            animateY(2000)
            animateX(1000)
            legend.isEnabled = false
        }

    }

    // Setting the TextViews based on the intered values, in order
    override fun setTextValues(textViews: List<TextView>, values: List<String>) {

        textViews.forEachIndexed { index, textView ->
            textView.text = values.getOrNull(index) ?: "-"
        }

    }
    //Detaching and disconnecting from the presenter, and restarting the data stored in presenter
    override fun onDetach() {
        super.onDetach()

        presenter.OnDetach()

    }

}