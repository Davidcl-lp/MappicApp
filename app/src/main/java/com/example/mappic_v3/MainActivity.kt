package com.example.mappic_v3
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mappic_v3.ui.MainScreen
import com.example.mappic_v3.ui.theme.MappicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MappicTheme {
                    MainScreen()
            }
        }
    }
}

