package com.github.mutoxu_n.splitapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class EditReceiptActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            SplitAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                }
//            }
        }
    }
}