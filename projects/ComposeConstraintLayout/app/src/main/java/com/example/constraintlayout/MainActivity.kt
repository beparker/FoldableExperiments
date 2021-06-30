package com.example.constraintlayout

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.compose.*

class MainActivity : AppCompatActivity() {
    private var mFrameLayout: FrameLayout? = null
    private var composeNum = 28
    private var MAX = 32
    var map = HashMap<Int,String>();
   init {
       var count = 24;
       map.put(24,"scaleX/Y")
       map.put(25,"tanslationX/Y")
       map.put(26,"rotationZ")
       map.put(27,"rotationXY")
       map.put(28,"Cycle Scale")
       map.put(29,"Cycle TranslationXY")
       map.put(30,"Cycle RotationZ")
       map.put(31,"Cycle RotationXY")
    }

    private fun show(com: ComposeView) {
        com.setContent() {
            when (composeNum) {
                0 -> ScreenExample()
                1 -> ScreenExample()
                2 -> ScreenExample2()
                3 -> ScreenExample3()
                4 -> ScreenExample4()
                5 -> ScreenExample5()
                6 -> ScreenExample6()
                7 -> ScreenExample7()
                8 -> ScreenExample8()
                9 -> ScreenExample9()
                10 -> ScreenExample10()
                11 -> ScreenExample11()
                12 -> ScreenExample12()
                13 -> ScreenExample13()
                14 -> ScreenExample14()
                15 -> ScreenExample15()
                16 -> ScreenExample16()
                17 -> ScreenExample17()
                18 -> ScreenExample18()
                19 -> MotionExample1()
                20 -> MotionExample2()
                21 -> MotionExample3()
                22 -> MotionExample4()
                23 -> MotionExample5()

                24 -> AttributesScale()
                25 -> AttributesTranslationXY()
                26 -> AttributesRotationZ()
                27 -> AttributesRotationXY()

                28 -> CycleScale()
                29 -> CycleTranslationXY()
                30 -> CycleRotationZ()
                31 -> CycleRotationXY()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFrameLayout = findViewById<FrameLayout>(R.id.frame)
        setCompose();
    }

    fun setCompose() {
        if (mFrameLayout!!.childCount > 0) {
            mFrameLayout!!.removeAllViews()
        }
        var sub   = " example " + composeNum
        if (map.containsKey(composeNum)) {
            sub += " "+ map.get(composeNum)
        }
        title = sub;
        findViewById<TextView>(R.id.layoutName).text = "! example " + composeNum;
        var com = ComposeView(this);
        mFrameLayout!!.addView(com)
        show(com)
    }

    fun prev(view: View) {
        composeNum = (composeNum + MAX - 1) % MAX
        setCompose()
    }

    fun next(view: View) {
        composeNum = (composeNum + 1) % MAX
        setCompose();
    }
}
