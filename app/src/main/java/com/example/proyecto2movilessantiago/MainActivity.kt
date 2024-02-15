package com.example.proyecto2movilessantiago

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar
        setSupportActionBar(findViewById(R.id.myToolbar))
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)


        if(savedInstanceState == null){
            val startFragment = InitialFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, startFragment).commit()
        }
    }
}