package model.cartas

import Turno
import model.Campo
import model.Jogador

class CartaMonstro(name: String, description: String, attack: Int, defense: Int,  type: String):
    Carta(name, description, attack, defense, type){

        var equipamentos = mutableListOf<CartaEquip>()
        var attackMode: Boolean = false
        var defenseMode: Boolean = false
        var ataqueTurno: Boolean = false
        var vida: Int = 0

    override fun toString(): String {
        return "Nome: $name, descrição: $description, ataque: $attack, defesa: $defense, tipo: $type"
    }

    fun setPosicao(posicao: String){
        if(ataqueTurno){
            println("O estado desta carta não pode ser alterado pois ela já realizou um ataque nesta rodada.")
            return
        }
        if(posicao == "A"){
            attackMode = true
            defenseMode = false
            vida = attack
        } else{
            defenseMode = true
            attackMode = false
            vida = defense
        }
    }

    fun atacar(campo: Campo, turno: Int, defensor: Jogador, atacante: Jogador) {

        if(!ataqueTurno){

            val campoInimigo = if(turno == 1) campo.ladoVermelho else campo.ladoAzul
            val campoAtacante = if(campoInimigo == campo.ladoVermelho) campo.ladoAzul else campo.ladoVermelho

            if(campoInimigo.isEmpty()){
                defensor.setVida(defensor.getVida() - attack)
                println("Dano direto causado ao jogador ${defensor.nome}: $attack")
            }
            if(attackMode){

                println("Selecione uma das cartas do campo inimigo para atacar: ")
                val auxTurno = if(turno ==1) turno + 1 else turno - 1
                campo.printCartasCampo(auxTurno)
                val indexCarta = readlnOrNull()?.toInt()
                val cartaSelecionada = indexCarta?.let { campoInimigo[it]}

                if(cartaSelecionada?.attackMode == true){

                    if(attack > cartaSelecionada.vida){

                        val danoDireto = attack - cartaSelecionada.vida
                        defensor.setVida(defensor.getVida() - danoDireto)

                        println("Monstro ${cartaSelecionada.name} destruido e $danoDireto causado ao jogador ${defensor.nome}")
                        campoInimigo.remove(cartaSelecionada)
                        ataqueTurno = true

                    } else if(attack < cartaSelecionada.vida){

                        val danoDireto = cartaSelecionada.vida - attack
                        println("Seu monstro foi mais fraco, foi destruido e você perdeu $danoDireto pontos de vida")
                        atacante.setVida(atacante.getVida() - danoDireto)
                        campoAtacante.remove(this)
                    }
                } else{
                    if(attack > cartaSelecionada?.vida!!){

                        println("Carta ${cartaSelecionada.name} destruída")
                        campoInimigo.remove(cartaSelecionada)
                        ataqueTurno = true

                    } else if(attack < cartaSelecionada.vida){
                        val danoDireto = cartaSelecionada.vida - attack
                        println("Seu monstro foi mais fraco, foi destruido e você perdeu $danoDireto pontos de vida.")
                        campoAtacante.remove(this)
                    }
                }
            }else{
                println("Cartas em modo de defesa não podem atacar!")
            }
        }else{
            println("Esta carta ja atacou neste turno")
        }
    }

    fun equipar(equip: CartaEquip){
        attack += equip.attack
        defense+= equip.defense
        equipamentos.add(equip)
    }
}