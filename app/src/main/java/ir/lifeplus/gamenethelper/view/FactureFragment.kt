package ir.lifeplus.gamenethelper.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.DialogFactureBinding
import ir.lifeplus.gamenethelper.databinding.FragmentFactureBinding
import ir.lifeplus.gamenethelper.model.Converters
import ir.lifeplus.gamenethelper.model.FactureItem
import ir.lifeplus.gamenethelper.model.PlayerItem
import ir.lifeplus.gamenethelper.presenter.FacturePresenter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import android.graphics.Shader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import kotlin.math.hypot


class FactureFragment : Fragment() , ContractPV.FactureView {
    lateinit var binding: FragmentFactureBinding
    lateinit var presenter : ContractPV.FacturePresenter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFactureBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = FacturePresenter()
        presenter.OnAtttach(this, view)

        // یه ویوپیجر کار بزار که با اسرول کردن به سمت چپ یا راست بره به روز قبل و بعد

        binding.fab.setOnClickListener {
            val alert = AlertDialog.Builder(context).create()
            val dialogs = DialogFactureBinding.inflate(layoutInflater)
            alert.setView(dialogs.root)
            alert.setCancelable(true)
            alert.show()

            val sdf = SimpleDateFormat( "HH:mm", Locale("en", "IR") ).format( Date() )
            dialogs.edtStartTime.editText!!.setText( sdf.toString() )
            dialogs.RGPlayerStatus.check(R.id.RB_AnyPlayer)


            dialogs.RGPlayerStatus.setOnCheckedChangeListener { radioGroup, i ->
                when (i) {
                    R.id.RB_FamilierPlayer -> {
                        dialogs.ACPlayer.visibility = View.VISIBLE

                        fun Int.toPx(context: Context): Int =
                            (this * context.resources.displayMetrics.density).toInt()
                        val marginInDp = 55
                        val marginInPx = marginInDp.toPx(view.context)
                        val params = dialogs.btnOk.layoutParams as ConstraintLayout.LayoutParams
                        params.topMargin = marginInPx
                        dialogs.btnOk.layoutParams = params
                    }
                    R.id.RB_AnyPlayer -> {
                        dialogs.ACPlayer.visibility = View.GONE
                        presenter.ClearPlayersList()
                        dialogs.RVPlayerName.visibility = View.GONE

                        fun Int.toPx(context: Context): Int =
                            (this * context.resources.displayMetrics.density).toInt()
                        val marginInDp = 0
                        val marginInPx = marginInDp.toPx(view.context)
                        val params = dialogs.btnOk.layoutParams as ConstraintLayout.LayoutParams
                        params.topMargin = marginInPx
                        dialogs.btnOk.layoutParams = params
                    }
                }
            }

            val NamePlayerList = presenter.GetPlayerName()
            val adapterAC = ArrayAdapter(dialogs.root.context, R.layout.item_nameplayer, NamePlayerList)
            (dialogs.ACPlayer.editText as AutoCompleteTextView).setAdapter(adapterAC)
            (dialogs.ACPlayer.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->
                val name = adapterAC.getItem(position) ?: return@setOnItemClickListener
                presenter.AddPlayerToList(name,dialogs)
            }

            val adapter = ArrayAdapter(dialogs.root.context, R.layout.item_nameplayer, presenter.GetOperatorsName())
            (dialogs.ACOperator.editText as AutoCompleteTextView).setAdapter(adapter)

            dialogs.btnOk.setOnClickListener {
                if(dialogs.edtStartTime.editText!!.text.isNotEmpty() && dialogs.ACOperator.editText!!.text.isNotEmpty() ) {
                    presenter.AddNewFacture(dialogs)
                    alert.dismiss()
                    Toast.makeText(view.context, "فاکتور با موفقیت ساخته شد", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(view.context, "زمان شروع یا اسم اپراتور را وارد نکردید!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun Run(table: List<FactureItem>) {

        val adapterFacture = GenericAdapter<FactureItem>(
            data = table,
            layoutId = R.layout.item_facture,
            bind = { viewA, item -> //آیتم طبق فیگما نیاز به باز طراحی دارد

                val leftColor = if (item.Price > item.PayedPrice) Color.parseColor("#DC1010") else Color.parseColor("#1674CC")
                val rightColor = if (presenter.GetOperatorByName(item.Operator).Model == "کنسول") Color.parseColor("#135492") else Color.parseColor("#D97614")

                val gradientDrawable = AngledGradientDrawable(50.0, leftColor, rightColor)
                viewA.background = gradientDrawable

                val PauseButton = viewA.findViewById<Button>(R.id.btn_Pause) // وقتی آیتم متوقف شده نشون داده نشه
                val PlayButton = viewA.findViewById<Button>(R.id.btn_Play)
                val FinishButton = viewA.findViewById<Button>(R.id.btn_Finish)

                val firstPlayer = Converters().toList(item.Players.toString()).firstOrNull() ?: "مشتری جدید"

                val onPaidPrice = item.PayedPrice - item.Price
                val priceTextColor = if( onPaidPrice >= 0) Color.GREEN else Color.RED

                val map = mapOf(
                    R.id.txt_OnePlayer to firstPlayer,
                    R.id.txt_StartTime to item.StartTime,
                    R.id.txt_EndTime to (item.EndTime ?: "").ifEmpty { " - " },
                    R.id.txt_PlayedTimeM to item.PlayedTimeH,
                    R.id.txt_PlayerCount to item.PlayerCount.toString(),
                    R.id.txt_Players to item.Players,
                    R.id.txt_Price to item.Price.toString(),
                    R.id.txt_Games to item.GamesName,
                    R.id.txt_Operator to item.Operator
                )
                map.forEach { (id, value) ->
                    viewA.findViewById<TextView>(id).text = value
                }
                viewA.findViewById<TextView>(R.id.txt_Price).setTextColor(priceTextColor)

                val isPaused = !item.PausedTime.isNullOrEmpty()
                val isFinished = !item.EndTime.isNullOrEmpty()

//                    visibility = if (isPaused) View.VISIBLE else View.INVISIBLE
//                    if (isPaused) {
//                        val elapsed = System.currentTimeMillis() - item.PausedTime!!.toLong()
//                        text = String.format(Locale.US, "%.2f", elapsed / 60000.0)
//                        PlayButton.visibility = View.VISIBLE
//                        FinishButton.visibility = View.INVISIBLE
//                    }

                FinishButton.visibility = if(isFinished) {View.INVISIBLE} else { if(isPaused) { View.INVISIBLE } else { View.VISIBLE} }
                PlayButton.visibility = if(isPaused) View.VISIBLE else View.INVISIBLE
                PauseButton.visibility = if(isFinished) { View.INVISIBLE } else { if(isPaused) { View.INVISIBLE } else { View.VISIBLE} }
                PauseButton.setOnClickListener {
                    StartPause(item)
                    PlayButton.visibility = View.VISIBLE
                    PauseButton.visibility = View.INVISIBLE
                    FinishButton.visibility = View.INVISIBLE
                }

                PlayButton.setOnClickListener {
                    //مسئله اینه که اگه فاکتور برای دیروز باشه و این توقف خواسته بشه که تموم بشه، نسبت به زمان لحظه ممکنه تایم رو بد محاسبه کنه
                    //یعنی باید روی بستن شیفت تاکید کنی و زودتر اجراش کنی

                    //مسئله دیگه اگه زده شد، چک کن ببین تایم تموم شده و میخواد استارت کنه یا توقف رو میخواد تموم کنه

                    //PauseText.visibility = View.INVISIBLE
                    PlayButton.visibility = View.INVISIBLE
                    //PauseText.visibility = View.VISIBLE
                    FinishButton.visibility = View.VISIBLE

                    EndPause(item)
                }

                FinishButton.setOnClickListener {
                    if (isFinished) {
                        Toast.makeText(viewA.context, "ناموفق!!\nساعت پایان قبلاً مشخص شده\nاگر قیمت محاسبه نشده با پشیبانی در ارتباط باشید", Toast.LENGTH_LONG).show()
                    } else {
                        presenter.FinishTime(item)
                    }
                    FinishButton.visibility = View.INVISIBLE
                }
            },
            onClick = { clicked() },
            onLongClick = { LongClick(it) }
        )
        binding.RcyclerFacture.adapter = adapterFacture
        binding.RcyclerFacture.layoutManager = LinearLayoutManager(requireView().context, RecyclerView.VERTICAL,false)
    }

    override fun updatePlayers(players: ArrayList<PlayerItem>,viewd: DialogFactureBinding) {
        viewd.RVPlayerName.visibility = View.VISIBLE
        val adapterPlayerName = GenericAdapter<PlayerItem>(
            data = players,
            layoutId = R.layout.item_playervertical,
            bind = { viewA, item ->
                val Player = viewA.findViewById<TextView>(R.id.txt_TitleV)
                val NumberOfAttendance = viewA.findViewById<TextView>(R.id.txt_AttendancesNumber)
                Player.text = item.Name
                NumberOfAttendance.text = item.NumberOfAttendances.toString()
            }
        )
        viewd.RVPlayerName.adapter = adapterPlayerName
        viewd.RVPlayerName.layoutManager = LinearLayoutManager(viewd.root.context, RecyclerView.VERTICAL,false)
    }

    override fun ShowBottomSheet(bottomSheet: PayModel) {
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }
    fun clicked() { /* باز شدن صفحه ی ویرایش فاکتور */ }
    fun LongClick(item: FactureItem) {
        if(item.PayedPrice < item.Price) {
            presenter.ShowPayModel(item.id!!)
        } else {
            Toast.makeText(requireView().context, "فاکتور پرداخت شده است", Toast.LENGTH_SHORT).show()
        }
    }

    fun StartPause(item: FactureItem) {
        val updated = item.copy(
            PausedTime = System.currentTimeMillis().toString()
        )
        presenter.UpdateFacture(updated)
    }
    fun EndPause(item: FactureItem) {
        val playedTimeMS = System.currentTimeMillis() - item.PausedTime!!.toLong()
        val PlayedTimeH = (playedTimeMS / 60000) / 60.00
        val NewPlayedTime = item.PlayedTimeH.toDouble() - PlayedTimeH

        val updated = item.copy(
            PlayedTimeH = String.format(Locale.US,"%.2f", NewPlayedTime),
            PausedTime = null
        )
        presenter.UpdateFacture(updated)
    }
}

class AngledGradientDrawable(private val angle: Double, val colorStart: Int, val colorEnd: Int) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()

        val middleColor1 = Color.parseColor("#690A63")
        val middleColor2 = Color.parseColor("#65065F")
        val middleColor3 = Color.parseColor("#62045C")
        val middleColor4 = Color.parseColor("#62045C")

        val radians = Math.toRadians(angle)
        val dirX = cos(radians)
        val dirY = sin(radians)

        val centerX = w / 2f
        val centerY = h / 2f

        val length = hypot(w, h)

        val x0 = centerX - dirX * length / 2
        val y0 = centerY - dirY * length / 2
        val x1 = centerX + dirX * length / 2
        val y1 = centerY + dirY * length / 2

        val shader = LinearGradient(
            x0.toFloat(), y0.toFloat(), x1.toFloat(), y1.toFloat(),
            intArrayOf(colorStart, middleColor1, middleColor2, middleColor3, middleColor4, colorEnd),
            floatArrayOf(0f, 0.3f, 0.42f, 0.53f, 0.60f, 1f),
            Shader.TileMode.CLAMP
        )

        paint.shader = shader
        canvas.drawRect(bounds, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}