package ir.lifeplus.gamenethelper.model

import androidx.sqlite.db.SimpleSQLiteQuery

//Convert the string of column names into an SQLite query using SimpleSQLiteQuery to retrieve the entered column data, ordered DESC by the second entered column.
class Repository(private val dao: OperatorDao) {

    fun getXSortByX(column:String,orderBy:String?):List<Any> {

        val query = if( orderBy.isNullOrEmpty() ){

            SimpleSQLiteQuery("SELECT $column FROM TableOperator")

        }else{

            SimpleSQLiteQuery("SELECT $column FROM TableOperator ORDER BY $orderBy DESC")

        }

        return dao.getXSortByX(query)

    }

}