package com.jamesniedfeldt.colorblender

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceHolder
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_color_blender.*

const private val LEFT = 123
const private val RIGHT = 456

class ColorBlender : AppCompatActivity() {

    //Values for left color
    var leftRed = 255
    var leftGreen = 0
    var leftBlue = 0
    //Values for right color
    var rightRed = 0
    var rightGreen = 0
    var rightBlue = 255
    //Values for blended color
    var blendRed = 0
    var blendGreen = 0
    var blendBlue = 0
    //Used for calculating blended color
    var mod = 0.5
    //Canvas for drawing color
    var canvas = Canvas()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_blender)
        supportActionBar!!.setLogo(R.drawable.logo)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)

        blenderBar.max = 100
        blenderBar.progress = 50

        makeViewsDrawable()

        //Make left clickable to send intent to Color Picker
        leftDisplay.setOnClickListener{view ->
            var getIntent = Intent()
            getIntent.action = "com.jamesniedfeldt.colorblender.FETCH_COLOR"
            getIntent.putExtra("GET_COLOR", "")
            startActivityForResult(getIntent, LEFT)
        }
        //Make right clickable to send intent to Color Picker
        rightDisplay.setOnClickListener{view ->
            var getIntent = Intent()
            getIntent.action = "com.jamesniedfeldt.colorblender.FETCH_COLOR"
            getIntent.putExtra("GET_COLOR", "")
            startActivityForResult(getIntent, RIGHT)
        }
        //Set up seekbar
        blenderBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                mod = value.toDouble() / 100
                updateBlendedDisplay()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //Log.i("Log: ","started")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //Log.i("Log: ","stopped")
            }
        })
    }

    //Process intent from Color Picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        var tempParts: List<String>

        //Process into left
        if(requestCode == LEFT && resultCode == RESULT_OK){
            tempParts = data!!.extras["COLOR"].toString().split(",")
            leftRed = tempParts[0].toInt()
            leftGreen = tempParts[1].toInt()
            leftBlue = tempParts[2].toInt()
        }
        //Process into right
        else if(requestCode == RIGHT && resultCode == RESULT_OK){
            tempParts = data!!.extras["COLOR"].toString().split(",")
            rightRed = tempParts[0].toInt()
            rightGreen = tempParts[1].toInt()
            rightBlue = tempParts[2].toInt()
        }
        else{
            Log.i("Error: ", "invalid intent found")
        }
    }

    private fun makeViewsDrawable(){
        /*These overrides are necessary to make sure
        the displays are all drawn at the proper times.*/
        leftDisplay.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                updateLeftDisplay()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                updateLeftDisplay()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }
        })
        rightDisplay.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                updateRightDisplay()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                updateRightDisplay()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }
        })
        blendedDisplay.holder.addCallback(object: SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                updateBlendedDisplay()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                updateBlendedDisplay()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }
        })
    }

    fun updateLeftDisplay(){
        //Draw to left
        if(leftDisplay.holder.surface.isValid){
            canvas = leftDisplay.holder.lockCanvas()
            canvas.drawRGB(leftRed, leftGreen, leftBlue)
            leftDisplay.holder.unlockCanvasAndPost(canvas)
        }
    }
    fun updateRightDisplay(){
        //Draw to right
        if(rightDisplay.holder.surface.isValid) {
            canvas = rightDisplay.holder.lockCanvas()
            canvas.drawRGB(rightRed, rightGreen, rightBlue)
            rightDisplay.holder.unlockCanvasAndPost(canvas)
        }
    }
    fun updateBlendedDisplay(){
        //Draw to blend
        blendRed = ((leftRed * (1-mod)) + (rightRed * mod)).toInt()
        blendGreen = ((leftGreen * (1-mod)) + (rightGreen * mod)).toInt()
        blendBlue = ((leftBlue * (1-mod)) + (rightBlue * mod)).toInt()
        if(blendedDisplay.holder.surface.isValid){
            canvas = blendedDisplay.holder.lockCanvas()
            canvas.drawRGB(blendRed, blendGreen, blendBlue)
            blendedDisplay.holder.unlockCanvasAndPost(canvas)
        }
    }
}
