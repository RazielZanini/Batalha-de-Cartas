package model.cartas

class CartaEquip(name: String, description: String, attack: Int, defense: Int, type: String):
    Carta(name, description, attack, defense, type) {

    override fun toString(): String {
        return "Nome: $name, descrição: $description, ataque: $attack, defesa: $defense, tipo: $type"
    }
}