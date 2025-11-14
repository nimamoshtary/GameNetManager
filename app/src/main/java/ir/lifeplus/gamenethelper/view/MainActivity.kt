package ir.lifeplus.gamenethelper.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.transition.Visibility
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.ActivityMainBinding
import ir.lifeplus.gamenethelper.presenter.Presenter

class MainActivity : AppCompatActivity() , ContractPV.View {
    lateinit var binding : ActivityMainBinding
    lateinit var presenter : ContractPV.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = Presenter()
        binding.navigationBottom.selectedItemId = R.id.nb_Home
        presenter.OnAtttach(this)

        binding.navigationBottom.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nb_Player ->{ Run( PlayerFragment()  ) }
                R.id.nb_factor ->{ Run( FactureFragment() ) }
                R.id.nb_Home   ->{ Run( HomeFragment()    ) }
            }
            true
        }
    }
    //=============> functions ->
    override fun Run(Fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FrameLayout,Fragment)
        transaction.commit()

        when (Fragment) {
            is HomeFragment -> {
                binding.ap1Linearlayout1.visibility = View.GONE
                binding.ap1Linearlayout2.visibility = View.GONE
            }
            is PlayerFragment -> {
                binding.ap1Linearlayout1.visibility = View.VISIBLE
                binding.ap1Linearlayout2.visibility = View.VISIBLE
            }
            is FactureFragment -> {
                binding.ap1Linearlayout1.visibility = View.GONE
                binding.ap1Linearlayout2.visibility = View.GONE
            }

        }
    }
    //=============//
    override fun onDestroy() {
        super.onDestroy()
        presenter.OnDetach()
    }
}



