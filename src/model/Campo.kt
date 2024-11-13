package model

import model.cartas.CartaMonstro

class Campo() {
    var ladoAzul = MutableList<CartaMonstro?>(5){null}
    var ladoVermelho = MutableList<CartaMonstro?>(5){null}
}