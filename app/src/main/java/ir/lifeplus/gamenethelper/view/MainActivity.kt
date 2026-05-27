package ir.lifeplus.gamenethelper.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import ir.lifeplus.gamenethelper.ContractPV
import ir.lifeplus.gamenethelper.R
import ir.lifeplus.gamenethelper.databinding.ActivityMainBinding
import ir.lifeplus.gamenethelper.presenter.Presenter

//Activity connected to the activity_main.xml layout
class MainActivity : AppCompatActivity() , ContractPV.View {

    //Connecting the activity to the layout (View) and the presenter (and then the model)
    lateinit var binding : ActivityMainBinding
    lateinit var presenter : ContractPV.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = Presenter()

        //Attach the presenter in order to start the process of displaying the contents of this activity in the view
        presenter.OnAtttach(this)

        fun getIntColor(attResId :Int): Int {
            val tA = this.theme.obtainStyledAttributes(intArrayOf(attResId))
            return try {
                tA.getColor(0, 0)
            } finally {
                tA.recycle()
            }
        }

        val backgroundColor = getIntColor(com.google.android.material.R.attr.colorPrimaryVariant)
        val OnbackgroundColor = getIntColor(com.google.android.material.R.attr.colorOnBackground)
        val ColorTop = getIntColor(com.google.android.material.R.attr.colorSecondary)
        //val b3 = getIntColor(com.google.android.material.R.attr.colorPrimaryVariant)
        binding.root.post {
            val paint = Paint().apply {
                shader = LinearGradient(
                    binding.root.width.toFloat() , binding.root.height.toFloat() , 0f , 0f ,
                    intArrayOf(
                        backgroundColor,
                        OnbackgroundColor,
                        ColorTop
                        //Color.parseColor("#1D1F24")
                    ),
                    floatArrayOf(
                        0f ,
                        0.6f ,
                        0.8f
                    ),
                    Shader.TileMode.CLAMP
                )
            }
            val bitmap = Bitmap.createBitmap(binding.root.width, binding.root.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawRect(0f, 0f, binding.root.width.toFloat(), binding.root.height.toFloat(), paint)
            val drawablebackground = BitmapDrawable(binding.root.resources,bitmap)
            binding.root.background = drawablebackground
        }

        //Setting the default navigation and connecting navigation items to their corresponding fragments for display
        binding.apply {

            navigationBottom.selectedItemId = R.id.nb_Home
            navigationBottom.setOnItemSelectedListener {

                when(it.itemId){

                    R.id.nb_Player ->{ Run( PlayerFragment() ) }
                    R.id.nb_factor ->{ Run( FactureFragment() ) }
                    R.id.nb_Home   ->{ Run( HomeFragment()    ) }

                }
                true

            }

        }

    }

    // Displaying the requested fragment inside the FrameLayout
    override fun Run(Fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.FrameLayout,Fragment)
            commit()
        }

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

    //Detaching and disconnecting from the presenter, and restarting the data stored in presenter
    override fun onDestroy() {
        super.onDestroy()

        presenter.OnDetach()

    }

}



