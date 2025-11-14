package ir.lifeplus.gamenethelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity("tablePlayer")
data class PlayerItem(
    @PrimaryKey(autoGenerate = true)
    val Id: Int?=null, //آی دی یونیک تولید توسط سیستم
    val Name: String, //نام کامل
    val Username: String?=null, //نام کاربری(شماره تلفن)
    val ImageURL: String?=null, //آدرس پروفایل
    val CardId: Int?=null, //شماره ملی
    val Phone: Long, //شماره تماس
    val Sub: String?=null, //توضیحات یا بیوگرافی
    val Friends: String?=null, //لیست از اینتجر ها
    val Inviter: String?=null, //دعوت کننده
    val Log: Boolean = true, //مجوز لاگ انداختن
    val Cash: Int = 0, //موجودی
    val TimePlay: String = "0",
    val NumberAchievments: Int = 1, //تعداد دستاورد ها
    val Achievments: String?=null, //لیستی از دستاورد ها
    val PlayedTimeH: String = "0", //
    val PayedPrice: Int = 0, //پول پرداخت شده
    val NumberOfAttendances: Int = 1, //تعداد حضور
    val Attendance: Boolean = false, //وضعیت حضور
    val Centers: String?=null) // لیستی از مراکز

data class Center(
    val CenterName: String,
    val TimeSpent: String,
    val NumberReferrals: Int = 1,
    val CostSpent: Int
)
data class Achievment(
    val id: Int,
    val Name: String,
    val Rank: Int,
    val date: String,
)

@Entity("tableFacture")
data class FactureItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val CreateDate: String,
    val Operator: String,
    val GamesName: String?=null,
    val Sub: String?=null,
    val Price: Int = 0,
    val PlayedTimeH: String = "0",
    val PlayerCount: Int = 1,
    val Players: String?=null,
    val StartTime: String,
    var PausedTime: String?=null,
    val EndTime: String?=null,
    val Note: String?=null,
    val PayedTime :String?=null,
    val PayedDate :String?=null,
    val PayedPrice: Int = 0,
    val Status: Boolean = false)

@Entity("tableOperator")
data class OperatorItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val Operator: String,
    val Model: String,
    val CountPlayer: Int?=0,
    val TimePlayed: Int?=0,
    val PriceBG: Int?=null,
    val PriceCO: Int?=null,
    val PriceCTW: Int?=null,
    val PriceCTH: Int?=null,
    val PriceCFR: Int?=null,
    val lastcleanup: String?=null)

@Entity ("tableStatistic")
data class StatisticItem(
    @PrimaryKey
    val date: String,
    val Pricereceived: Int,
    val PriceWithPlayer: Int,
    val PriceSpent: Int,
    val PlayerCount: Int,
    val TimePlayed: Int,
    val FactureCount: Int
)

class Converters {
    @TypeConverter
    fun fromList(Players: List<Any>): String {
        return Players.joinToString(",")
    }

    @TypeConverter
    fun toList(Players: String): List<String> {
        return Players.split(",").map { it.toString() }
    }
}