package model

import model.cartas.CartaMonstro

class Campo() {
    var ladoAzul = MutableList<CartaMonstro?>(5){null}
    var ladoVermelho = MutableList<CartaMonstro?>(5){null}


    fun printCartasCampo(turno: Int) {
        val ladoCampo = if (turno == 1) ladoAzul else ladoVermelho

        for (i in ladoCampo.indices) {
            val carta = ladoCampo[i]
            if (carta != null) {
                val modo = if (carta.defenseMode) "Defesa" else "Ataque"
                println("$i : ${carta.name} | Modo: $modo - Vida: ${carta.vida}")
            } else {
                println("$i : [Espaço Vazio]")
            }
        }
    }


}