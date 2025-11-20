package com.example.olddragon.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.olddragon.controller.CharacterController
import com.example.olddragon.model.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterScreen(
    controller: CharacterController,
    onStartBattle: (Character) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criador Old Dragon 2") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Inputs ---
            OutlinedTextField(
                value = controller.name,
                onValueChange = { controller.name = it },
                label = { Text("Nome do Personagem") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Raça", fontWeight = FontWeight.Bold)
            Row {
                controller.races.forEach { race ->
                    FilterChip(
                        selected = controller.selectedRace == race,
                        onClick = { controller.selectedRace = race },
                        label = { Text(race.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Text("Classe", fontWeight = FontWeight.Bold)
            Row {
                controller.classes.forEach { charClass ->
                    FilterChip(
                        selected = controller.selectedClass == charClass,
                        onClick = { controller.selectedClass = charClass },
                        label = { Text(charClass.name) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Text("Modo de Geração", fontWeight = FontWeight.Bold)
            controller.modes.forEach { mode ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (controller.selectedMode == mode),
                            onClick = { controller.selectedMode = mode }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (controller.selectedMode == mode),
                        onClick = null
                    )
                    Text(text = mode.name)
                }
            }

            Button(
                onClick = { controller.createCharacter() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("ROLAR DADOS") }

            Divider()

            // --- Ficha Gerada ---
            if (controller.generatedCharacter != null) {
                val char = controller.generatedCharacter!!
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(char.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("${char.raceName} ${char.className} (Nível 1)")
                        Spacer(Modifier.height(8.dp))
                        Text("PV: ${char.maxHp} | CA: ${char.ac} | Atq: +${char.attackBonus}")
                        Divider(Modifier.padding(vertical = 8.dp))
                        Text("FOR: ${char.attributes.str} | DES: ${char.attributes.dex} | CON: ${char.attributes.con}")
                        Text("INT: ${char.attributes.int} | SAB: ${char.attributes.wis} | CAR: ${char.attributes.cha}")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onStartBattle(char) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("INICIAR BATALHA (BACKGROUND)")
                }
            }
        }
    }
}