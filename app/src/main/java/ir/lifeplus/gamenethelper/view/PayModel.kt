package ir.lifeplus.gamenethelper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.BottomsheetPaymodelBinding
import ir.lifeplus.gamenethelper.model.FactureItem
import ir.lifeplus.gamenethelper.presenter.PayModelPresenter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PayModel : BottomSheetDialogFragment() , ContractPV.PayModelView{
    lateinit var binding: BottomsheetPaymodelBinding
    lateinit var presenter: ContractPV.PayModelPresenter
    companion object {
        private const val ARG_ID = "arg_id"

        fun newInstance(itemId: Int): PayModel {
            val fragment = PayModel()
            val args = Bundle()
            args.putInt(ARG_ID, itemId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomsheetPaymodelBinding.inflate(layoutInflater, container, false)
        presenter = PayModelPresenter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val SDF = SimpleDateFormat("yyyy-MM-dd", Locale("en", "IR")).format(Date())
        binding.txtDate.text = SDF

        presenter.OnAttach(this, view, arguments?.getInt(ARG_ID)!!)

        var Status = "money"
        binding.RadioGroup.check(R.id.rb_PayMoney)
        binding.RadioGroup.setOnCheckedChangeListener { _ , id ->
            when(id){
                R.id.rb_PayMoney -> {
                    binding.apply {
                        rbPayMoney.alpha = 0.7F
                        rbPayElectronic.alpha = 1F
                        btnCheckPay.text = "تایید پرداخت نقدی"
                        Status = "money"
                    }
                }
                R.id.rb_PayElectronic -> {
                    binding.apply {
                        rbPayMoney.alpha = 1F
                        rbPayElectronic.alpha = 0.7F
                        btnCheckPay.text = "تایید پرداخت اعتباری"
                        Status = "Electronic"
                    }
                }
            }
        }

        binding.btnCheckPay.setOnClickListener {
            presenter.PayFacture( arguments?.getInt(ARG_ID)!! ,Status )
            dismiss()
        }

        binding.exit.setOnClickListener {
            dismiss()
        }



    }

    override fun Run(Facture: FactureItem) {

        val price = Facture.Price - Facture.PayedPrice
        binding.txtPriceFacture.text = price.toString()

    }

    override fun onDetach() {
        super.onDetach()
        presenter.OnDetach()
    }
}