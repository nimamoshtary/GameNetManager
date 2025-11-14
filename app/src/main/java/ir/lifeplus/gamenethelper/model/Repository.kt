package ir.lifeplus.gamenethelper.model

import androidx.sqlite.db.SimpleSQLiteQuery

class Repository(private val dao: OperatorDao) {
    fun getXSortByX(column:String,orderBy:String):List<Any> {
        val query = SimpleSQLiteQuery("SELECT $column FROM TableOperator ORDER BY $orderBy DESC")
        return dao.getXSortByX(query)
    }
}