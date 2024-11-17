package controllers

import model.Campo
import model.Jogador
import model.cartas.Carta
import model.cartas.CartaEquip
import model.cartas.CartaMonstro
import kotlin.random.Random

object CartaController {

    fun addCartaTurno(jogador1: Jogador, jogador2: Jogador, cardList: MutableList<Carta>){
        var index = Random.nextInt(cardList.size)
        jogador1.cartas.add(cardList[index])
        cardList.removeAt(index)
        index = Random.nextInt(cardList.size)
        jogador2.cartas.add(cardList[index])
        cardList.removeAt(index)
    }

    fun descartarCarta(jogador: Jogador) {
        println("Escolha um carta para descartar")
        jogador.printCartas()

        val indexCarta = readlnOrNull()?.toIntOrNull()

        if (indexCarta == null || indexCarta !in jogador.cartas.indices) {
            println("Erro! Índice inválido, tente novamente.")
            return
        }
        val cartaSelecionada = jogador.cartas[indexCarta]
        jogador.cartas.remove(cartaSelecionada)

        println("Carta ${cartaSelecionada.name} descartada!")
    }

    fun invocarCarta(jogador: Jogador, tabuleiro: Campo) {
        val ladoTabuleiro = tabuleiro.getLadoTabuleiro(Turno.vez)

        if (ladoTabuleiro.none { it == null }) {
            println("O tabuleiro já está cheio!")
            return
        }

        jogador.printCartas()
        println("Escolha o índice da carta para invocar:")
        val monstroIndex = readlnOrNull()?.toIntOrNull()

        if (monstroIndex == null || monstroIndex !in jogador.cartas.indices) {
            println("Erro! Valor inválido, tente novamente.")
            return
        }

        val cartaSelecionada = jogador.cartas[monstroIndex]

        if (cartaSelecionada is CartaMonstro) {
            println("Carta em estado de ataque ou defesa? (A/D)")
            val estado: String = readlnOrNull().orEmpty().uppercase()

            if (estado == "A" || estado == "D") {
                cartaSelecionada.setPosicao(estado)

                // Adiciona a carta no primeiro slot vazio
                val indexVazio = ladoTabuleiro.indexOfFirst { it == null }
                if (indexVazio != -1) {
                    ladoTabuleiro[indexVazio] = cartaSelecionada
                    jogador.cartas.removeAt(monstroIndex)
                    println("Carta ${cartaSelecionada.name} invocada com sucesso!")
                } else {
                    println("Erro ao posicionar a carta no tabuleiro.")
                }
            } else {
                println("Estado inválido! Escolha 'A' para ataque ou 'D' para defesa.")
            }
        } else {
            println("Erro! Somente monstros podem ser colocados em campo.")
        }
    }

    fun Campo.getLadoTabuleiro(vez: Int): MutableList<CartaMonstro?> {
        return if (vez == 1) ladoAzul else ladoVermelho
    }

    fun equiparCarta(jogador: Jogador, campo: Campo) {
        println("Escolha uma carta do tabuleiro para equipar: ")
        val ladoCampo = campo.getLadoTabuleiro(Turno.vez)
        campo.printCartasCampo(Turno.vez)

        val indexCartaMonstro = readlnOrNull()?.toIntOrNull()
        val cartaMonstroSelec = indexCartaMonstro?.let {
            if (it in ladoCampo.indices) ladoCampo[it] else null
        }

        if (cartaMonstroSelec is CartaMonstro) {
            println("Escolha uma carta de equipamento da sua mão para equipar: ")
            jogador.printCartas()

            val indexCartaEquip = readlnOrNull()?.toIntOrNull()
            val cartaEquipSelec = indexCartaEquip?.let {
                if (it in jogador.cartas.indices) jogador.cartas[it] else null
            }

            if (cartaEquipSelec is CartaEquip) {
                cartaMonstroSelec.equipar(cartaEquipSelec)
                jogador.cartas.remove(cartaEquipSelec)
                println("A carta ${cartaEquipSelec.name} foi equipada com sucesso em ${cartaMonstroSelec.name}!")
            } else {
                println("Erro! Escolha uma carta do tipo equipamento.")
            }
        } else {
            println("Erro! Escolha uma carta do tipo monstro.")
        }
    }
}