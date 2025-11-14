package ir.lifeplus.gamenethelper.presenter

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.util.LocaleData
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.databinding.DialogFactureBinding
import ir.lifeplus.gamenethelper.model.Converters
import ir.lifeplus.gamenethelper.model.FactureDao
import ir.lifeplus.gamenethelper.model.FactureItem
import ir.lifeplus.gamenethelper.model.OperatorDao
import ir.lifeplus.gamenethelper.model.OperatorItem
import ir.lifeplus.gamenethelper.model.PlayerDao
import ir.lifeplus.gamenethelper.model.PlayerItem
import ir.lifeplus.gamenethelper.model.Repository
import ir.lifeplus.gamenethelper.model.StatisticDao
import ir.lifeplus.gamenethelper.model.StatisticItem
import ir.lifeplus.gamenethelper.model.database
import ir.lifeplus.gamenethelper.view.HomeFragment
import ir.lifeplus.gamenethelper.view.PayModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class Presenter : ContractPV.Presenter{
    var View :ContractPV.View?=null

    //Launching the Activity and Setting the Fragment
    override fun OnAtttach(view:ContractPV.View){
        View = view
        View!!.Run(HomeFragment())
    }
    //Detaching an activity
    override fun OnDetach() {
        View = null
    }
}
class PlayerPresenter : ContractPV.PlayerPresenter {
    var View :ContractPV.PlayerView ?=null
    var Views :View ?=null
    var tablePlayer :PlayerDao ?=null

    //Launching the Fragment and Setting the RecyclerView
    override fun OnAtttach(view: ContractPV.PlayerView, views: View) {
        View = view
        Views = views
        tablePlayer = database.getdb(Views!!.context).PlayerDao
        View!!.Run(tablePlayer!!)
    }
    //Adding Customer and Player to the Player Table
    override fun AddPlayer(item :PlayerItem) {
        tablePlayer!!.insert(item)
        View!!.Run(tablePlayer!!)
    }
    //Detaching an activity
    override fun OnDetach() {
        View = null
        Views = null
        tablePlayer = null
    }
}
class FacturePresenter : ContractPV.FacturePresenter {
    var View :ContractPV.FactureView?=null
    var Views :View?=null
    var tableFacture :FactureDao?=null
    var tablePlayer :PlayerDao?=null
    var tableOperator :OperatorDao?=null
    var tableStatic :StatisticDao?=null
    private val playerNameList = mutableListOf<String>()
    private val playersList = arrayListOf<PlayerItem>()

    override fun OnAtttach(view: ContractPV.FactureView, views: View) {
        View = view
        Views = views
        tableFacture = database.getdb(views.context).FactureDao
        tablePlayer = database.getdb(views.context).PlayerDao
        tableOperator = database.getdb(views.context).OperatorDao
        tableStatic = database.getdb(views.context).StatisticDao
        val SDF = SimpleDateFormat("yyyy-MM-dd").format( Date()).toString()
        View!!.Run( tableFacture!!.getFacturesByDate(SDF) )
    }

    override fun OnDetach() {
        View = null
        Views = null
        tableFacture = null
        tablePlayer = null
        tableOperator = null
        tableStatic = null
    }

    override fun AddPlayerToList(name: String, viewd: DialogFactureBinding) {
        val playerItem = tablePlayer!!.getPlayerByName(name)
        val updated = playerItem.copy(
            NumberOfAttendances = playerItem.NumberOfAttendances + 1,
            Attendance = true
        )
        tablePlayer!!.insert(updated)

        playerNameList.add(name)
        playersList.add(updated)
        View!!.updatePlayers(playersList,viewd)
    }

    override fun ClearPlayersList() {
        playerNameList.clear()
        playersList.clear()
    }

    @SuppressLint("NewApi")
    fun TimeCalculator(sTime: String, eTime: String):Int {
        val start = LocalTime.parse(sTime)
        val end = LocalTime.parse(eTime)
        val duration = Duration.between(start, end)
        val minutes = duration.toMinutes().toInt()
        return minutes
    }

    override fun AddNewFacture(viewd: DialogFactureBinding) {

        val StartTime = viewd.edtStartTime.editText!!.text.toString()
        //Convert stop time from minutes to hours if available
        val PausedTime = if(viewd.edtPausedTime.editText!!.text.isNullOrEmpty()) "0" else String.format(Locale.US,"%.2f", viewd.edtPausedTime.editText!!.text.toString().toDouble()/60.00)

        val Players = Converters().fromList(playerNameList.toList())
        var EndTime :String? = viewd.edtEndTime.editText?.text.toString()
        var PlayedTime :Double = 0.00
        var PlayedTimeS :String = "0.00"
        var Price :Int = 0
        var PlayerCount = viewd.edtPlayerCount.editText!!.text.toString().toInt()
        val Operator = viewd.ACOperator.editText!!.text.toString()

        if (EndTime.isNullOrEmpty()) {
            EndTime = null
            //subtracts the stop time from the played clock, if you enter a stop time value.
            val NewPlayedTime = 0.00 - PausedTime.toDouble()
            PlayedTime = NewPlayedTime
            PlayedTimeS = NewPlayedTime.toString()
        }else{
            //Converts played hours from minutes to hours and subtracts stop time from played hours, if you enter a stop time value.
            PlayedTime = ( TimeCalculator(StartTime,EndTime).toDouble() / 60.00 ) - PausedTime.toDouble()
            PlayedTimeS = String.format(Locale.US,"%.2f", PlayedTime)
            val OperatorItem = tableOperator!!.getOperatorByName(Operator)
            when(OperatorItem.Model){
                "بردگیم" -> { Price = (PlayedTime * OperatorItem.PriceBG!!.toDouble() * PlayerCount.toDouble()).toInt() }
                "کنسول" -> { when(PlayerCount){
                    1 -> { Price = (PlayedTime * OperatorItem.PriceCO!!).toInt() }
                    2 -> { Price = (PlayedTime * OperatorItem.PriceCTW!!).toInt() }
                    3 -> { Price = (PlayedTime * OperatorItem.PriceCTH!!).toInt() }
                    else -> { Price = (PlayedTime * OperatorItem.PriceCFR!!).toInt() }
                } }
            }
            val CheckPlayerList = if(playersList.isNullOrEmpty()) null else playersList
            CheckPlayerList?.forEach{ itemPlayer ->
                val NewPleyedTime = itemPlayer.PlayedTimeH.toDouble() + PlayedTime
                val NEWPlayer = itemPlayer.copy(
                    PlayedTimeH = NewPleyedTime.toString(),
                    Attendance = false)
                tablePlayer!!.insert(NEWPlayer)
            }
        }

        val SDF = SimpleDateFormat("yyyy-MM-dd").format( Date() ).toString()
        val factureItem = FactureItem(
            StartTime = StartTime,
            EndTime = EndTime,
            GamesName = viewd.edtGames.editText?.text.toString(),
            PausedTime = null,
            Sub = viewd.edtSub.editText?.text.toString(),
            PlayerCount = PlayerCount,
            Players = Players,
            Price = Price,
            Operator = Operator,
            PlayedTimeH = PlayedTimeS ,
            CreateDate = SDF)
        tableFacture!!.insert(factureItem)

        playersList.clear()
        playerNameList.clear()

        View!!.Run(tableFacture!!.getFacturesByDate(SDF))
    }

    override fun UpdateFacture(item: FactureItem) {
        tableFacture!!.insert(item)
    }

    override fun GetPlayerName():List<String> {
        return tablePlayer!!.getTitlePlayer()
    }

    override fun GetOperatorsName() :List<String> {
        return tableOperator!!.getTitleOperator()
    }

    override fun GetOperatorByName(operator: String) : OperatorItem {
       return tableOperator!!.getOperatorByName(operator)
    }

    override fun ShowPayModel(itemId: Int) {
        val bottomSheet = PayModel.newInstance(itemId = itemId)
            //PayModel(item, tableFacture!!, tablePlayer!!, tableStatic!!, tableOperator!!)
        View!!.ShowBottomSheet(bottomSheet)
    }

    @SuppressLint("NewApi")
    override fun TimeCalculatorM(Start: String, End: String): Double {
        val start = LocalTime.parse(Start)
        val end = LocalTime.parse(End)
        val duration = Duration.between(start, end)
        val minutes = duration.toMinutes().toInt()
        return minutes/60.00
    }

    override fun FinishTime(item: FactureItem) {
        val PlayerList = if(item.Players.isNullOrEmpty()) null else Converters().toList(item.Players)
        val SDF = SimpleDateFormat("HH:mm", Locale("en", "IR")).format(Date())
        val PlayedTime = TimeCalculatorM(item.StartTime, SDF.toString()) + item.PlayedTimeH.toDouble()
        val PlayedTimeH = String.format(Locale.US,"%.2f", PlayedTime)

        PlayerList!!.forEach{ Item ->
            val PlayerData = tablePlayer!!.getPlayerByName(Item) //بعد از اضافه کردن قابلیت حذف مشتری، چک کن که مشتری حذف نشده باشه
            val updated = PlayerData.copy(
                PlayedTimeH = (PlayerData.PlayedTimeH.toDouble() + PlayedTimeH.toDouble()).toString(),
                Attendance = false)
            tablePlayer!!.insert(updated)
        }

        val Operator = tableOperator!!.getOperatorByName(item.Operator)
        val CalculatePrice = if(Operator.Model == "بردگیم"){
            PlayedTimeH.toDouble() * Operator.PriceBG!!.toDouble() * item.PlayerCount
        }else{
            when(item.PlayerCount){
                1 -> {PlayedTimeH.toDouble() * Operator.PriceCO!!.toDouble()}
                2 -> {PlayedTimeH.toDouble() * Operator.PriceCTW!!.toDouble()}
                3 -> {PlayedTimeH.toDouble() * Operator.PriceCTH!!.toDouble()}
                else -> {PlayedTimeH.toDouble() * Operator.PriceCFR!!.toDouble()}
            }
        } //مدل اپراتور جدید بیاد باید when بنویسی

        val updated = item.copy(
            EndTime = SDF.toString(),
            PlayedTimeH = PlayedTimeH,
            Price = CalculatePrice.toInt(),
        )
        tableFacture!!.insert(updated)
    }
}
class HomePresenter :ContractPV.HomePresenter{
    var View :ContractPV.HomeView ?=null
    var Views :View ?=null
    var tableStatic :StatisticDao ?=null
    var tableOperator :OperatorDao ?=null
    var TodaysStatistic :StatisticItem ?=null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun OnAttach(view: ContractPV.HomeView, views: View) {
        View = view
        Views = views
        tableStatic = database.getdb(Views!!.context).StatisticDao
        tableOperator = database.getdb(Views!!.context).OperatorDao
        val SDF = SimpleDateFormat("yyyy-MM-dd", Locale("en", "IR")).format(Date())
        val todayIsSaturday = LocalDate.now().dayOfWeek == DayOfWeek.SATURDAY

        tableOperator!!.getAllOperator().forEach { operator ->
            var updatedOperator = operator
            if (todayIsSaturday) {
                if ( LocalDate.parse(operator.lastcleanup) != LocalDate.now() ) {
                    updatedOperator = operator.copy(
                        CountPlayer = 0,
                        TimePlayed = 0,
                        lastcleanup = LocalDate.now().toString()
                    )
                    tableOperator!!.insert(updatedOperator)
                }
            } else {
                val daySinceReset = ChronoUnit.DAYS.between(LocalDate.parse(operator.lastcleanup),LocalDate.now())
                if ( daySinceReset >= 7 ) {
                    val newResetData = LocalDate.parse(operator.lastcleanup).plusDays(7)
                    updatedOperator = operator.copy(
                        CountPlayer = 0,
                        TimePlayed = 0,
                        lastcleanup = newResetData.toString()
                    )
                    tableOperator!!.insert(updatedOperator)
                }
            }

        }
        //این خط کد بعد از بسته شدن شیفت جمعه انجام بشه. چون الان شیفت نداریم، اینجا ران کردیم تا هر هفته شنبه دیتای هفته ی قبلی رو از آیتم اپراتور ها پاک کنه
        TodaysStatistic = tableStatic!!.getStatisticByDate(SDF)
            ?: StatisticItem(date = SDF, 0, 0, 0, 0, 0, 0).also {
                tableStatic!!.insert(it)
            }

        val popularOperators = Repository(tableOperator!!).getXSortByX("Operator", "CountPlayer")
        val mostCountPlayers = Repository(tableOperator!!).getXSortByX("CountPlayer", "CountPlayer")
        val comfortableOperators = Repository(tableOperator!!).getXSortByX("Operator", "TimePlayed")
        val mostTimePlayed = Repository(tableOperator!!).getXSortByX("TimePlayed", "TimePlayed")

        View!!.Run(TodaysStatistic!!,popularOperators,mostCountPlayers,comfortableOperators,mostTimePlayed)
    }

    override fun GetChart() {
        val TodaysIncome = TodaysStatistic!!.Pricereceived
        val TodaysProfit = TodaysIncome - TodaysStatistic!!.PriceSpent
        var TodaysExpenses = TodaysStatistic!!.PriceSpent
        var RegisteredCustomerIncome = TodaysStatistic!!.PriceWithPlayer
        var UnregisteredCustomerIncome = TodaysIncome - TodaysStatistic!!.PriceWithPlayer
        var TodaysLoss = 0
        if (TodaysProfit < 0) {
            UnregisteredCustomerIncome = 0
            RegisteredCustomerIncome = 0
            TodaysExpenses = 0
            TodaysLoss = TodaysProfit
        }

        val entries = listOf(
            PieEntry(UnregisteredCustomerIncome.toFloat()),//نشان دادن درآمد از مشتریان ناآشنا با رنگ آبی پر رنگ
            PieEntry(TodaysExpenses.toFloat()),//نشان دادن مخارج با رنگ سفید
            PieEntry(RegisteredCustomerIncome.toFloat()),//نشان دادن درآمد از مشتریان ثابت با رنگ آبی کم رنگ
            PieEntry(TodaysLoss.toFloat())//در صورت ضرر، کل چارت قرمز شده و این مقدار را نشان میدهد
        )

        val dataSet = PieDataSet(entries, "$TodaysProfit")
        dataSet.colors = listOf(
            Color.parseColor("#0E5EC2"),
            Color.parseColor("#C7E4FF"),
            Color.parseColor("#52D2F6"),
            Color.RED
        )
        dataSet.valueTextSize = 12f
        dataSet.setDrawValues(false)
        val data = PieData(dataSet)

        View!!.SetChart(data)
    }

}
class ManageOperatorPresenter :ContractPV.ManageOperatorPresenter {
    var View :ContractPV.ManageOperatorView ?=null
    var Views :View ?=null
    var tableOperator :OperatorDao ?=null

    override fun OnAttach(view: ContractPV.ManageOperatorView, views: View) {
        View = view
        Views = views
        tableOperator = database.getdb(Views!!.context).OperatorDao

        View!!.Run(tableOperator!!.getAllOperator())
    }

    @SuppressLint("NewApi")
    override fun AddOperatorBoardGame(operator: String, model: String, price: Int) {
        val todayIsSaturday = LocalDate.now().dayOfWeek == DayOfWeek.SATURDAY
        if (todayIsSaturday) {
            val item = OperatorItem(
                Operator = operator,
                Model = model,
                PriceBG = price,
                lastcleanup = LocalDate.now().toString()
            )
            tableOperator!!.insert(item)
        } else {
            val dayOfWeek = LocalDate.now().dayOfWeek.value
            val daysToSubtract = if(dayOfWeek == 7) 1 else dayOfWeek + 1
            val item = OperatorItem(
                Operator = operator,
                Model = model,
                PriceBG = price,
                lastcleanup = ( LocalDate.now().minusDays(daysToSubtract.toLong()) ).toString()
            )
            tableOperator!!.insert(item)
        }
    }

    @SuppressLint("NewApi")
    override fun AddOperatorConsol(operator: String, model: String, price: List<Int>) {
        val todayIsSaturday = LocalDate.now().dayOfWeek == DayOfWeek.SATURDAY
        if (todayIsSaturday) {
            val item = OperatorItem(
                Operator = operator,
                Model = model,
                PriceCO = price[0],
                PriceCTW = price[1],
                PriceCTH = price[2],
                PriceCFR = price[3],
                lastcleanup = LocalDate.now().toString()
            )
            tableOperator!!.insert(item)
        } else {
            val dayOfWeek = LocalDate.now().dayOfWeek.value
            val daysToSubtract = if(dayOfWeek == 7) 1 else dayOfWeek + 1
            val item = OperatorItem(
                Operator = operator,
                Model = model,
                PriceCO = price[0],
                PriceCTW = price[1],
                PriceCTH = price[2],
                PriceCFR = price[3],
                lastcleanup = ( LocalDate.now().minusDays(daysToSubtract.toLong()) ).toString()
            )
            tableOperator!!.insert(item)
        }

    }

    override fun OnDetach() {
        View = null
        Views = null
        tableOperator = null
    }

}
class PayModelPresenter :ContractPV.PayModelPresenter {
    var View :ContractPV.PayModelView?=null
    var Views :View?=null
    var tableFacture :FactureDao?=null
    var tableOperator :OperatorDao?=null
    var tablePlayer :PlayerDao?=null
    var tableStatic :StatisticDao?=null

    override fun OnAttach(view: ContractPV.PayModelView, views: View, itemId: Int) {
        View = view
        Views = views

        database.getdb(views.context).apply {
            tableFacture = FactureDao
            tableOperator = OperatorDao
            tablePlayer = PlayerDao
            tableStatic = StatisticDao
        }

        View!!.Run( tableFacture!!.getFactureById(itemId) )
    }

    override fun EditFacture(item: FactureItem) {
        tableFacture!!.insert(item)
    }
    override fun PayFacture(itemId: Int, Status: String) {

        //اگه حسابداری نیاز داره بدونه در روز چقدر ورودی نقدی داشته و چقدر اعتباری، از استاتوس استفاده کن

        val Date = SimpleDateFormat("yyyy-MM-dd", Locale("en", "IR")).format(Date())
        val Time = SimpleDateFormat("HH:mm", Locale("en", "IR")).format(Date())
        val item = tableFacture!!.getFactureById(itemId)
        tableFacture!!.insert(item.copy(
            PayedPrice = item.Price,
            Status = false,
            Note = null,
            PayedDate = Date,
            PayedTime = Time
        ))

        val operatorItem = tableOperator!!.getOperatorByName(item.Operator)
        val updatedOperator = operatorItem.copy(
            CountPlayer = operatorItem.CountPlayer!! + item.PlayerCount,
            TimePlayed = ( operatorItem.TimePlayed!!.toDouble() + item.PlayedTimeH.toDouble() ).toInt()
        )
        tableOperator!!.insert(updatedOperator)

        var PriceWithplayer = 0
        val Players = extractPlayerNames(item.Players, item.PlayerCount)
        if( Players.isNullOrEmpty() == false){
            Players.forEach { player ->
                var Player = tablePlayer!!.getPlayerByName(player) ?: return@forEach
                val PricePerPlayer = item.Price / item.PlayerCount

                val updatedPlayer = Player.copy(
                    PayedPrice = Player.PayedPrice + PricePerPlayer,
                    Attendance = false
                )
                PriceWithplayer = item.Price
                tablePlayer!!.insert(updatedPlayer)
            }
        }

        if( tableStatic!!.getStatisticByDate(Date) == null ){
            val Static = StatisticItem(
                date = Date,
                Pricereceived = item.PayedPrice,
                PlayerCount = item.PlayerCount,
                TimePlayed = item.PlayedTimeH.toInt(),
                PriceSpent = 0,
                PriceWithPlayer = PriceWithplayer,
                FactureCount = 1
            )
            tableStatic!!.insert(Static)
        }else{
            val Statistic = tableStatic!!.getStatisticByDate(Date)
            val updatedStatistic = Statistic!!.copy(
                Pricereceived = Statistic.Pricereceived + item.Price,
                PlayerCount = Statistic.PlayerCount + item.PlayerCount,
                TimePlayed = ( Statistic.TimePlayed.toDouble() + item.PlayedTimeH.toDouble() ).toInt(),
                PriceWithPlayer = Statistic.PriceWithPlayer + PriceWithplayer,
                FactureCount = Statistic.FactureCount + 1
            )
            tableStatic!!.insert(updatedStatistic)
        }
        //موقع ثبت شدن فاکتور، چک کن ببین سایز لیست پلیر با تعداد پلیری که وارد کرده، یکی باشه
    }
    override fun extractPlayerNames(raw: Any?, count: Int): List<String> {
        if (raw == null) return emptyList()
        return if (count >= 2) {
            Converters().toList(raw.toString())
        } else {
            listOf(raw.toString())
        }
    }
    override fun OnDetach() {
        View = null
        Views = null
        tableFacture = null
        tablePlayer = null
        tableOperator = null
    }
}