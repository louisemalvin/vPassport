@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.vpassport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vpassport.ui.theme.VPassportTheme
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VPassportTheme {
                // A surface container using the 'background' color from the theme
            }
        }
    }
}

@Composable
fun UserEntry(userData: UserData) {
    Row() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = userData.icon,
                contentDescription = "Icon",
                modifier = Modifier.size(30.dp)
            )
        }
        Column(
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Text(
                fontSize = 8.sp,
                text = userData.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.inverseSurface
            )
            Text(
                fontSize = 20.sp,
                text = userData.data,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UserProfile(userProfile: List<UserData>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        LazyColumn() {
            items(userProfile) { userData ->
                UserEntry(userData = userData)
            }
        }
    }
}

@Composable
fun UserCard(passport: Passport) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer,
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
                Text(text = passport.docNum)
                Text(text = "DE - VPASS")
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "user image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(corner = CornerSize(8.dp)))
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column() {
                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        ) {
                            append("John Smith \n")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Light,
                                fontSize = 15.sp
                            )
                        ) {
                            append("Birth Date\n")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        ) {
                            append("01 January 1900\n")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        ) {
                            append("Female")
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                Text(text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    ) {
                        append("Created\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    ) {
                        append("01/01/2020")
                    }
                })
                Text(text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    ) {
                        append("Valid Until\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    ) {
                        append("01/01/2025")
                    }
                })
            }
        }
    }
}

@Composable
fun History(history: History) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
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
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "2000-10-20",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
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
fun Histories(histories: List<History>) {
    Box(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = "Recent History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            LazyColumn() {
                items(histories) {
                    History(history = it)
                }
            }
        }
    }

}

@Composable
fun DefaultHistory() {
    val histories = ArrayList<History>()
    histories.add(History("google.com", Date(2000, 1, 1, 10, 10), true))
    histories.add(History("wikipedia.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("test.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("asd.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("erqer.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("fasvf.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("wikipedia.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("test.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("asd.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("erqer.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("fasvf.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("ewrawerewa.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("awfevawe.com", Date(2000, 1, 1, 10, 10), false))
    histories.add(History("wikifawvewpedia.com", Date(2000, 1, 1, 10, 10), false))
    Histories(histories)
}

@Composable
fun DefaultData() {
    UserEntry(
        UserData(
            title = "Test",
            data = "Hello World",
            icon = Icons.Rounded.ShoppingCart
        )
    )
}

@Composable
fun DefaultCard() {
    val defaultPass = Passport(
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

    val defaultProfile = ArrayList<UserData>()
    defaultProfile.add(
        UserData(
            title = "Document Type",
            data = defaultPass.docType,
            icon = Icons.Rounded.ShoppingCart
        )
    )

    defaultProfile.add(
        UserData(
            title = "Issuer",
            data = defaultPass.issuer,
            icon = Icons.Rounded.Share
        )
    )

    defaultProfile.add(
        UserData(
            title = "Name",
            data = defaultPass.name,
            icon = Icons.Rounded.Person
        )
    )

    defaultProfile.add(
        UserData(
            title = "Document Number",
            data = defaultPass.docNum,
            icon = Icons.Rounded.FavoriteBorder
        )
    )

    defaultProfile.add(
        UserData(
            title = "Nationality",
            data = defaultPass.nationality,
            icon = Icons.Rounded.AccountCircle
        )
    )

    defaultProfile.add(
        UserData(
            title = "Birth Date",
            data = defaultPass.birthDate,
            icon = Icons.Rounded.Call
        )
    )

    defaultProfile.add(
        UserData(
            title = "Sex",
            data = defaultPass.sex,
            icon = Icons.Rounded.Person
        )
    )

    defaultProfile.add(
        UserData(
            title = "Expiry Date",
            data = defaultPass.expiryDate,
            icon = Icons.Rounded.MoreVert
        )
    )

    UserCard(defaultPass)
}


@Composable
fun DefaultProfile() {
    val defaultPass = Passport(
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

    val defaultProfile = ArrayList<UserData>()
    defaultProfile.add(
        UserData(
            title = "Document Type",
            data = defaultPass.docType,
            icon = Icons.Rounded.ShoppingCart
        )
    )

    defaultProfile.add(
        UserData(
            title = "Issuer",
            data = defaultPass.issuer,
            icon = Icons.Rounded.Share
        )
    )

    defaultProfile.add(
        UserData(
            title = "Name",
            data = defaultPass.name,
            icon = Icons.Rounded.Person
        )
    )

    defaultProfile.add(
        UserData(
            title = "Document Number",
            data = defaultPass.docNum,
            icon = Icons.Rounded.FavoriteBorder
        )
    )

    defaultProfile.add(
        UserData(
            title = "Nationality",
            data = defaultPass.nationality,
            icon = Icons.Rounded.AccountCircle
        )
    )

    defaultProfile.add(
        UserData(
            title = "Birth Date",
            data = defaultPass.birthDate,
            icon = Icons.Rounded.Call
        )
    )

    defaultProfile.add(
        UserData(
            title = "Sex",
            data = defaultPass.sex,
            icon = Icons.Rounded.Person
        )
    )

    defaultProfile.add(
        UserData(
            title = "Expiry Date",
            data = defaultPass.expiryDate,
            icon = Icons.Rounded.MoreVert
        )
    )

    UserProfile(defaultProfile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
fun DefaultApp() {
    BottomSheetScaffold(
        sheetContent = {
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                DefaultProfile()
            }

        },
        sheetContainerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(it)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
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
                        Icon(Icons.Filled.Settings, contentDescription = null, Modifier.size(30.dp))
                    }
                    Spacer(Modifier.size(10.dp))
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            Icons.Filled.ShoppingCart,
                            contentDescription = null,
                            Modifier.size(30.dp)
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.size(20.dp))
            DefaultCard()
            Spacer(modifier = Modifier.size(20.dp))
            DefaultHistory()
        }
    }

}