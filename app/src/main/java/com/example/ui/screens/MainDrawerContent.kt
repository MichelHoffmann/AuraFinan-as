package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PurpleAccentGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSecondary
import com.example.ui.theme.PurpleTertiary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavDestination {
    HOME,
    REPORTS,
    GOALS,
    WALLETS,
    REMINDERS
}

@Composable
fun MainDrawerContent(
    currentDestination: NavDestination,
    userName: String,
    userEmail: String,
    isBiometricEnabled: Boolean,
    onSelectDestination: (NavDestination) -> Unit,
    onToggleBiometric: (Boolean) -> Unit,
    onLockApp: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = SurfaceDark,
        drawerContentColor = TextPrimary,
        modifier = Modifier.width(310.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Drawer Header with User Photo and Brand Glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(PurpleTertiary.copy(alpha = 0.6f), SurfaceDark)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .border(2.5.dp, PurplePrimary, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.user_avatar),
                                    contentDescription = "Foto do Usuário",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = userName,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = userEmail,
                                    color = PurpleSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(IncomeGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = IncomeGreen,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Protegido",
                                        color = IncomeGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Items
                NavigationDrawerItem(
                    label = { Text("Tela Inicial") },
                    selected = currentDestination == NavDestination.HOME,
                    onClick = { onSelectDestination(NavDestination.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_nav_home"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PurplePrimary.copy(alpha = 0.2f),
                        selectedTextColor = PurpleSecondary,
                        selectedIconColor = PurplePrimary,
                        unselectedTextColor = TextPrimary,
                        unselectedIconColor = TextMuted
                    )
                )

                NavigationDrawerItem(
                    label = { Text("Relatórios & Gráficos") },
                    selected = currentDestination == NavDestination.REPORTS,
                    onClick = { onSelectDestination(NavDestination.REPORTS) },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = null) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_nav_reports"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PurplePrimary.copy(alpha = 0.2f),
                        selectedTextColor = PurpleSecondary,
                        selectedIconColor = PurplePrimary,
                        unselectedTextColor = TextPrimary,
                        unselectedIconColor = TextMuted
                    )
                )

                NavigationDrawerItem(
                    label = { Text("Metas Mensais") },
                    selected = currentDestination == NavDestination.GOALS,
                    onClick = { onSelectDestination(NavDestination.GOALS) },
                    icon = { Icon(Icons.Default.TrackChanges, contentDescription = null) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_nav_goals"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PurplePrimary.copy(alpha = 0.2f),
                        selectedTextColor = PurpleSecondary,
                        selectedIconColor = PurplePrimary,
                        unselectedTextColor = TextPrimary,
                        unselectedIconColor = TextMuted
                    )
                )

                NavigationDrawerItem(
                    label = { Text("Minhas Carteiras") },
                    selected = currentDestination == NavDestination.WALLETS,
                    onClick = { onSelectDestination(NavDestination.WALLETS) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_nav_wallets"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PurplePrimary.copy(alpha = 0.2f),
                        selectedTextColor = PurpleSecondary,
                        selectedIconColor = PurplePrimary,
                        unselectedTextColor = TextPrimary,
                        unselectedIconColor = TextMuted
                    )
                )

                NavigationDrawerItem(
                    label = { Text("Lembretes Recorrentes") },
                    selected = currentDestination == NavDestination.REMINDERS,
                    onClick = { onSelectDestination(NavDestination.REMINDERS) },
                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .testTag("drawer_nav_reminders"),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = PurplePrimary.copy(alpha = 0.2f),
                        selectedTextColor = PurpleSecondary,
                        selectedIconColor = PurplePrimary,
                        unselectedTextColor = TextPrimary,
                        unselectedIconColor = TextMuted
                    )
                )
            }

            // Bottom Section: Biometric Settings & Lock Button
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HorizontalDivider(color = Color(0x22A855F7), modifier = Modifier.padding(bottom = 14.dp))

                // Biometric Toggle Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDarkElevated)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Biometria",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isBiometricEnabled) "Bloqueio ativo" else "Desativado",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = onToggleBiometric,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PurplePrimary,
                            checkedTrackColor = PurplePrimary.copy(alpha = 0.4f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Lock App Button
                NavigationDrawerItem(
                    label = { Text("Bloquear Agora") },
                    selected = false,
                    onClick = onLockApp,
                    icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PurpleSecondary) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = TextPrimary,
                        unselectedIconColor = PurpleSecondary
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
