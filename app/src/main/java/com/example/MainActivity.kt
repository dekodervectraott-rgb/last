package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AddEditScreen
import com.example.ui.screens.CheatSheetScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImportExportScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TechBlack
import com.example.ui.theme.TechBlackPure
import com.example.ui.theme.TechOrange
import com.example.ui.theme.TechOrangeBright
import com.example.ui.theme.TechOrangeContainer
import com.example.ui.theme.TechSurface
import com.example.ui.theme.TechTextPrimary
import com.example.ui.theme.TechTextSecondary
import com.example.ui.theme.TechWhite
import com.example.ui.theme.TechWhiteBorderSubtle
import com.example.ui.viewmodel.IntercomViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Katalog", Icons.Filled.Pin, Icons.Outlined.Pin)
    object Add : Screen("add", "Dodaj", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline)
    object Cloud : Screen("cloud", "Chmura / Pliki", Icons.Filled.CloudSync, Icons.Outlined.CloudQueue)
    object CheatSheet : Screen("cheatsheet", "Ściąga", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
}

class MainActivity : ComponentActivity() {

    private val viewModel: IntercomViewModel by viewModels {
        IntercomViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                RccMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RccMainApp(
    viewModel: IntercomViewModel
) {
    var currentScreen by remember { mutableStateOf<String>(Screen.Home.route) }
    var editingEntryId by remember { mutableStateOf<Long?>(null) }

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Add,
        Screen.Cloud,
        Screen.CheatSheet
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TechBlack,
        bottomBar = {
            Surface(
                color = TechSurface,
                border = BorderStroke(1.dp, TechWhiteBorderSubtle)
            ) {
                NavigationBar(
                    containerColor = TechSurface,
                    contentColor = TechWhite,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentScreen == screen.route && editingEntryId == null
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                editingEntryId = null
                                currentScreen = screen.route
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TechOrangeBright,
                                selectedTextColor = TechOrangeBright,
                                unselectedIconColor = TechTextSecondary,
                                unselectedTextColor = TechTextSecondary,
                                indicatorColor = TechOrangeContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TechBlack)
        ) {
            AnimatedContent(
                targetState = Pair(currentScreen, editingEntryId),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { (screen, editId) ->
                when {
                    editId != null -> {
                        AddEditScreen(
                            viewModel = viewModel,
                            entryId = editId,
                            onNavigateBack = {
                                editingEntryId = null
                            }
                        )
                    }
                    screen == Screen.Home.route -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToAdd = {
                                editingEntryId = null
                                currentScreen = Screen.Add.route
                            },
                            onNavigateToEdit = { id ->
                                editingEntryId = id
                            }
                        )
                    }
                    screen == Screen.Add.route -> {
                        AddEditScreen(
                            viewModel = viewModel,
                            entryId = null,
                            onNavigateBack = {
                                currentScreen = Screen.Home.route
                            }
                        )
                    }
                    screen == Screen.Cloud.route -> {
                        ImportExportScreen(
                            viewModel = viewModel
                        )
                    }
                    screen == Screen.CheatSheet.route -> {
                        CheatSheetScreen()
                    }
                }
            }
        }
    }
}
