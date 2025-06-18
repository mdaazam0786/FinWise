package com.example.finwise

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finwise.biometric.BiometricPromptManager
import com.example.finwise.ui.navgraph.RootNavGraph
import com.example.finwise.ui.theme.FinWiseTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val Context.dataStore by preferencesDataStore(name = "onboarding_prefs")

    private val onBoardingComplete = booleanPreferencesKey("onboarding_completed")

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            FinWiseTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                var showOnboarding by remember { mutableStateOf(true) }

                val biometricResult by promptManager.promptResults.collectAsState(
                    initial = null
                )
                val enrollLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        println("Activity result: $it")
                    }
                )
                LaunchedEffect(biometricResult) {
                    if(biometricResult is BiometricPromptManager.BiometricResult.AuthenticationNotSet) {
                        if (Build.VERSION.SDK_INT >= 30) {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )
                            }
                            enrollLauncher.launch(enrollIntent)
                        }
                    }

                }
                // Load the onboarding status from DataStore on first composition
                LaunchedEffect(key1 = Unit) {
                    scope.launch {
                        val preferences = context.dataStore.data.first()
                        showOnboarding = preferences[onBoardingComplete] ?: true
                    }
                }
                LaunchedEffect(key1 = Unit) {
                    promptManager.showBiometricPrompt(
                        title = "Unlock Finwise Application",
                        description = "Enter Device Fingerprint"
                    )
                }

                biometricResult?.let { result ->
                    when(result){
                        is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                            Text(text = result.error)
                        }
                        BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                            Text(text = "Authentication failed")
                        }
                        BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                            Text(text = "Authentication not set")
                        }
                        BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                            RootNavGraph(
                                navController = navController,
                                scope = scope,
                                onBoardingComplete = onBoardingComplete,
                                datastore = dataStore,
                                currentUser = currentUser
                            )
                        }
                        BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                            Text(text = "Feature unavailable")
                        }
                        BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                            Text(text = "Hardware unavailable")
                        }
                    }

                }
            }
        }
    }
}

