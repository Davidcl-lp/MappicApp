package com.example.mappic_v3.ui.components

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mappic_v3.ui.SortField
import com.example.mappic_v3.ui.SortOrder
@Composable
fun SortBar(
    onSort: (SortField, SortOrder) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            "Ordenar por:",
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Spacer(Modifier.width(6.dp))

        TextButton(
            onClick = { expanded = true },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text("Seleccionar ⌄")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Título A–Z") },
                onClick = { expanded = false; onSort(SortField.TITLE, SortOrder.ASC) }
            )
            DropdownMenuItem(
                text = { Text("Título Z–A") },
                onClick = { expanded = false; onSort(SortField.TITLE, SortOrder.DESC) }
            )
            DropdownMenuItem(
                text = { Text("Más reciente") },
                onClick = { expanded = false; onSort(SortField.DATE, SortOrder.DESC) }
            )
            DropdownMenuItem(
                text = { Text("Más antiguo") },
                onClick = { expanded = false; onSort(SortField.DATE, SortOrder.ASC) }
            )
        }
    }
}
