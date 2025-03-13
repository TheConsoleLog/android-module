package at.htlwels.ires.view.main.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import at.htlwels.ires.R
import at.htlwels.ires.control.ProfileViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.dto.profile.AccountDeletionDialog
import at.htlwels.ires.view.HorizontalSpacer
import at.htlwels.ires.view.Routes
import at.htlwels.ires.view.VerticalSpacer
import at.htlwels.ires.view.auth.ErrorText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    updateTopBar: (@Composable () -> Unit) -> Unit,
    logout: () -> Unit,
    navTo: (Routes) -> Unit
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()

    var showDeletionDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        updateTopBar{
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(
                        onClick = {
                            drawerScope.launch {
                                if(drawerState.isOpen) drawerState.close()
                                else drawerState.open()
                            }
                        }
                    ) {
                        Icon(Icons.Default.Settings, null)
                    }
                }
            )
        }
    }

    Box {
        ExtendedFloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).zIndex(1F),
            onClick = { navTo(Routes.Main.ProfileScreen.BuyPremiumScreen) }
        ) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null)
                HorizontalSpacer(4)
                Text("Purchase Premium")
            }
        }

        //Switch layout direction so drawer appears on the right of the screen
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.6f)) {

                        //Switch back to normal layout direction
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Box(modifier = Modifier.align(Alignment.End)) {
                                Text(
                                    text = "Einstellungen",
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            NavigationDrawerItem(
                                label = { Text("Account löschen") },
                                selected = false,
                                onClick = { showDeletionDialog = true },
                                icon = { Icon(Icons.Default.Delete, null) }
                            )
                            NavigationDrawerItem(
                                label = { Text("Ausloggen") },
                                selected = false,
                                onClick = {
                                    drawerScope.launch { drawerState.close() }
                                    logout()
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.baseline_logout_24),
                                        null
                                    )
                                }
                            )
                        }
                    }
                }
            ) {
                //----------------------------------------------------------------
                //------------------------ CAREFUL HERE --------------------------

                // Since the ModalNavigationDrawer's layout direction is Rtl, its direct child Composable will always be aligned to the right.
                // Therefore, if you use a composable that doesn't fill max width as a direct child of the second, "correcting" (=Ltr),
                // CompositionLocalProvider inside the drawer, although the child will be aligned to the left INSIDE the CLP, IT WILL STILL SHOW UP
                // ON THE RIGHT SIDE OF THE SCREEN, because the CLP is aligned to the Right and doesn't fill Max Size either. (CLP Size = Child Size)

                // You can resolve this problem as follows:
                // The correcting Ltr provider should have only one direct child (e.g. Box) which fills the max Width.
                // The provider itself will still be aligned to the right but that doesn't matter because now it fills max Width anyways, and its children
                // will again be correctly aligned to the left.

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        when (val profileState = viewModel.profileState.value) {
                            is Resource.Loading -> {
                                CircularProgressIndicator()
                            }

                            is Resource.Error -> {
                                ErrorText(profileState.getMessage())
                            }

                            is Resource.Ready -> {}
                            is Resource.Success -> {

                                val profile = profileState.data

                                val lazyListState = rememberLazyListState()
                                val lazyListScope = rememberCoroutineScope()
                                val firstVisibleItem =
                                    remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

                                if (firstVisibleItem.value >= 2) {
                                    FloatingActionButton(
                                        onClick = {
                                            lazyListScope.launch {
                                                lazyListState.animateScrollToItem(0)
                                            }
                                        },
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .zIndex(1f)
                                            .padding(16.dp)    //zIndex for stacking inside Box
                                    ) {
                                        Icon(Icons.Default.KeyboardArrowUp, null)
                                    }
                                }

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .zIndex(0f),
                                    state = lazyListState
                                ) {
                                    this.item {
                                        Column(modifier = Modifier.fillMaxWidth()) {

                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "@${profile.userName}",
                                                    style = MaterialTheme.typography.headlineMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            VerticalSpacer(16)

                                            Card(modifier = Modifier.fillMaxWidth()) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 12.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(profile.email)
                                                    }
                                                    HorizontalDivider()
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 12.dp)
                                                    ) {
                                                        Text("First Name")
                                                        Text(profile.firstName)
                                                    }
                                                    HorizontalDivider()
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 12.dp)
                                                    ) {
                                                        Text("Last Name")
                                                        Text(profile.lastName)
                                                    }
                                                    HorizontalDivider()

                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text("Active Tour")
                                                        TextButton(
                                                            onClick = { navTo(Routes.Main.TourScreen) }
                                                        ) {
                                                            Text(
                                                                profile.activeOrNextTour?.name
                                                                    ?: "No active tour"
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            VerticalSpacer(16)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    if(showDeletionDialog) {
        AccountDeletionDialog(
            closeDeletionDialog = { showDeletionDialog = false },
            logout = logout
        )
    }
}
