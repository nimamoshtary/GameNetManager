package ir.lifeplus.gamenethelper.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PlayerItem::class,FactureItem::class,OperatorItem::class,StatisticItem::class],
    version = 22,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class database :RoomDatabase(){

    abstract val  FactureDao: FactureDao
    abstract val  PlayerDao: PlayerDao
    abstract val  OperatorDao: OperatorDao
    abstract val  StatisticDao: StatisticDao

    companion object{

        @Volatile
        private var databasee: database? = null
        fun getdb(context: Context): database {

            synchronized(this){

                var instance= databasee
                if(instance==null){
                    instance= Room.databaseBuilder(context.applicationContext,
                        database::class.java,"DB")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration(false)
                        .build()
                    }
                return instance

            }

        }

    }

}