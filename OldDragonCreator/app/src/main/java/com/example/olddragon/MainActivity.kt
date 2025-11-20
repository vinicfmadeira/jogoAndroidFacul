package com.example.olddragon

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.olddragon.controller.CharacterController
import com.example.olddragon.model.Character
import com.example.olddragon.service.BattleService
import com.example.olddragon.view.CharacterScreen

class MainActivity : ComponentActivity() {

    private val controller = CharacterController()

    // Permissão para notificações (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pede permissão se for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            // Renderiza a tela passando o Controller e o Callback de Batalha
            CharacterScreen(
                controller = controller,
                onStartBattle = { character -> startBattleService(character) }
            )
        }
    }

    private fun startBattleService(character: Character) {
        val intent = Intent(this, BattleService::class.java).apply {
            putExtra(BattleService.EXTRA_CHAR, character)
        }
        // Inicia o Serviço
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}