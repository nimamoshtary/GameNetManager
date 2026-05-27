package ir.lifeplus.gamenethelper.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
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
import kotlin.math.hypot
import androidx.core.graphics.toColorInt
import ir.lifeplus.gamenethelper.databinding.ItemFactureBinding

//Fragment connected to the fragment_facture.xml layout.
class FactureFragment : Fragment() , ContractPV.FactureView {

    //Connecting the fragment to the layout (View) and the presenter (and then the model).
    lateinit var binding: FragmentFactureBinding
    lateinit var presenter : ContractPV.FacturePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentFactureBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Note: به کل صفحه ویوپیجر بزار تا با اسکرول شدن بره به روزهای قبل و فاکتور های روز قبل رو نشون بده

        presenter = FacturePresenter()
        //Attach the presenter in order to start the process of displaying the contents of this fragment in the view
        presenter.OnAtttach(this, view)

        val SDF = SimpleDateFormat( "HH:mm", Locale("en", "IR") ).format( Date() )

        //Show DialogFacture...
        binding.fabAddFacture.setOnClickListener {

            val dialogs = DialogFactureBinding.inflate(layoutInflater)
            val alert = AlertDialog.Builder(context).create().apply {
                setView(dialogs.root)
                setCancelable(true)
                show()
            }

            val adapterACNp = ArrayAdapter(
                dialogs.root.context,
                R.layout.item_nameplayer,
                presenter.GetPlayerName())
            val adapterACOn = ArrayAdapter(
                dialogs.root.context,
                R.layout.item_nameplayer,
                presenter.GetOperatorsName()
            )

            dialogs.apply {

                edtStartTime.editText!!.setText(SDF)
                RGPlayerStatus.check(R.id.RB_AnyPlayer)
                (ACPlayer.editText as AutoCompleteTextView).setAdapter(adapterACNp)
                (ACOperator.editText as AutoCompleteTextView).setAdapter(adapterACOn)

                //When an item from the 'NamePlayerList' is clicked, that player or customer are added to the facture’s customer list
                (ACPlayer.editText as AutoCompleteTextView).setOnItemClickListener { _, _, position, _ ->

            val sdf = SimpleDateFormat( "HH:mm", Locale("en", "IR") ).format( Date() )
            dialogs.edtStartTime.editText!!.setText( sdf.toString() )
            dialogs.RGPlayerStatus.check(R.id.RB_AnyPlayer)
                    val name = adapterACNp.getItem(position) ?: return@setOnItemClickListener
                    presenter.AddPlayerToList(name, dialogs)

                }

                RGPlayerStatus.setOnCheckedChangeListener { RG, RB ->

                    when (RB) {

                        R.id.RB_FamilierPlayer -> {

                            ACPlayer.visibility = View.VISIBLE

                            //The function 'toPX()' converts a Int into pixel
                            fun Int.toPx(context: Context): Int =
                                (this * context.resources.displayMetrics.density).toInt()
                            //By the amount of 'marginInDp', the 'Ok' button moves lower
                            val marginInDp = 55
                            val marginInPx = marginInDp.toPx(view.context)
                            val params = dialogs.btnSubmit.layoutParams as ConstraintLayout.LayoutParams
                            params.topMargin = marginInPx
                            btnSubmit.layoutParams = params

                        }

                        R.id.RB_AnyPlayer -> {

                            ACPlayer.visibility = View.GONE
                            RVPlayerName.visibility = View.GONE
                            presenter.ClearPlayersList()

                            //The function 'toPX()' converts a Int into pixel
                            fun Int.toPx(context: Context): Int =
                                (this * context.resources.displayMetrics.density).toInt()
                            //The 'Ok' button returns to its original position
                            val marginInPx = 0.toPx(view.context)
                            val params = dialogs.btnSubmit.layoutParams as ConstraintLayout.LayoutParams
                            params.topMargin = marginInPx
                            dialogs.btnSubmit.layoutParams = params

                        }

                    }

                }

                //Send the entered information to the presenter for registration
                btnSubmit.setOnClickListener {

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

    }

    //Receive data from presenter and display it with the generic adapter on RecyclerView
    override fun Run(table: List<FactureItem>) {

        val adapterFacture = GenericAdapter<FactureItem>(
            data = table,
            layoutId = R.layout.item_facture,
            bind = { viewA, item -> bindRC(viewA, item) },
            onClick = { clicked() },
            onLongClick = { presenter.ShowPayModel(it) }
        )
        binding.RcyclerFacture.adapter = adapterFacture
        binding.RcyclerFacture.layoutManager = LinearLayoutManager(requireView().context, RecyclerView.VERTICAL,false)

    }

    //Bind Facture items in the RecyclerView
    fun bindRC(view: View, item: FactureItem) {

        // Setting view background color based on operator model and payment status
        val leftColor = CheckPaying(item.Price, item.PayedPrice)
        val rightColor =  presenter.GetOperatorColor(item.Operator)
        val gradientDrawable = AngledGradientDrawable(50.0, leftColor, rightColor)
        view.background = gradientDrawable

        val ItemBinding = ItemFactureBinding.bind(view)

        val firstPlayer = Converters().toList(item.Players.toString()).firstOrNull() ?: "مشتری جدید"

        val priceTextColor = CheckPaying(item.Price, item.PayedPrice)

        val isPaused = !item.PausedTime.isNullOrEmpty()
        val isFinished = !item.EndTime.isNullOrEmpty()

        //Set data in Item
        ItemBinding.apply {
            txtOnePlayer.text = firstPlayer
            txtStartTime.text = item.StartTime
            txtEndTime.text = (item.EndTime ?: "").ifEmpty { " - " }
            txtPlayedTimeM.text = item.PlayedTimeH
            txtPlayerCount.text = item.PlayerCount.toString()
            txtPlayers.text = item.Players
            txtPrice.text = item.Price.toString()
            txtPrice.setTextColor(priceTextColor)
            txtGames.text = item.GamesName
            txtOperator.text = item.Operator

            //Close the facture and record the current time as the end time of this factor (the end button disappears after being pressed and the facture is closed)
            btnFinish.visibility = if(isFinished) {View.INVISIBLE} else { if(isPaused) { View.INVISIBLE } else { View.VISIBLE} }
            btnFinish.setOnClickListener {

                presenter.FinishTime(item)

                btnFinish.visibility = View.GONE

            }

            // Resuming the Facture timer and taking it out of pause
            btnPlay.visibility = if(isPaused) View.VISIBLE else View.INVISIBLE
            btnPlay.setOnClickListener {
                btnPlay.visibility = View.INVISIBLE
                btnFinish.visibility = View.VISIBLE

                presenter.ReplayPausedFacture(item)
            }

            // Pausing the Facture timer
            btnPause.visibility = if(isFinished) { View.INVISIBLE } else { if(isPaused) { View.INVISIBLE } else { View.VISIBLE} }
            btnPause.setOnClickListener {
                presenter.StartPauseFacture(item)
                btnPlay.visibility = View.VISIBLE
                btnPause.visibility = View.INVISIBLE
                btnFinish.visibility = View.INVISIBLE
            }

        }

    }

    //Update the list of selected players and display it in the RVPlayerName RecyclerView
    override fun updatePlayers(players: ArrayList<PlayerItem>,viewd: DialogFactureBinding) {

        viewd.RVPlayerName.visibility = View.VISIBLE
        val adapterPlayerName = GenericAdapter<PlayerItem>(
            data = players,
            layoutId = R.layout.item_playervertical,
            bind = { viewA, item ->
                viewA.findViewById<TextView>(R.id.txt_TitleV).text = item.Name
                viewA.findViewById<TextView>(R.id.txt_AttendancesNumber).text = item.NumberOfAttendances.toString()
            }
        )
        viewd.RVPlayerName.adapter = adapterPlayerName
        viewd.RVPlayerName.layoutManager = LinearLayoutManager(viewd.root.context, RecyclerView.VERTICAL,false)

    }

    //Show the PayModel BottomSheet
    override fun ShowBottomSheet(bottomSheet: PayModel) {

        bottomSheet.show(parentFragmentManager, bottomSheet.tag)

    }

    // Check Facture payment status and returning red if unpaid, green if paid
    override fun CheckPaying(Price: Int, PayedPrice: Int) :Int{

        if (Price > PayedPrice) {
            return "#DC1010".toColorInt()
        } else {
            return "#1674CC".toColorInt()
        }

    }

    //On tap an FactureItem in the RecyclerView, open the Facture edit screen; after edit and confirm, update editable fields in the database
    fun clicked() { /* باز شدن صفحه ی ویرایش فاکتور  که فعلا امکان پذیر نیست و باید درموردش  فکر کنم*/ }

    //Detaching and disconnecting from the presenter, and restarting the data stored in presenter
    override fun onDetach() {
        super.onDetach()

        presenter.OnDetach()

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