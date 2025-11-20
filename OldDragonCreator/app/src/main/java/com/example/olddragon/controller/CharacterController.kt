package com.example.olddragon.controller

import androidx.compose.runtime.*
import com.example.olddragon.model.*

class CharacterController {
    // Estado da UI
    var name by mutableStateOf("")
    var selectedRace by mutableStateOf<Race>(Human())
    var selectedClass by mutableStateOf<CharClass>(Fighter())
    var selectedMode by mutableStateOf(GenMode.CLASSICO)

    // Personagem gerado
    var generatedCharacter by mutableStateOf<Character?>(null)

    // Listas para Dropdowns
    val races = listOf(Human(), Dwarf(), Elf())
    val classes = listOf(Fighter(), MagicUser(), Thief())
    val modes = GenMode.values().toList()

    fun createCharacter() {
        // 1. Define o gerador
        val generator: AttributeGenerator = when(selectedMode) {
            GenMode.CLASSICO -> ClassicGenerator()
            GenMode.AVENTUREIRO -> PriorityGenerator(selectedClass.getPriorities(), false)
            GenMode.HEROICO -> PriorityGenerator(selectedClass.getPriorities(), true)
        }

        // 2. Gera Atributos
        val attrs = generator.generate()
        val conMod = getModifier(attrs.con)
        val dexMod = getModifier(attrs.dex)

        // 3. Calcula Derivados
        // PV: DV + CON (Pág 48)
        val hp = maxOf(1, selectedClass.hd + conMod)
        // CA: 10 + DES (Sem armadura)
        val ac = 10 + dexMod
        // Ataque Base: Nível 1 = +1 (Simplificado)
        val ba = 1
        // Dado de dano: Guerreiro d8, Outros d4
        val dmgDie = if (selectedClass is Fighter) 8 else 4

        // 4. Cria Objeto
        generatedCharacter = Character(
            name = if (name.isBlank()) "Aventureiro" else name,
            raceName = selectedRace.name,
            className = selectedClass.name,
            attributes = attrs,
            maxHp = hp,
            ac = ac,
            attackBonus = ba,
            damageDie = dmgDie
        )
    }
}