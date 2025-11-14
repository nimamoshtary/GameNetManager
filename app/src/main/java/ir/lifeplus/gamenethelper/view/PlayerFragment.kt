package ir.lifeplus.gamenethelper.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.DialogPlayerBinding
import ir.lifeplus.gamenethelper.databinding.FragmentPlayerBinding
import ir.lifeplus.gamenethelper.model.PlayerDao
import ir.lifeplus.gamenethelper.model.PlayerItem
import ir.lifeplus.gamenethelper.presenter.PlayerPresenter


class PlayerFragment : Fragment() , /*AdapterRecycler.Transferdata,*/ ContractPV.PlayerView{
    lateinit var binding: FragmentPlayerBinding
    //lateinit var adapter : AdapterRecycler
    lateinit var presenter : ContractPV.PlayerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlayerBinding.inflate(layoutInflater, container, false)
        presenter = PlayerPresenter()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.OnAtttach(this, view)

        binding.fab.setOnClickListener {
            val alert = AlertDialog.Builder(context).create()
            val dialogs = DialogPlayerBinding.inflate(layoutInflater)
            alert.setView(dialogs.root)
            alert.setCancelable(true)
            alert.show()
            dialogs.btnOk.setOnClickListener {
                val name = dialogs.edtFullname.editText!!.text.toString().trim()
                val number = dialogs.edtNumber.editText!!.text.toString().trim()
                val password = if(dialogs.edtPassword.visibility == View.VISIBLE){ dialogs.edtPassword.editText!!.text.toString().trim() } else { dialogs.edtFullname.editText!!.text.toString().trim() }
                val inviter = dialogs.edtInviter.editText?.text.toString().trim()

                if(name.isEmpty() || number.isEmpty()) {
                    Toast.makeText(context, "لطفاً فیلدهای ضروری را پر کنید!", Toast.LENGTH_SHORT).show()
                }else{
                    val ActionPlayer = PlayerItem(
                        Name = name,
                        Phone = number.toLong(),
                        Username = number,
                        Inviter = inviter)
                    presenter.AddPlayer(ActionPlayer)
                    alert.dismiss()
                }
                //binding.RcyclerPlayer.smoothScrollToPosition(binding.RcyclerPlayer.size - 1) //میشه Invalid target position این باعث ارور
            }
        }
    }

    /*override*/ fun clicked() = Unit
    /*override*/ fun longcliked(playerItem: PlayerItem) { }

    override fun Run(table: PlayerDao) {

        val adapterPlayer = GenericAdapter<PlayerItem>(
            data = table.getAllPlayer(),
            layoutId = R.layout.item_player,
            bind = { view, item ->
                view.findViewById<TextView>(R.id.txt_Title).text = item.Name
                view.findViewById<TextView>(R.id.txt_PlayedTime).text = item.PlayedTimeH
                view.findViewById<TextView>(R.id.txt_PricePayed).text = item.PayedPrice.toString()
            },
            onClick = { clicked() },
            onLongClick = { player -> longcliked(player) }
        )

        binding.RcyclerPlayer.adapter = adapterPlayer
        binding.RcyclerPlayer.layoutManager = GridLayoutManager(requireView().context, 3, RecyclerView.VERTICAL,false)
    }
}
// از این صفحه افزودن حالت آنلاین و ادیت پلیر و دیدن اطلاعات پلیر باقی موند که میسپرم به نسخه ی بعدی...