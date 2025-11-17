package com.arandadiegoa.kindystarts.ui.router

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.arandadiegoa.kindystarts.R
import com.arandadiegoa.kindystarts.ui.auth.LoginActivity
import com.arandadiegoa.kindystarts.ui.auth.RegisterActivity
import com.arandadiegoa.kindystarts.ui.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var textMessages: Array<String>
    private lateinit var textSwitcher: TextSwitcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Reference
        textSwitcher = findViewById(R.id.textSwitcherWelcome)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        //load Arrays
        textMessages = resources.getStringArray(R.array.mensajes_bienvenida)

        //wiews and actions
        setupTextSwitcher()

        //actions buttons
        buttonLogin.setOnClickListener {
            //create intent
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

        buttonRegister.setOnClickListener {
            //create intent
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //start
        startCarousel()
    }

    private fun setupTextSwitcher(){
        textSwitcher.setFactory {
            val textView = TextView(this@MainActivity)
            textView.textSize = 22f
            textView.gravity = Gravity.CENTER_HORIZONTAL
            //textView.setBackgroundResource(R.drawable.dialogo_background)
            textView.setPadding(48,48,48,48)
            textView
        }
        textSwitcher.setInAnimation(this, R.anim.fade_in)
        textSwitcher.setOutAnimation(this, R.anim.fade_out)
    }

        //coroutine
        private fun startCarousel(){
        lifecycleScope.launch {
            var currentIndex = 0
            while (isActive) {
                textSwitcher.setText(textMessages[currentIndex])
                currentIndex = (currentIndex + 1) % textMessages.size
                delay(3000)
            }
        }


    }
}