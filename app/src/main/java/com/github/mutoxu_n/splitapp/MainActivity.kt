package com.github.mutoxu_n.splitapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.mutoxu_n.splitapp.firebase.Auth
import com.github.mutoxu_n.splitapp.ui.theme.SplitAppTheme

class MainActivity : ComponentActivity() {
    private var isSignedIn: Boolean by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LogoDisplay(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = "${Auth.get().auth.uid}"
                        )

                        Button(onClick = { signIn() }) {
                            Text(
                                text = if (isSignedIn) "Sign Out" else "Sign In"
                            )

                        }
                    }
                }
            }
        }
    }

    private fun signIn() {
        val auth = Auth.get()
        auth.signIn()
    }
}

@Composable
private fun LogoDisplay(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(LocalConfiguration.current.screenWidthDp.dp*0.8f),
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = null
    )
}