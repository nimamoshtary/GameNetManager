package ir.lifeplus.gamenethelper.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)

}

@Dao
interface FactureDao : BaseDao<FactureItem> {
    @Query("SELECT * FROM TableFacture ")
    fun getAllFacture(): List<FactureItem>
    @Query("SELECT * FROM TableFacture WHERE id = :Id")
    fun getFactureById(Id:Int): FactureItem
    @Query("SELECT * FROM TABLEFACTURE WHERE CreateDate = :Date")
    fun getFacturesByDate(Date:String) :List<FactureItem>
}
@Dao
interface PlayerDao : BaseDao<PlayerItem> {
    @Query("SELECT * from TablePlayer")
    fun getAllPlayer(): List<PlayerItem>
    @Query("SELECT NAME FROM TablePlayer")
    fun getTitlePlayer(): List<String>
    @Query("SELECT * FROM TablePlayer WHERE Name = :name")
    fun getPlayerByName(name:String): PlayerItem
}
@Dao
interface OperatorDao : BaseDao<OperatorItem> {
    @Query("SELECT * from tableOperator")
    fun getAllOperator(): List<OperatorItem>
    @Query("SELECT Operator FROM TableOperator")
    fun getTitleOperator(): List<String>
    @Query("SELECT CountPlayer FROM TableOperator ORDER BY CountPlayer DESC")
    fun getPlayerCountSortByPlayer(): List<Int>
    @RawQuery
    fun getXSortByX(query: SupportSQLiteQuery) :List<String> //Get two column names in the constructor and return a list of the first column and sort by the second column
    @Query("SELECT * FROM tableOperator WHERE Operator = :name")
    fun getOperatorByName(name:String): OperatorItem
}
@Dao
interface StatisticDao : BaseDao<StatisticItem> {
    @Query("SELECT * FROM tableStatistic")
    fun getAllStatistic() :List<StatisticItem>
    @Query("SELECT * FROM tableStatistic WHERE date BETWEEN :StartDate And :EndDate")
    fun getWeekStatistic(StartDate:String, EndDate:String) :List<StatisticItem>
    //بالایی انجام نمیشه
    @Query("SELECT * FROM tableStatistic WHERE date = :statisticsDate")
    fun getStatisticByDate( statisticsDate:String) :StatisticItem?
    @Query("SELECT Pricereceived FROM tableStatistic ORDER BY Pricereceived DESC LIMIT 1")
    fun getStatisticSortInTop() :List<Int>
}