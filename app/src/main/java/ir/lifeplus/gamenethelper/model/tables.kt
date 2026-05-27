package ir.lifeplus.gamenethelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

//A table of players. Many of the details are not yet used but are being recorded so they can be utilized in the next version
@Entity("tablePlayer")
data class PlayerItem(
    //A unique ID that is sequentially assigned to players
    @PrimaryKey(autoGenerate = true)
    val Id: Int?=null,
    //Full name of the player
    val Name: String,
    //Player username (by default the player's phone number is used)
    val Username: String?=null,
    //address of the player's profile picture
    val ImageURL: String?=null,
    //National ID of the player
    val CardId: Int?=null,
    //Mobile phone number of the player
    val Phone: Long,
    //Player description or biography
    val Sub: String?=null,
    //Friend IDs stored as a string (with a defined format for conversion to a list if needed)
    val Friends: String?=null,
    //Inviter ID of the player
    val Inviter: String?=null,
    //Permission to record entry and exit logs at centers
    val Log: Boolean = true,
    //Player wallet balance
    val Cash: Int = 0,
    //Money the player spent at the center
    val PayedPrice: Int = 0,
    //Unknown!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    val TimePlay: String = "0",
    //Total time the player has spent playing at the center
    val PlayedTimeH: String = "0",
    //Number of achievements the player has earned
    val NumberAchievments: Int = 1,
    //List of achievements the player has received
    val Achievments: String?=null,
    //Number of times the player has attended the center
    val NumberOfAttendances: Int = 1,
    //Current attendance status of the player at the center
    val Attendance: Boolean = false,
    //List of centers the player has visited, stored as a string (with a defined format for conversion to a list if needed)
    val Centers: String?=null)
//Password column for the player (required for account login) <= Next Version

//Coming Soon . . . {
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
//}

//A table of Factures. Data in this table is deleted every week after statistics are recorded in the statistics table
@Entity("tableFacture")
data class FactureItem(
    //A unique ID that is sequentially assigned to factures
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    //facture creation date
    val CreateDate: String,
    //Operator involved in the facture
    val Operator: String,
    //Games played on the operator related to this facture
    val GamesName: String?=null,
    //facture notes or description
    val Sub: String?=null,
    //Cost and payable amount by the customer for this facture
    val Price: Int = 0,
    //Playtime on the operator related to this facture (in hours)
    val PlayedTimeH: String = "0",
    //Number of players who played on the operator related to this facture
    val PlayerCount: Int = 1,
    //List of players who played on the operator related to this facture (stored as a String convertible to an ArrayList)
    val Players: String?=null,
    //Game start time of the player on the operator related to this facture
    val StartTime: String,
    //facture stop time
    var PausedTime: String?=null,
    //Game end time of the players on the operator related to this facture
    val EndTime: String?=null,
    //Note written at the time of payment
    val Note: String?=null,
    //facture payment time
    val PayedTime :String?=null,
    //facture payment date
    val PayedDate :String?=null,
    //Money or credit paid so far for this facture
    val PayedPrice: Int = 0,
    //facture payment status
    val Status: Boolean = false)

//A table of Operators. Operator stats reset weekly after report submission
@Entity("tableOperator")
data class OperatorItem(
    //A unique ID that is sequentially assigned to operators
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    //Operator display name
    val Operator: String,
    //Operator type
    val Model: String,
    //Number of players who played with this operator this week
    val CountPlayer: Int?=0,
    //Hours played with the operator this week
    val TimePlayed: Int?=0,
    //Cost in operators of type boardgame and billiard, calculated as (hourly rate * number of players)
    val CostByCountPlayer: Int?=null,
    //Cost in operators of type console and video game system, calculated based on the time a single controller or device is used
    val CostConsoleSingelController: Int?=null,
    //Cost in operators of type console and video game system, calculated based on the time two controllers or devices is used
    val CostConsoleTwoController: Int?=null,
    //Cost in operators of type console and video game system, calculated based on the time three controllers or devices is used
    val CostConsoleThreeController: Int?=null,
    //Cost in operators of type console and video game system, calculated based on the time four controllers or devices are used
    val CostConsoleFourController: Int?=null,
    //Date of last statistics reset for this operator.
    val lastcleanup: String?=null)

//A table of Statistics. Daily statistics are stored in this table, and currently there is no cleanup plan, which may cause memory leak!!
@Entity ("tableStatistic")
data class StatisticItem(
    //Unique date value that calculated that day's statistics
    @PrimaryKey
    val date: String,
    //Center revenue on this day
    val Pricereceived: Int,
    //Center revenue from regular customers on this day
    val PriceWithPlayer: Int,
    //Center expenses on this day
    val PriceSpent: Int,
    //Number of players who visited the center on this day
    val PlayerCount: Int,
    //Total time all players spent in the center playing
    val TimePlayed: Int,
    //Number of invoices recorded on this day
    val FactureCount: Int
)

//A class that converts a formatted string to a list and can also convert a list back to a formatted string
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