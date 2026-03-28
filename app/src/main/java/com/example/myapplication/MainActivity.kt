package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.components.AppScaffold
import com.example.myapplication.presentation.navigation.TodoNavGraph
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // SPEC-602: AppScaffold로 감싸서 전역 에러 Snackbar 활성화
                AppScaffold {
                    val navController = rememberNavController()
                    TodoNavGraph(navController = navController)
                }
            }
        }
    }
}
