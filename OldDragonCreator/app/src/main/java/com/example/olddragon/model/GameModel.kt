package com.example.olddragon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

// --- UTILITÁRIOS DE DADOS ---
object Dice {
    fun d6() = Random.nextInt(1, 7)
    fun d20() = Random.nextInt(1, 21)
    // Rola N dados
    fun roll(n: Int) = (1..n).sumOf { d6() }
    // Heroico: 4d6, descarta o menor (Pág 14)
    fun rollHeroic() = List(4) { d6() }.sortedDescending().take(3).sum()
}

// Tabela 1.1: Modificadores (Pág 15)
fun getModifier(score: Int): Int = when (score) {
    in 0..3 -> -3; in 4..5 -> -2; in 6..8 -> -1; in 9..12 -> 0
    in 13..14 -> +1; in 15..16 -> +2; in 17..18 -> +3; else -> +4
}

// --- ESTRUTURAS DE DADOS ---
enum class AttributeName { FORCA, DESTREZA, CONSTITUICAO, INTELIGENCIA, SABEDORIA, CARISMA }
enum class GenMode { CLASSICO, HEROICO, AVENTUREIRO }

@Parcelize
data class Attributes(
    val str: Int, val dex: Int, val con: Int,
    val int: Int, val wis: Int, val cha: Int
) : Parcelable

// --- PERSONAGEM (Transportável via Parcelable) ---
@Parcelize
data class Character(
    val name: String,
    val raceName: String,
    val className: String,
    val attributes: Attributes,
    val maxHp: Int,
    val ac: Int,
    val attackBonus: Int,
    val damageDie: Int
) : Parcelable {
    var currentHp: Int = maxHp
    val isAlive: Boolean get() = currentHp > 0

    // Getters rápidos para modificadores de combate
    val strMod get() = getModifier(attributes.str)
    val dexMod get() = getModifier(attributes.dex)
}

// Monstro Simples para Batalha (Apêndice II)
data class Monster(
    val name: String,
    val ac: Int,
    var hp: Int,
    val attackBonus: Int,
    val damageDie: Int
)

// --- RAÇAS (Capítulo II) ---
abstract class Race(val name: String, val move: Int)
class Human : Race("Humano", 9)
class Dwarf : Race("Anão", 6)
class Elf : Race("Elfo", 9)

// --- CLASSES (Capítulo III) ---
abstract class CharClass(val name: String, val hd: Int) {
    abstract fun getPriorities(): List<AttributeName>
}
class Fighter : CharClass("Guerreiro", 10) {
    override fun getPriorities() = listOf(AttributeName.FORCA, AttributeName.CONSTITUICAO)
}
class MagicUser : CharClass("Mago", 4) {
    override fun getPriorities() = listOf(AttributeName.INTELIGENCIA, AttributeName.DESTREZA)
}
class Thief : CharClass("Ladrão", 6) {
    override fun getPriorities() = listOf(AttributeName.DESTREZA, AttributeName.INTELIGENCIA)
}

// --- GERADORES DE ATRIBUTOS (Strategy Pattern) ---
interface AttributeGenerator {
    fun generate(): Attributes
}

class ClassicGenerator : AttributeGenerator {
    override fun generate() = Attributes(
        Dice.roll(3), Dice.roll(3), Dice.roll(3),
        Dice.roll(3), Dice.roll(3), Dice.roll(3)
    )
}

class PriorityGenerator(private val priority: List<AttributeName>, private val isHeroic: Boolean) : AttributeGenerator {
    override fun generate(): Attributes {
        // Gera 6 rolagens
        val rolls = MutableList(6) { if (isHeroic) Dice.rollHeroic() else Dice.roll(3) }
            .sortedDescending().toMutableList()

        val map = mutableMapOf<AttributeName, Int>()
        // Distribui os melhores valores nas prioridades
        priority.forEach { if (rolls.isNotEmpty()) map[it] = rolls.removeAt(0) }
        // O resto é aleatório
        AttributeName.values().forEach { if (!map.containsKey(it)) map[it] = rolls.removeAt(0) }

        return Attributes(
            map[AttributeName.FORCA]!!, map[AttributeName.DESTREZA]!!, map[AttributeName.CONSTITUICAO]!!,
            map[AttributeName.INTELIGENCIA]!!, map[AttributeName.SABEDORIA]!!, map[AttributeName.CARISMA]!!
        )
    }
}