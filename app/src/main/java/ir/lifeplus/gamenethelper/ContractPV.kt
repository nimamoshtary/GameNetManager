package ir.lifeplus.gamenethelper

import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import ir.lifeplus.gamenethelper.databinding.DialogFactureBinding
import ir.lifeplus.gamenethelper.model.FactureItem
import ir.lifeplus.gamenethelper.model.OperatorItem
import ir.lifeplus.gamenethelper.model.PlayerDao
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
        fun OnDetach()
        fun AddPlayerToList(name: String, viewd: DialogFactureBinding)
        fun AddNewFacture(viewd: DialogFactureBinding)
        fun ClearPlayersList()
        fun UpdateFacture(item: FactureItem)
        fun GetPlayerName():List<String>
        fun GetOperatorsName() :List<String>
        fun GetOperatorByName(operator: String) :OperatorItem
        fun ShowPayModel(itemId: Int)
        fun TimeCalculatorM(Start:String, End:String):Double
        fun FinishTime(cur:FactureItem)
    }
    interface HomePresenter{
        fun OnAttach(view: HomeView, views:android.view.View)
        fun GetChart()
    }
    interface ManageOperatorPresenter{
        fun OnAttach(view: ManageOperatorView, views: android.view.View)
        fun AddOperatorBoardGame(operator: String, model: String, price: Int)
        fun AddOperatorConsol(operator: String, model: String, price: List<Int>)
        fun OnDetach()
    }
    interface PayModelPresenter {
        fun OnAttach(view: ContractPV.PayModelView, views: android.view.View, itemId: Int)
        fun EditFacture(item:FactureItem)
        fun PayFacture(itemId: Int, Status: String)
        fun extractPlayerNames(raw: Any?, count: Int): List<String>
        fun OnDetach()
    }

    interface View{
        fun Run(Fragment: Fragment = HomeFragment() )
    }
    interface PlayerView{
        fun Run(table: PlayerDao)
    }
    interface FactureView{
        fun Run(table: List<FactureItem>)
        fun updatePlayers(players: ArrayList<PlayerItem>, viewd: DialogFactureBinding)
        fun ShowBottomSheet(bottomSheet : PayModel)
    }
    interface HomeView{
        fun Run(item: StatisticItem, popularOP: List<Any>, mostCP: List<Any>, comfortableOP: List<Any>, mostTP: List<Any>)
        fun SetChart(data: PieData)
        fun CheckNewWeek()
    }
    interface ManageOperatorView{
        fun Run(table: List<OperatorItem>)
    }
    interface PayModelView {
        fun Run(Facture: FactureItem)
    }
}