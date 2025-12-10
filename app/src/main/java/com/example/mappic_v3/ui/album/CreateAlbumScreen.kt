package com.example.mappic_v3.ui.album

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CreateAlbumScreen(
    viewModel: AlbumViewModel = AlbumViewModel(),
    modifier: Modifier = Modifier
){

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier.padding(20.dp)) {

        Text("Crear álbum", fontSize = 22.sp)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.createAlbum(
                    title = title,
                    description = description,
                    location = "Madrid",
                    lat = "40.41",
                    lon = "-3.70",
                    isGlobal = false
                )
            }
        ) {
            Text("Guardar álbum")
        }
    }
}
