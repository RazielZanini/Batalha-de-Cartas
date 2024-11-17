package model

import model.cartas.Carta
import model.cartas.CartaMonstro

class Jogador(var nome: String) {

    private var vida: Int = 10000
    var cartas  = mutableListOf<Carta>()

        fun getVida():Int{
            return vida
        }

        fun setVida(vida:Int){
            this.vida = vida
        }

    fun printCartas(){
        for (i in 0..<cartas.size) {
            println("$i : ${cartas[i].name} | tipo: ${cartas[i].type} | ")
        }
    }
}