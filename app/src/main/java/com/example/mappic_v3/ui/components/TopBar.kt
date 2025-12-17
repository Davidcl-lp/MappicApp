package com.example.mappic_v3.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onSelectList: () -> Unit,
    onSelectCreate: () -> Unit,
    onSelectProfile: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("") },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Listar álbumes") },
                    onClick = {
                        menuExpanded = false
                        onSelectList()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Crear álbum") },
                    onClick = {
                        menuExpanded = false
                        onSelectCreate()
                    }
                )
            }
        }
    )
}
