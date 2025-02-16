package dev.bltucker.echojournal.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.bltucker.echojournal.R
import dev.bltucker.echojournal.common.Mood
import dev.bltucker.echojournal.common.room.JournalEntry
import dev.bltucker.echojournal.common.theme.EchoJournalTheme
import dev.bltucker.echojournal.common.theme.GradientColors
import dev.bltucker.echojournal.home.composables.JournalListSection
import dev.bltucker.echojournal.home.composables.PermissionBanner
import dev.bltucker.echojournal.home.composables.RecordingBottomSheet
import java.time.Instant
import java.time.format.DateTimeFormatter

const val HOME_SCREEN_ROUTE = "home"

fun NavGraphBuilder.homeScreen(onNavigateToSettings: () -> Unit,
                               onNavigateToCreateEntry: (String) -> Unit) {
    composable(route = HOME_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<HomeScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()
        val context = LocalContext.current
        val activity = LocalActivity.current

        var showSettingsDialog by remember { mutableStateOf(false) }
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            val canShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                )
            } ?: false
            viewModel.updatePermissionState(isGranted, canShowRationale)
        }

        LaunchedEffect(model.finishedRecordingId){
            val finishedRecordingId = model.finishedRecordingId
            if(finishedRecordingId!= null){
                onNavigateToCreateEntry(finishedRecordingId)
                viewModel.onHandledFinishedRecording()
            }
        }

        LaunchedEffect(Unit) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            viewModel.initializePermissionState(hasPermission,)
        }

        LaunchedEffect(model.permissionState.shouldShowPermissionRequest) {
            if (model.permissionState.shouldShowPermissionRequest) {
                if (model.permissionState.userHasRepeatedlyDenied) {
                    showSettingsDialog = true
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
                viewModel.onShowRequestHandled()
            }
        }

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }


        if(showSettingsDialog){
            PermissionAlertDialog(onDismiss = { showSettingsDialog = false })
        }

        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onNavigateToSettings = onNavigateToSettings,
            onClickCreateEntryFab = viewModel::onClickCreateEntry,

            onStartRecording = viewModel::onStartRecording,
            onPauseRecording = viewModel::onPauseRecording,
            onResumeRecording = viewModel::onResumeRecording,
            onCancelRecording = viewModel::onCancelRecording,
            onFinishRecording = viewModel::onFinishRecording,
            onPlayPauseClick = viewModel::onPlayPauseClick,
            onRequestPermission = {
                if(model.permissionState.userHasRepeatedlyDenied){
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    model: HomeModel,
    onNavigateToSettings: () -> Unit,
    onClickCreateEntryFab: () -> Unit,

    onStartRecording: () -> Unit = {},
    onPauseRecording: () -> Unit = {},
    onResumeRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    onFinishRecording: () -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onPlayPauseClick: (String) -> Unit,
) {

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
                onClick = onClickCreateEntryFab,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add),
                    contentDescription = "Add"
                )
            }
        }
    ) { paddingValues ->

        if (model.entries.isEmpty()) {
            EmptyHomeScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                permissionState = model.permissionState,
                onRequestPermission = onRequestPermission
            )
        } else {
            HomeScreenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                model = model,
                onRequestPermission = onRequestPermission,
                onPlayPauseClick = onPlayPauseClick,
            )
        }

        if (model.showRecordingBottomSheet) {
            RecordingBottomSheet(
                state = model.recordingState,
                onStartRecording = onStartRecording,
                onPauseRecording = onPauseRecording,
                onResumeRecording = onResumeRecording,
                onCancelRecording = onCancelRecording,
                onFinishRecording = onFinishRecording
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    model: HomeModel,
    onRequestPermission: () -> Unit,
    onPlayPauseClick: (String) -> Unit,
) {

    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("EEEE, MMM dd") }


    LazyColumn(modifier = modifier) {

        if(!model.permissionState.hasAudioPermission){
            item {
                PermissionBanner(
                    modifier = Modifier.fillMaxWidth(),
                    onRequestPermission = onRequestPermission
                )
            }
        }


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
                    onPlayPauseClick = onPlayPauseClick,
                    onShowMoreClick = { /* TODO */ },
                    onTopicClick = { /* TODO */ },
                )
            }
        }
    }


}


@Composable
private fun EmptyHomeScreenContent(
    modifier: Modifier = Modifier,
    permissionState: PermissionState,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = !permissionState.hasAudioPermission,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            PermissionBanner(
                modifier = Modifier.fillMaxWidth(),
                onRequestPermission = onRequestPermission
            )
        }

        Spacer(modifier = Modifier.weight(1F))

        Image(
            painter = painterResource(R.drawable.empty_entries_icon),
            contentDescription = "Empty Entries Icon",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(34.dp))

        Text(text = "No Entries", style = MaterialTheme.typography.headlineMedium)

        Text(text = "Start recording your first Echo", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.weight(1F))
    }
}

@Composable
private fun PermissionAlertDialog(onDismiss: () -> Unit){
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Permission Required") },
        text = {
            Text("Audio recording permission is required for this feature. " +
                    "Please enable it in app settings.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            ) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val homeModel = HomeModel(
        entries = listOf(
            JournalEntry(
                title = "Entry 1",
                description = "lorem ipsum",
                createdAt = Instant.now(),
                audioFilePath = "",
                durationSeconds = 120,
                mood = Mood.PEACEFUL,
                transcription = "",
            )
        )
    )

    EchoJournalTheme {
        HomeScreen(
            model = homeModel,
            onNavigateToSettings = {},
            onClickCreateEntryFab = {},
            onRequestPermission = {},
            onPlayPauseClick = {},
        )
    }
}

@Preview
@Composable
private fun HomeScreenEmptyPreview() {
    EchoJournalTheme {
        EmptyHomeScreenContent(
            modifier = Modifier.fillMaxSize(),
            permissionState = PermissionState(true, false),
            onRequestPermission = {},
        )
    }
}
