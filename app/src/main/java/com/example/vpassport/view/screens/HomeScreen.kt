@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpassport.R
import com.example.vpassport.model.data.TempPass
import com.example.vpassport.model.data.ProfileEntry
import com.example.vpassport.model.data.History
import com.example.vpassport.util.events.HistoryEvent
import com.example.vpassport.util.states.HistoryState
import com.example.vpassport.view.dialogs.ConfirmationDialog
import com.example.vpassport.view.theme.icon
import com.example.vpassport.view.theme.spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tempPass: TempPass,
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded)
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetTonalElevation = 20.dp,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(650.dp)
            ) {
                UserProfile(tempPass = tempPass)
            }

        },
        sheetContainerColor = MaterialTheme.colorScheme.surface
    ) {
        if(state.isAddingHistory) {
            ConfirmationDialog(state = state, onEvent = onEvent)
        }
        Column(
            modifier = Modifier
                .safeDrawingPadding()
                .windowInsetsPadding(WindowInsets.statusBars)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(it)
                .padding(horizontal = MaterialTheme.spacing.medium)
        ) {
            Spacer(Modifier.size(MaterialTheme.spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID Card",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(MaterialTheme.icon.small)
                        )
                    }
                    Spacer(Modifier.size(MaterialTheme.spacing.small))
                    FloatingActionButton(
                        onClick = {
                                  onEvent(HistoryEvent.ShowDialog)
                        },
                        modifier = Modifier
                            .size(MaterialTheme.icon.large)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_qr_scanner),
                            contentDescription = "QR Scanner",
                            Modifier.size(MaterialTheme.icon.medium)
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.size(MaterialTheme.spacing.medium))
            UserCard(tempPass = tempPass, scaffoldState = bottomSheetScaffoldState, scope = scope)
            Spacer(modifier = Modifier.size(MaterialTheme.spacing.medium))
            Histories(state)
        }
    }
}

@Composable
fun UserEntry(profile: ProfileEntry) {
    Row() {
        Icon(
            painterResource(id = profile.id),
            contentDescription = null,
            modifier = Modifier
                .size(MaterialTheme.icon.medium)
                .align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
        Column(
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                text = stringResource(id = profile.title),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = profile.data,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UserProfile(tempPass: TempPass) {
    val profileEntries = ArrayList<ProfileEntry>()
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_docnum,
            id = R.drawable.numbers_fill1_wght400_grad0_opsz48,
            data = tempPass.docNum,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_name,
            id = R.drawable.badge_fill1_wght400_grad0_opsz48,
            data = tempPass.name,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_birthdate,
            id = R.drawable.cake_fill1_wght400_grad0_opsz48,
            data = tempPass.birthDate,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_gender,
            id = R.drawable.wc_fill1_wght400_grad0_opsz48,
            data = tempPass.sex,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_nationality,
            id = R.drawable.language_fill1_wght400_grad0_opsz48,
            data = tempPass.nationality,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_issuing_authority,
            id = R.drawable.assured_workload_fill1_wght400_grad0_opsz48,
            data = tempPass.issuer,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_issue_date,
            id = R.drawable.event_available_fill1_wght400_grad0_opsz48,
            data = tempPass.issueDate,
        )
    )
    profileEntries.add(
        ProfileEntry(
            title = R.string.user_expiry_date,
            id = R.drawable.event_busy_fill1_wght400_grad0_opsz48,
            data = tempPass.expiryDate,
        )
    )


    Box(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.medium)
            .fillMaxWidth()
    ) {
        Column() {
            Box (
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Profile Details",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(modifier = Modifier.size(MaterialTheme.spacing.medium))
            Surface {
                Column (
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.medium)
                ){
                    profileEntries.forEach {
                        Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                        UserEntry(profile = it)
                        Spacer(modifier = Modifier.size(MaterialTheme.spacing.small))
                        Divider()
                    }
                }

            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(tempPass: TempPass, scaffoldState: BottomSheetScaffoldState, scope: CoroutineScope) {
    Surface(
        onClick = {
            scope.launch {
                if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded)
                    scaffoldState.bottomSheetState.expand()
                else
                    scaffoldState.bottomSheetState.partialExpand()
            }
        },
        shadowElevation = 5.dp,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .fillMaxWidth(1f)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                Text(
                    text = tempPass.docNum,
                    style = MaterialTheme.typography.labelMedium
                    )
                Text(
                    text = stringResource(R.string.card_pass),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "user image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(8.dp)))
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column() {
                    Text(
                        text = tempPass.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Column {
                        Text(
                            text = tempPass.sex,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = tempPass.birthDate,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.card_issue),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = tempPass.issueDate,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                Column {
                    Text(
                        text = stringResource(id = R.string.card_expiry),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = tempPass.expiryDate,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun History(history: History) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Column {
                Text(
                    text = history.site,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val year = history.date.year.toString().padStart(2, '0')
                val month = history.date.monthValue.toString().padStart(2, '0')
                val day = history.date.dayOfMonth.toString().padStart(2, '0')
                val hour = history.date.hour.toString().padStart(2, '0')
                val minute = history.date.minute.toString().padStart(2, '0')
                val second = history.date.second.toString().padStart(2, '0')

                Text(
                    text = "$year/$month/$day - $hour:$minute:$second",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Icon(
                imageVector = if (history.isAllowed) Icons.Filled.Check else Icons.Filled.Clear,
                contentDescription = null
            )
        }
    }
}

@Composable
fun Histories(state: HistoryState) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = "Recent History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.size(10.dp))
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(state.histories) {
                    History(history = it)
                    Divider(thickness = Dp.Hairline)
                }
            }

        }
    }

}

@Composable
fun DefaultHistory() {
    val state = HistoryState()
    Histories(state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultCard() {
    val defaultPass = TempPass(
        docType = "Passport",
        issuer = "ABC Country",
        name = "John Smith",
        docNum = "A1234567",
        nationality = "Country A",
        birthDate = "1990-01-01",
        sex = "Male",
        issueDate = "2022-01-01",
        expiryDate = "2025-01-01"
    )

    val defaultScope = rememberCoroutineScope()
    val defaultSheetState = rememberBottomSheetScaffoldState()
    UserCard(defaultPass, defaultSheetState, defaultScope)
}


@Preview(showBackground = true)
@Composable
fun DefaultProfile() {
    val defaultPass = TempPass(
        docType = "Passport",
        issuer = "ABC Country",
        name = "John Smith",
        docNum = "A1234567",
        nationality = "Country A",
        birthDate = "1990-01-01",
        sex = "Male",
        issueDate = "2022-01-01",
        expiryDate = "2025-01-01"
    )

    UserProfile(defaultPass)
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showSystemUi = true)
//@Composable
//fun DefaultApp() {
//    val defaultPass = Passport(
//        docType = "Passport",
//        issuer = "ABC Country",
//        name = "John Smith",
//        docNum = "A1234567",
//        nationality = "Country A",
//        birthDate = "1990-01-01",
//        sex = "Male",
//        issueDate = "2022-01-01",
//        expiryDate = "2025-01-01"
//    )
//
//    HomeScreen(passport = defaultPass, onEvent = )
//}