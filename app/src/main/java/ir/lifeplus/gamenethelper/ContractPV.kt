package ir.lifeplus.gamenethelper

import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import ir.lifeplus.gamenethelper.databinding.DialogFactureBinding
import ir.lifeplus.gamenethelper.model.FactureItem
import ir.lifeplus.gamenethelper.model.OperatorItem
import ir.lifeplus.gamenethelper.model.PlayerItem
import ir.lifeplus.gamenethelper.model.StatisticItem
import ir.lifeplus.gamenethelper.view.HomeFragment
import ir.lifeplus.gamenethelper.view.PayModel

interface ContractPV {
    interface Presenter{
        fun OnAtttach(view: View)
        fun OnDetach()
    }
    interface PlayerPresenter{
        fun OnAtttach(view: PlayerView, views:android.view.View)
        fun OnDetach()
        fun AddPlayer(item:PlayerItem)
    }
    interface FacturePresenter{

        fun OnAtttach(view: FactureView, views:android.view.View)

        fun AddPlayerToList(playerName: String, viewd: DialogFactureBinding)

        fun AddNewFacture(viewd: DialogFactureBinding)

        fun ClearPlayersList()

        fun FinishTime(item:FactureItem)

        fun StartPauseFacture(item: FactureItem)

        fun ReplayPausedFacture(item: FactureItem)

        fun OnDetach()

        fun GetPlayerName():List<String>

        fun GetOperatorsName() :List<String>

        fun GetOperatorColor(operator: String) :Int

        fun ShowPayModel(item: FactureItem)

        fun BetweenTimeCalculator(startTime: String, endTime: String):Double

        fun CostCalculatorByOperator(Operator: String, CountPlayer: Int) :Long

    }
    interface HomePresenter{
        fun OnAttach(view: HomeView, views:android.view.View)
        fun GetChart()
        fun OnDetach()
    }
    interface ManageOperatorPresenter{
        fun OnAttach(view: ManageOperatorView, views: android.view.View)
        fun AddOperatorBoardGame(operator: String, price: Int)
        fun AddOperatorConsol(operator: String, price: List<Int>)
        fun OnDetach()
    }
    interface PayModelPresenter {
        fun OnAttach(view: ContractPV.PayModelView, views: android.view.View, itemId: Int)
        fun PayFacture(itemId: Int, Status: String)
        fun extractPlayerNames(raw: String?, count: Int): List<String>
        fun OnDetach()
    }

    interface SettingPresenter {

    }

    interface View{
        fun Run(Fragment: Fragment = HomeFragment() )
    }
    interface PlayerView{
        fun Run(Data: List<PlayerItem>)
    }

    interface FactureView{

        fun Run(table: List<FactureItem>)
        fun updatePlayers(players: ArrayList<PlayerItem>, viewd: DialogFactureBinding)
        fun ShowBottomSheet(bottomSheet : PayModel)

        fun CheckPaying(Price: Int, PayedPrice: Int) :Int

    }

    interface HomeView{
        fun Run(item: StatisticItem, popularOP: List<Any>, mostCP: List<Any>, comfortableOP: List<Any>, mostTP: List<Any>)
        fun setTextValues(textViews: List<TextView>, values: List<String>)
        fun SetChart(data: PieData)
    }
    interface ManageOperatorView{
        fun Run(table: List<OperatorItem>)
    }
    interface PayModelView {
        fun Run(Facture: FactureItem)
    }

    interface SettingView {

        fun Run()
        fun click()

    }
}