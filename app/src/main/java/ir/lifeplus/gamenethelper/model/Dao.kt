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

    //Insert data of type T in the child class so it can be added to its Dao or replaced if duplicate.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)
    //Insert data of type T in the child class where its PrimaryKey already exists in the database so the new information replaces the old.
    @Update
    fun update(obj: T)
    //Insert data of type T in the child class where its PrimaryKey already exists in the system so that information is deleted.
    @Delete
    fun delete(obj: T)

}

@Dao
interface FactureDao : BaseDao<FactureItem> {

    //Retrieve and return the list of all facture
    @Query("SELECT * FROM TableFacture ")
    fun getAllFacture(): List<FactureItem>
    //Retrieve and return the facture with the given Id
    @Query("SELECT * FROM TableFacture WHERE id = :Id")
    fun getFactureById(Id:Int): FactureItem
    //Retrieve and return the facture for the given dates.
    @Query("SELECT * FROM TABLEFACTURE WHERE CreateDate = :Date")
    fun getFacturesByDate(Date:String) :List<FactureItem>

    @Query("DELETE FROM TABLEFACTURE")
    fun reset()

}

@Dao
interface PlayerDao : BaseDao<PlayerItem> {

    //Retrieve and return the list of all players
    @Query("SELECT * from TablePlayer")
    fun getAllPlayer(): List<PlayerItem>
    //Retrieve and return the list of all player names.
    @Query("SELECT NAME FROM TablePlayer")
    fun getTitlePlayer(): List<String>
    //Retrieve and return the player information for the given name.
    @Query("SELECT * FROM TablePlayer WHERE Name = :name")
    fun getPlayerByName(name:String): PlayerItem

}

@Dao
interface OperatorDao : BaseDao<OperatorItem> {

    //Retrieve and return the list of all operators
    @Query("SELECT * from tableOperator")
    fun getAllOperator(): List<OperatorItem>
    //Retrieve and return a list of column data you entered (X), sorted by the column you specified (X).
    @RawQuery
    fun getXSortByX(query: SupportSQLiteQuery) :List<String>
    //Retrieve and return the operator information for the given name
    @Query("SELECT * FROM tableOperator WHERE Operator = :name")
    fun getOperatorByName(name:String): OperatorItem

}

@Dao
interface StatisticDao : BaseDao<StatisticItem> {

    //Retrieve and return the list of all statistics
    @Query("SELECT * FROM tableStatistic")
    fun getAllStatistic() :List<StatisticItem>
    //Retrieve and return the date statistics you entered
    @Query("SELECT * FROM tableStatistic WHERE date = :statisticsDate")
    fun getStatisticByDate( statisticsDate:String) :StatisticItem?

}