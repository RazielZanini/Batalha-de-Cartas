package model.cartas

class CartaMonstro(name: String, description: String, attack: Int, defense: Int,  type: String):
    Carta(name, description, attack, defense, type){

        var equipamentos = mutableListOf<CartaEquip>()
        var attackMode: Boolean = false
        var defenseMode: Boolean = false

    override fun toString(): String {
        return "Nome: $name, descrição: $description, ataque: $attack, defesa: $defense, tipo: $type"
    }

    fun setPosicao(posicao: String){
        if(posicao == "Ataque"){
            attackMode = true
        } else{
            defenseMode = true
        }
    }

    fun atacar(carta: CartaMonstro) {

    }

    fun equipar(equip: CartaEquip){
        attack += equip.attack
        defense+= equip.defense
        equipamentos.add(equip)
    }
}