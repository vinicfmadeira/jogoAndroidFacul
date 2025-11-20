package com.example.olddragon.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.olddragon.model.Character
import com.example.olddragon.model.Dice
import com.example.olddragon.model.Monster
import kotlinx.coroutines.*

class BattleService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    companion object {
        const val CHANNEL_ID = "OD2_Battle"
        const val EXTRA_CHAR = "character_data"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val character = intent?.getParcelableExtra<Character>(EXTRA_CHAR)

        if (character != null) {
            // Cria um Monstro Aleatório (Ex: Orc - Pág 177)
            // CA 14, PV 5 (1d8), Atq +1, Dano 1d6
            val orc = Monster("Orc", 14, 5, 1, 6)
            startBattle(character, orc)
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startBattle(player: Character, monster: Monster) {
        // Notificação inicial para manter o serviço vivo
        startForeground(1, buildNotification("Batalha Iniciada", "${player.name} encontrou um ${monster.name}!"))

        serviceScope.launch {
            while (player.isAlive && monster.hp > 0) {
                delay(2000) // Simula o tempo do round (2 segundos)

                // Regra de Combate (Pág 81 - Iniciativa)
                val pInit = Dice.d20() + player.dexMod
                val mInit = Dice.d20()

                // Quem tirou maior age primeiro
                if (pInit >= mInit) {
                    performAttack(player, monster, isPlayer = true)
                    if (monster.hp > 0) performAttack(player, monster, isPlayer = false)
                } else {
                    performAttack(player, monster, isPlayer = false)
                    if (player.isAlive) performAttack(player, monster, isPlayer = true)
                }

                // Atualiza notificação com status (sem spammar som)
                updateNotificationStatus(player, monster)
            }

            // Fim da batalha
            if (!player.isAlive) {
                sendDeathNotification(player.name)
            } else {
                updateNotification("Vitória!", "${player.name} derrotou o ${monster.name}!")
                stopForeground(STOP_FOREGROUND_DETACH)
            }
            stopSelf()
        }
    }

    private fun performAttack(player: Character, monster: Monster, isPlayer: Boolean) {
        val d20 = Dice.d20()

        if (isPlayer) {
            // Ataque: d20 + BA + FOR vs CA (Pág 83)
            val totalAtk = d20 + player.attackBonus + player.strMod
            if (totalAtk >= monster.ac) {
                // Dano: Arma + FOR
                val dmg = maxOf(1, Dice.roll(1) + player.strMod) // Simplificado para d6
                monster.hp -= dmg
            }
        } else {
            // Monstro Ataca
            val totalAtk = d20 + monster.attackBonus
            if (totalAtk >= player.ac) {
                val dmg = (1..monster.damageDie).random()
                player.currentHp -= dmg
            }
        }
    }

    // --- NOTIFICAÇÕES ---

    private fun sendDeathNotification(charName: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("VOCÊ MORREU!")
            .setContentText("O personagem $charName caiu em combate glorioso.")
            .setSmallIcon(android.R.drawable.ic_delete)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        nm.notify(2, notif)
    }

    private fun updateNotification(title: String, text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(1, buildNotification(title, text))
    }

    private fun updateNotificationStatus(p: Character, m: Monster) {
        updateNotification("Combate...", "${p.name} (${p.currentHp} PV) vs ${m.name} (${m.hp} PV)")
    }

    private fun buildNotification(title: String, text: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Simulador OD2", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}