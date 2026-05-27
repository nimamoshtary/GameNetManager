package ir.lifeplus.gamenethelper.view

import android.R.attr.height
import android.R.attr.width
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.DialogPlayerBinding
import ir.lifeplus.gamenethelper.databinding.FragmentPlayerBinding
import ir.lifeplus.gamenethelper.model.PlayerItem
import ir.lifeplus.gamenethelper.presenter.PlayerPresenter


class PlayerFragment : Fragment() , ContractPV.PlayerView{
    lateinit var binding: FragmentPlayerBinding
    lateinit var presenter : ContractPV.PlayerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPlayerBinding.inflate(layoutInflater, container, false)
        presenter = PlayerPresenter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.OnAtttach(this, view)

        binding.fab.setOnClickListener {
            val alert = AlertDialog.Builder(context).create()
            val dialogs = DialogPlayerBinding.inflate(layoutInflater)
            alert.setView(dialogs.root)
            alert.setCancelable(true)
            alert.show()
            dialogs.btnSubmit.setOnClickListener {
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
            }
        }
    }

    override fun Run(Data: List<PlayerItem>) {

        val adapterPlayer = GenericAdapter<PlayerItem>(
            data = Data,
            layoutId = R.layout.item_player,
            bind = { view, item ->
                view.findViewById<TextView>(R.id.txt_PlayerName).text = item.Name
                view.findViewById<TextView>(R.id.txt_PlayedTime).text = item.PlayedTimeH
                view.findViewById<TextView>(R.id.txt_PayedPrice).text = item.PayedPrice.toString()
                // در نسخه ی 1.0.3 در صورت بودن پلیر در مرکز، آیتم آن به حالت آنلاین تغییر خواهد کرد
            },
            onClick = { /* در نسخه ی 1.0.1 اطلاعات پلیر نشان داده خواهد شد */ },
            onLongClick = { /* در نسخه ی 1.0.2 قابلیت ویرایش پلیر خواهد آمد */ }
        )

        binding.RcyclerPlayer.adapter = adapterPlayer
        binding.RcyclerPlayer.layoutManager = GridLayoutManager(requireView().context, 3, RecyclerView.VERTICAL,false)
    }
}
