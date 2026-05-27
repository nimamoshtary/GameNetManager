package ir.lifeplus.gamenethelper.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.BottomsheetManageoperatorBinding
import ir.lifeplus.gamenethelper.model.OperatorItem
import ir.lifeplus.gamenethelper.presenter.ManageOperatorPresenter

//کامنت گذاری از اینجا ادامه بده
class ManageOperator : BottomSheetDialogFragment() , ContractPV.ManageOperatorView {
    lateinit var binding: BottomsheetManageoperatorBinding
    lateinit var presenter : ContractPV.ManageOperatorPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomsheetManageoperatorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = ManageOperatorPresenter()
        presenter.OnAttach(this, view)

        binding.btnAddOperator.setOnClickListener {

            show("افزودن")

            val adapterAC = ArrayAdapter(view.context, R.layout.item_nameplayer, listOf("بردگیم","کنسول") )
            (binding.autoCompleteOP.editText as AutoCompleteTextView).setAdapter(adapterAC)
            (binding.autoCompleteOP.editText as AutoCompleteTextView).setOnItemClickListener { adapterView, view, position, l ->
                val selectedItem = (binding.autoCompleteOP.editText as AutoCompleteTextView).adapter.getItem(position) as String
                when(selectedItem){
                    "کنسول" -> {
                        show("کنسول")
                    }
                    "بردگیم" -> {
                        show("بردگیم")
                    }
                }
            }

            binding.btnSubmit.setOnClickListener {
                val model = binding.autoCompleteOP.editText!!.text.toString()
                val operatorName = binding.edtStartTime.editText!!.text.toString()
                when (model) {
                    "بردگیم" -> {
                        if (isBoardGameValid()) {
                            presenter.AddOperatorBoardGame(operatorName, binding.edtPriceBG.editText!!.text.toString().toInt())
                            show("نمایش")
                        } else {
                            Toast.makeText(view.context, "شما قیمت را وارد نکردید", Toast.LENGTH_LONG).show()
                        }
                    }
                    "کنسول" -> {
                        if (isConsoleValid()) {
                            val price = listOf(binding.edtPriceCOne.editText!!.text.toString().toInt(),
                                binding.edtPriceCTwo.editText!!.text.toString().toInt(),
                                binding.edtPriceCThree.editText!!.text.toString().toInt(),
                                binding.edtPriceCFour.editText!!.text.toString().toInt())
                            presenter.AddOperatorConsol(operatorName, price)
                            show("نمایش")
                        } else {
                            Toast.makeText(view.context, "شما همه‌ی قیمت‌ها را وارد نکردید", Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> {
                        Toast.makeText(view.context, "شما مدل اپراتور را انتخاب نکردید", Toast.LENGTH_LONG).show()
                        null
                    }
                }
            }
        }
    }

    override fun Run(table: List<OperatorItem>) {

        val adapterPlayer = GenericAdapter<OperatorItem>(
            data = table,
            layoutId = R.layout.item_operator,
            bind = { view, item ->
                view.findViewById<TextView>(R.id.txt_Title).text = item.Operator

                if(item.Model == "بردگیم") {
                    view.findViewById<ImageView>(R.id.img_Operator).setImageResource(R.drawable.ic_operatorboardgame)
                } else {
                    view.findViewById<ImageView>(R.id.img_Operator).setImageResource(R.drawable.ic_operatorconsol)
                }
            },
            onClick = { /* فرایند نمایش اطلاعات و آمار اپراتور */ },
            onLongClick = { /* فرایند ویرایش اپراتور */ }
        )

        binding.RecyclerOperator.adapter = adapterPlayer
        binding.RecyclerOperator.layoutManager = GridLayoutManager(requireView().context, 3, RecyclerView.VERTICAL, false)

    }

    fun show(layout: String) {
        when(layout) {
            "بردگیم" -> {
                binding.BoardGame.visibility = View.VISIBLE
                binding.Console.visibility = View.GONE

                binding.edtPriceBG.editText!!.text.clear()
            }
            "کنسول" -> {
                binding.Console.visibility = View.VISIBLE
                binding.BoardGame.visibility = View.GONE

                binding.edtPriceCOne.editText!!.text.clear()
                binding.edtPriceCTwo.editText!!.text.clear()
                binding.edtPriceCThree.editText!!.text.clear()
                binding.edtPriceCFour.editText!!.text.clear()
            }
            "نمایش" -> {
                binding.layoutShow.visibility = View.VISIBLE
                binding.layoutAdd.visibility = View.GONE

                presenter.OnAttach(this, requireView())
            }
            "افزودن" -> {
                binding.layoutShow.visibility = View.GONE
                binding.layoutAdd.visibility = View.VISIBLE

                binding.BoardGame.visibility = View.GONE
                binding.Console.visibility = View.GONE

                binding.edtStartTime.editText!!.text.clear()
                binding.autoCompleteOP.editText!!.text.clear()
            }
        }
    }
    fun isBoardGameValid() =
        binding.edtPriceBG.editText!!.text.isNotEmpty() &&
                binding.autoCompleteOP.editText!!.text.isNotEmpty()
    fun isConsoleValid() =
        binding.edtPriceCOne.editText!!.text.isNotEmpty() &&
                binding.edtPriceCTwo.editText!!.text.isNotEmpty() &&
                binding.edtPriceCThree.editText!!.text.isNotEmpty() &&
                binding.edtPriceCFour.editText!!.text.isNotEmpty() &&
                binding.autoCompleteOP.editText!!.text.isNotEmpty()
}