package ir.lifeplus.gamenethelper.presenter

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Toast
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
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

//Main Activity Presenter
class Presenter : ContractPV.Presenter{

    var View :ContractPV.View?=null

    //Launching the Activity and Setting the HomeFragment
    override fun OnAtttach(view:ContractPV.View){
        View = view
        View!!.Run(HomeFragment())
    }

    //Detaching an activity
    override fun OnDetach() {
        View = null
    }

}

//HomeFragment Presenter
class HomePresenter :ContractPV.HomePresenter{

    var View :ContractPV.HomeView ?=null
    var Views :View ?=null

    var tableStatic :StatisticDao ?=null
    var tableOperator :OperatorDao ?=null
    var tableFacture :FactureDao ?=null

    var TodaysStatistic :StatisticItem ?=null

    //Attach home fragment, store view context, arrange and calculate daily statistics, and send them to the view for display
    @SuppressLint("NewApi")
    override fun OnAttach(view: ContractPV.HomeView, views: View) {

        View = view
        Views = views

        tableStatic = database.getdb(Views!!.context).StatisticDao
        tableOperator = database.getdb(Views!!.context).OperatorDao
        tableFacture = database.getdb(Views!!.context).FactureDao

        val SDF = SimpleDateFormat("yyyy-MM-dd", Locale("en", "IR")).format(Date())

        //Check if it is Saturday or not
        val todayIsSaturday = LocalDate.now().dayOfWeek == DayOfWeek.SATURDAY

        //If it is Saturday or 7 days have passed since the last operator statistics reset, all operator statistics will be reset
        tableOperator!!.getAllOperator().forEach { operator ->

            var updatedOperator = operator
            if ( todayIsSaturday ) {

                if ( LocalDate.parse( operator.lastcleanup ) != LocalDate.now() ) {

                    updatedOperator = operator.copy(
                        CountPlayer = 0,
                        TimePlayed = 0,
                        lastcleanup = LocalDate.now().toString()
                    )

                    tableFacture!!.reset()

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

                    tableFacture!!.reset()

                }

            }

            tableOperator!!.insert(updatedOperator)

        }

        //If statistics for today are recorded, they will be stored in TodayStatistic, otherwise an empty data entry for today will be created in the table
        TodaysStatistic = tableStatic!!.getStatisticByDate(SDF)
            ?: StatisticItem(date = SDF, 0, 0, 0, 0, 0, 0).also {
                tableStatic!!.insert(it)
            }

        //Operators are sorted by the longest playtime by players and the highest number of players using them, then delivered to the view for display
        val popularOperators = Repository(tableOperator!!).getXSortByX("Operator", "CountPlayer")
        val mostCountPlayers = Repository(tableOperator!!).getXSortByX("CountPlayer", "CountPlayer")
        val comfortableOperators = Repository(tableOperator!!).getXSortByX("Operator", "TimePlayed")
        val mostTimePlayed = Repository(tableOperator!!).getXSortByX("TimePlayed", "TimePlayed")

        View!!.Run(TodaysStatistic!!,popularOperators,mostCountPlayers,comfortableOperators,mostTimePlayed)

    }

    //Receive and calculate the data needed for the revenue pie chart
    override fun GetChart() {

        val TodaysIncome = TodaysStatistic!!.Pricereceived
        var RegisteredCustomerIncome = TodaysStatistic!!.PriceWithPlayer
        var UnregisteredCustomerIncome = TodaysIncome - RegisteredCustomerIncome
        var TodaysExpenses = TodaysStatistic!!.PriceSpent
        val TodaysProfit = TodaysIncome - TodaysExpenses
        var TodaysLoss = 0

        //If profit is negative, meaning the center is in loss, revenue is calculated as zero and the absolute value of profit is stored in the variable **TodayLoss** so that it is shown in red on the chart
        if (TodaysProfit < 0) {

            UnregisteredCustomerIncome = 0
            RegisteredCustomerIncome = 0
            TodaysExpenses = 0
            TodaysLoss = TodaysProfit

        }

        //Input statistics list for the pie chart. Build and design the pie chart dataset and send it to the view
        val entries = listOf(
            //Revenue from unfamiliar customers (in blue)
            PieEntry(UnregisteredCustomerIncome.toFloat()),
            //Amount of expenses (in white)
            PieEntry(TodaysExpenses.toFloat()),
            //Revenue from regular customers (in sky blue)
            PieEntry(RegisteredCustomerIncome.toFloat()),
            //Amount of loss of the center (in red)
            PieEntry(TodaysLoss.toFloat())
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

    //Detaching an Fragment
    override fun OnDetach() {

        View = null
        Views = null

        tableStatic = null
        tableOperator = null
        tableFacture = null

        TodaysStatistic = null

    }

}

//PlayerFragment Presenter
class PlayerPresenter : ContractPV.PlayerPresenter {

    var View :ContractPV.PlayerView ?=null
    var Views :View ?=null

    var tablePlayer :PlayerDao ?=null

    //Attach Player Fragment, store view context, get data and send them to the view for display
    override fun OnAtttach(view: ContractPV.PlayerView, views: View) {

        View = view
        Views = views
        tablePlayer = database.getdb(Views!!.context).PlayerDao
        View!!.Run(tablePlayer!!.getAllPlayer())

    }

    //Insert or Update the received player information to the player table
    override fun AddPlayer(item :PlayerItem) {

        tablePlayer!!.insert(item)
        View!!.Run(tablePlayer!!.getAllPlayer())

    }

    //Detaching an Fragment
    override fun OnDetach() {

        View = null
        Views = null
        tablePlayer = null

    }

}

//FactureFragment Presenter
class FacturePresenter : ContractPV.FacturePresenter {

    var View :ContractPV.FactureView?=null
    var Views :View?=null

    var tableFacture :FactureDao?=null
    var tablePlayer :PlayerDao?=null
    var tableOperator :OperatorDao?=null
    var tableStatic :StatisticDao?=null

    private val playerNameList = mutableListOf<String>()
    private val playersList = arrayListOf<PlayerItem>()

    //Attach Facture Fragment, store view context, get data and send them to the view for display
    @SuppressLint("SimpleDateFormat")
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

    //Add the customer of the latest factor to the temporary list and Change the customer’s status to online and add to their attendance count and display this customer to view
    override fun AddPlayerToList(playerName: String, viewd: DialogFactureBinding) {

        val playerItem = tablePlayer!!.getPlayerByName(playerName)
        val updated = playerItem.copy(

            NumberOfAttendances = playerItem.NumberOfAttendances + 1,
            Attendance = true

        )

        tablePlayer!!.insert(updated)

        playerNameList.add(playerName)
        playersList.add(updated)
        View!!.updatePlayers(playersList,viewd)

    }

    //Get the dialog view, extract the entered information from the dialog widgets, and build the factor based on this information
    @SuppressLint("SimpleDateFormat")
    override fun AddNewFacture(viewd: DialogFactureBinding) {
        //Note: Getting the list of information from the view instead of taking the view itself is better.

        val StartTime = viewd.edtStartTime.editText!!.text.toString()
        var EndTime :String? = viewd.edtEndTime.editText?.text.toString()
        var PlayerCount = viewd.edtPlayerCount.editText!!.text.toString().toInt()
        val GamesName = viewd.edtGames.editText?.text.toString()
        val Operator = viewd.ACOperator.editText!!.text.toString()
        val OperatorItem = tableOperator!!.getOperatorByName(Operator)
        //Convert the formatted string to an array list
        val Players = Converters().fromList(playerNameList.toList())
        //Convert the entered paused minutes from the view `edtPausedTime` to hours in the Double data type, if it is not empty or null (only with two decimal places)
        val PausedTime = if( viewd.edtPausedTime.editText!!.text.isNullOrEmpty() )  "0"  else  String.format(Locale.US,"%.2f", viewd.edtPausedTime.editText!!.text.toString().toDouble()/60.00)

        val SDF = SimpleDateFormat("yyyy-MM-dd").format( Date() ).toString()

        var PlayedTime :Double = 0.00
        var PlayedTimeS :String = "0.00"
        var Price :Int = 0

        //If the end time of the factor is not entered, the factor will be created in the "running" state, and the played time and cost of the factor will not be calculated
        if ( EndTime.isNullOrEmpty() ) {

            //Subtract the paused time from the played time of the factor (it will obviously become negative)
            val subtractedPlayTime = 0.00 - PausedTime.toDouble()
            PlayedTime = subtractedPlayTime
            PlayedTimeS = subtractedPlayTime.toString()

        }else{

            //Calculate the played time using the function "BetweenTimeCalculator" in minutes, then convert it to hours in the `Double` data type with two decimal places
            PlayedTime = ( BetweenTimeCalculator(StartTime,EndTime).toDouble() ) - PausedTime.toDouble()
            PlayedTimeS = String.format(Locale.US,"%.2f", PlayedTime)

            Price = ( PlayedTime * CostCalculatorByOperator(OperatorItem.Operator, PlayerCount).toDouble() ).toInt()

            //Check whether the customer is anonymous or a regular customer, and update their profile information
            val CheckPlayerList = if(playersList.isNullOrEmpty()) null else playersList
            CheckPlayerList?.forEach{ itemPlayer ->

                val NewPleyedTime = itemPlayer.PlayedTimeH.toDouble() + PlayedTimeS.toDouble()
                val NEWPlayer = itemPlayer.copy(
                    PlayedTimeH = NewPleyedTime.toString(),
                    Attendance = false)

                tablePlayer!!.insert(NEWPlayer)

            }

        }

        //Build and save the final item of the factor
        val factureItem = FactureItem(

            StartTime = StartTime,
            EndTime = EndTime,
            GamesName = GamesName,
            PausedTime = null,
            Sub = viewd.edtSub.editText?.text.toString(),
            PlayerCount = PlayerCount,
            Players = Players,
            Price = Price,
            Operator = Operator,
            PlayedTimeH = PlayedTimeS ,
            CreateDate = SDF

        )

        tableFacture!!.insert(factureItem)

        //Clear the temporary lists storing the names of the customers of this facture
        ClearPlayersList()

        //Send today’s factures to the view for display
        View!!.Run(tableFacture!!.getFacturesByDate(SDF))

    }

    //Apply the current time as the factor’s end time, change the factor’s status, and calculate the played time and the customer’s cost
    @TargetApi(Build.VERSION_CODES.N)
    override fun FinishTime(item: FactureItem) {

        val SDF = SimpleDateFormat("HH:mm", Locale("en", "IR")).format(Date())

        val BetwenTime = BetweenTimeCalculator( item.StartTime, SDF )
        val TotalPlayedTime = BetwenTime + item.PlayedTimeH.toDouble()
        val CalculatePrice = TotalPlayedTime * CostCalculatorByOperator(item.Operator, item.PlayerCount).toDouble()

        //Convert the formatted string of player names into a list, add to their played hours in the center, and change their status to offline
        val PlayerList :List<String>? = if(item.Players.isNullOrEmpty()) null else Converters().toList(item.Players)
        PlayerList?.forEach{ Item ->

            //Note: ریپازیتوری راه بندار و این رو از ریپازیتوری بگیر
            //Note: بعد از قابلیت حذف مشتری، اینجا چک کن که دیتای این اسم وجود داره یا حذف شده
            val PlayerData = tablePlayer!!.getPlayerByName(Item)

            val NewPlayedTime = PlayerData.PlayedTimeH.toDouble() + TotalPlayedTime
            val updated = PlayerData.copy(
                PlayedTimeH = String.format(Locale.US,"%.2f", NewPlayedTime),
                Attendance = false)

            tablePlayer!!.insert(updated)

        }

        val updated = item.copy(
            EndTime = SDF.toString(),
            PlayedTimeH = TotalPlayedTime.toString(),
            Price = CalculatePrice.toInt(),
        )
        tableFacture!!.insert(updated)

    }

    //Record the stop time of the facture
    override fun StartPauseFacture(item: FactureItem) {

        val updated = item.copy(
            PausedTime = System.currentTimeMillis().toString()
        )
        tableFacture!!.insert(updated)

    }

    //Resume the factor’s time and subtract the stopped duration from the played hours
    override fun ReplayPausedFacture(item: FactureItem) {

        //Calculate the time between now and when the facture was paused (in milliseconds)
        val Between = System.currentTimeMillis() - item.PausedTime!!.toLong()
        //Convert milliseconds to hours
        val PausedTime = Between.toDouble() / 3600000.00
        //Subtract the paused time from the played time
        val NewPlayedTime = item.PlayedTimeH.toDouble() - PausedTime

        val updated = item.copy(
            PlayedTimeH = String.format(Locale.US,"%.2f", NewPlayedTime),
            PausedTime = null
        )
        tableFacture!!.update(updated)

    }

    //Detaching an Fragment
    override fun OnDetach() {
        View = null
        Views = null
        tableFacture = null
        tablePlayer = null
        tableOperator = null
        tableStatic = null
    }

    //Calculate the difference between the two entered times ("start time" and "end time") in hours
    @SuppressLint("NewApi")
    override fun BetweenTimeCalculator(startTime: String, endTime: String):Double {

        val StartTime = LocalTime.parse(startTime)
        val EndTime = LocalTime.parse(endTime)
        val duration = Duration.between(StartTime, EndTime)
        val hours = duration.toMinutes().toInt() / 60.00
        return hours

    }

    //Calculate the cost of the factor based on the operator type
    override fun CostCalculatorByOperator(Operator: String, CountPlayer: Int): Long {

        val OperatorItem = tableOperator!!.getOperatorByName(Operator)
        when(OperatorItem.Model) {

            "بردگیم" -> { return OperatorItem.CostByCountPlayer!!.toLong() * CountPlayer }

            "کنسول" -> {
                when (CountPlayer) {

                    1 -> { return OperatorItem.CostConsoleSingelController!!.toLong() }
                    2 -> { return OperatorItem.CostConsoleTwoController!!.toLong() }
                    3 -> { return OperatorItem.CostConsoleThreeController!!.toLong() }
                    else -> { return OperatorItem.CostConsoleFourController!!.toLong() }

                }
            }

            else -> { return 1 }

        }

    }

    //Clear the temporary lists storing the names of the customers of last facture
    override fun ClearPlayersList() {

        playerNameList.clear()
        playersList.clear()

    }

    //Get the list of all customer names
    override fun GetPlayerName():List<String> {

        return tablePlayer!!.getTitlePlayer()

    }

    //Get the list of all operator names
    override fun GetOperatorsName() :List<String> {

        return Repository(tableOperator!!).getXSortByX("operator",null).map { it.toString() }

    }

    //Return the specific color of the operator whose name has been entered
    override fun GetOperatorColor(operator: String) : Int {

        when(tableOperator!!.getOperatorByName(operator).Model) {

            "بردگیم" -> { return Color.parseColor("#D97614") }

            "کنسول" -> { return Color.parseColor("#135492") }

            else -> return Color.WHITE

        }

    }

    //Display the bottom sheet "PayModel" and provide the selected factor’s information for update and status change
    override fun ShowPayModel(item: FactureItem) {

        if (item.PayedPrice < item.Price) {

            val bottomSheet = PayModel.newInstance(itemId = item.id!!)
            View!!.ShowBottomSheet(bottomSheet)

        } else {

            Toast.makeText( Views!!.context, "فاکتور پرداخت شده است", Toast.LENGTH_SHORT ).show()

        }

    }

}

//BottemSheetManageOperator Presenter
class ManageOperatorPresenter :ContractPV.ManageOperatorPresenter {

    var View :ContractPV.ManageOperatorView ?=null
    var Views :View ?=null

    var tableOperator :OperatorDao ?=null

    //Attach OperatorManager BottemSheet, store view context, get data and send them to the view for display
    override fun OnAttach(view: ContractPV.ManageOperatorView, views: View) {

        View = view
        Views = views

        tableOperator = database.getdb(Views!!.context).OperatorDao
        View!!.Run(tableOperator!!.getAllOperator())

    }

    //Retrieve the operator information from the board‑game type and store it. The last cleanup date of this operator is the same as the others
    @SuppressLint("NewApi")
    override fun AddOperatorBoardGame(operator: String, price: Int) {

        val LastCleanup = if(tableOperator!!.getAllOperator().size >= 1) tableOperator!!.getAllOperator().last().lastcleanup else LocalDate.now().toString()

        val item = OperatorItem(
            Operator = operator,
            Model = "بردگیم",
            CostByCountPlayer = price,
            lastcleanup = LastCleanup
        )

        tableOperator!!.insert(item)

    }

    //Retrieve the operator information from the consol type and store it. The last cleanup date of this operator is the same as the others
    @SuppressLint("NewApi")
    override fun AddOperatorConsol(operator: String, price: List<Int>) {

        val LastCleanup = if(tableOperator!!.getAllOperator().size >= 1) tableOperator!!.getAllOperator().last().lastcleanup else LocalDate.now().toString()

        val item = OperatorItem(
            Operator = operator,
            Model = "کنسول",
            CostConsoleSingelController = price[0],
            CostConsoleTwoController = price[1],
            CostConsoleThreeController = price[2],
            CostConsoleFourController = price[3],
            lastcleanup = LastCleanup
        )

        tableOperator!!.insert(item)

    }

    //Detaching an BottemSheet
    override fun OnDetach() {

        View = null
        Views = null
        tableOperator = null

    }

}

//BottemSheetPayModel Presenter
class PayModelPresenter :ContractPV.PayModelPresenter {

    var View :ContractPV.PayModelView?=null
    var Views :View?=null

    var tableFacture :FactureDao?=null
    var tableOperator :OperatorDao?=null
    var tablePlayer :PlayerDao?=null
    var tableStatic :StatisticDao?=null

    var PayingFacture :FactureItem?=null
    var PayingOperator :OperatorItem?=null

    //Attach PayModel BottemSheet, store view context, get data and send them to the view for display
    override fun OnAttach(view: ContractPV.PayModelView, views: View, itemId: Int) {

        View = view
        Views = views

        database.getdb(views.context).apply {
            tableFacture = FactureDao
            tableOperator = OperatorDao
            tablePlayer = PlayerDao
            tableStatic = StatisticDao
        }

        PayingFacture = tableFacture!!.getFactureById(itemId)
        PayingOperator = tableOperator!!.getOperatorByName(PayingFacture!!.Operator)

        View!!.Run( PayingFacture!!.copy( Status = true) )

    }

    //Change the Facture status to paid. Update the data of the related operator. Update the data of the customers of this invoice. Record the statistics obtained from this Facture in the statistics table
    @TargetApi(Build.VERSION_CODES.N)
    override fun PayFacture(itemId: Int, Status: String) {
        //Note: اگر آمار و فرایند حسابداری به حالت پرداخت(نقدی-اعتباری) نیاز داشت، از Status استفاده کن

        val Date = SimpleDateFormat("yyyy-MM-dd", Locale("en", "IR")).format(Date())
        val Time = SimpleDateFormat("HH:mm", Locale("en", "IR")).format(Date())

        tableFacture!!.insert(
            PayingFacture!!.copy(
                PayedPrice = PayingFacture!!.Price,
                Status = false,
                Note = null,
                PayedDate = Date,
                PayedTime = Time
            )
        )

        tableOperator!!.insert(
            PayingOperator!!.copy(
                CountPlayer = PayingOperator!!.CountPlayer!! + PayingFacture!!.PlayerCount,
                TimePlayed = ( PayingOperator!!.TimePlayed!!.toDouble() + PayingFacture!!.PlayedTimeH.toDouble() ).toInt()
            )
        )

        //Retrieve the Data of each individual customer in this Facture and update their profile
        var PriceWithplayer = 0
        val Players = extractPlayerNames(PayingFacture!!.Players, PayingFacture!!.PlayerCount)
        if( Players.isNullOrEmpty() == false){

            PriceWithplayer = PayingFacture!!.Price

            Players.forEach { player ->

                var Player = tablePlayer!!.getPlayerByName(player) ?: return@forEach
                val PricePerPlayer = PayingFacture!!.Price / PayingFacture!!.PlayerCount

                val updatedPlayer = Player.copy(
                    PayedPrice = Player.PayedPrice + PricePerPlayer,
                    Attendance = false
                )

                tablePlayer!!.insert(updatedPlayer)

            }

        }

        if( tableStatic!!.getStatisticByDate(Date) == null ){

            val Static = StatisticItem(
                date = Date,
                Pricereceived = PayingFacture!!.PayedPrice,
                PlayerCount = PayingFacture!!.PlayerCount,
                TimePlayed = PayingFacture!!.PlayedTimeH.toInt(),
                PriceSpent = 0,
                PriceWithPlayer = PriceWithplayer,
                FactureCount = 1
            )
            tableStatic!!.insert(Static)

        }else{

            val Statistic = tableStatic!!.getStatisticByDate(Date)
            val updatedStatistic = Statistic!!.copy(
                Pricereceived = Statistic.Pricereceived + PayingFacture!!.Price,
                PlayerCount = Statistic.PlayerCount + PayingFacture!!.PlayerCount,
                TimePlayed = ( Statistic.TimePlayed.toDouble() + PayingFacture!!.PlayedTimeH.toDouble() ).toInt(),
                PriceWithPlayer = Statistic.PriceWithPlayer + PriceWithplayer,
                FactureCount = Statistic.FactureCount + 1
            )
            tableStatic!!.insert(updatedStatistic)
        }

    }

    //Extract the names of the players from a string‑formatted and convert them into a list.
    override fun extractPlayerNames(raw: String?, count: Int): List<String> {

        if (raw == null) return emptyList()
        return if (count >= 2) {

            Converters().toList(raw.toString())

        } else {

            listOf(raw.toString())

        }

    }

    //Detaching an BottemSheet
    override fun OnDetach() {

        View = null
        Views = null

        tableFacture = null
        tablePlayer = null
        tableOperator = null

        PayingFacture = null
        PayingOperator = null

    }

}