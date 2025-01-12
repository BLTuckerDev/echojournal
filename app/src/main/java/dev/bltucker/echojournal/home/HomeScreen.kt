package dev.bltucker.echojournal.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.GradientColors
import dev.bltucker.echojournal.home.composables.JournalListSection
import java.time.format.DateTimeFormatter

const val HOME_SCREEN_ROUTE = "home"

fun NavGraphBuilder.homeScreen(onNavigateToSettings: () -> Unit) {
    composable(route = HOME_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<HomeScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()


        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onNavigateToSettings = onNavigateToSettings
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    model: HomeModel,
    onNavigateToSettings: () -> Unit
) {
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMM dd") }


    Scaffold(modifier = modifier
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    GradientColors.BgGradientStart,
                    GradientColors.BgGradientEnd
                ),
                tileMode = TileMode.Clamp
            )
        ),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(title = { Text("Your EchoJournal") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),

                actions = {
                    IconButton(onClick = {
                        onNavigateToSettings()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_settings),
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { /* TODO */ },
                containerColor = MaterialTheme.colorScheme.primary,
            ){
                Icon(
                    painter = painterResource(R.drawable.icon_add),
                    contentDescription = "Add"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            model.entriesByDay.forEach { (section, entries) ->
                item(key = "header_${section}") {
                    JournalListSection(
                        headerText = when (section) {
                            DaySection.Today -> "TODAY"
                            DaySection.Yesterday -> "YESTERDAY"
                            is DaySection.Date -> {
                                section.date.format(
                                    dateTimeFormatter
                                )
                            }
                        },
                        items = entries,
                        onPlayPauseClick = { /* TODO */ },
                        onShowMoreClick = { /* TODO */ },
                        onTopicClick = { /* TODO */ }
                    )
                }
            }
        }

    }
}

@Preview
@Composable
private fun HomeScreenPreview(){
    EchoJournalTheme {
        HomeScreen(model = HomeModel(), onNavigateToSettings = {})
    }
}


