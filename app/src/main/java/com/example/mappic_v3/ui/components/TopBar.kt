package com.example.mappic_v3.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import com.example.mappic_v3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onSelectList: () -> Unit,
    onSelectCreate: () -> Unit,
    onSelectProfile: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mappic),
                    contentDescription = "Mappic logo",
                    modifier = Modifier
                        .size(70.dp)
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Mappic",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            "Álbumes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.PhotoAlbum,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onSelectList()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            "Crear álbum",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onSelectCreate()
                    }
                )

                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            "Perfil",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onSelectProfile()
                    }
                )
            }

        }
    )
}
