import model.Campo
import model.Jogador
import model.cartas.Carta
import model.cartas.CartaEquip
import model.cartas.CartaMonstro
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.random.Random

object Turno{
    var vez = 1

    fun trocarVez(){
        vez = if(vez == 1){
            2
        }else{
            1
        }
    }
}

fun main() {
    //variável para armazenar as cartas já carregadas
    val cardList: List<Carta> = loadCards()

    println("Informe os nomes dos jogadores: ")

    val jogador1 = Jogador(readlnOrNull().toString());
    val jogador2 = Jogador(readlnOrNull().toString());

    val tabuleiro = Campo()

    //distribuição de cartas para cada jogador
    for (i in 0 until 5){
        jogador1.cartas.add(cardList[Random.nextInt(cardList.size)])
        jogador2.cartas.add(cardList[Random.nextInt(cardList.size)])
    }

    //rodadas
    while(jogador1.getVida() > 0 && jogador2.getVida() >0){
        println("Vez do jogador ${if(Turno.vez == 1) jogador1.nome else jogador2.nome}")

        println("Selecione uma ação para realizar: \n" +
                "1 - Posicionar um novo monstro no tabuleiro;\n" +
                "2 - Equipar um monstro com um equipamento;\n" +
                "3 - Descartar uma carta da sua mão;\n" +
                "4 - Realizar um ataque;\n" +
                "5 - Alterar o estado de uma carta(ataque/defesa)\n" +
                "6 - Passar a vez")

        val opcao: Int? = readlnOrNull()?.toInt()

        val jogadorVez = if(Turno.vez == 1) jogador1 else jogador2

        //verificar a escolha de opcao do usuário e executa de acordo
        when(opcao){

            1 -> {
                invocarCarta(jogadorVez, tabuleiro)
            }

            2 -> {
                equiparCarta(jogadorVez)
            }

            3 -> {
                descartarCarta(jogadorVez)
            }

            6-> Turno.trocarVez()
            else -> println("Opção inválida. Tente novamente.")
        }
    }
}

fun descartarCarta(jogador: Jogador){
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

fun invocarCarta(jogador: Jogador, tabuleiro: Campo){

    //verifica qual o lado do tabuleiro baseado no turno
    val ladoTabuleiro = if(Turno.vez == 1) tabuleiro.ladoAzul else tabuleiro.ladoVermelho

    //verifica se o tabuleiro já está cheio
    if(ladoTabuleiro.size>=5){
        println("O tabuleiro já está cheio!")
        return
    }
    println(jogador.printCartas())
    val monstroIndex = readlnOrNull()?.toInt()
    //verifica se o index selecionado está dentro do indice de cartas da mão do jogador
    val cartaSelecionada = monstroIndex?.let {
        if(it in jogador.cartas.indices) jogador.cartas[it] else println("Erro! Valor inválido, tente novamente.")
    }

    if(cartaSelecionada is CartaMonstro){
        println("Carta em estado de ataque ou defesa? (A/D)")
        val estado: String = readlnOrNull().orEmpty().uppercase()

        if(estado == "A" || estado == "D"){
            cartaSelecionada.setPosicao(estado)
            ladoTabuleiro.add(cartaSelecionada)
            jogador.cartas.remove(cartaSelecionada)
            println("Carta invocada com sucesso!")
        } else{
            println("Estado invalido! Escolha 'A' para ataque ou 'D' para defesa.")
        }
    }else{
        println("Erro! Somente monstros podem ser colocados em campo.")
    }

}

fun equiparCarta(jogador: Jogador){

    println("Esolha uma carta do tipo monstro para equipar: ")
    jogador.printCartas()
    val indexCartaMonstro = readlnOrNull()?.toInt()
    val cartaMonstroSelec = indexCartaMonstro?.let {
        if(it in jogador.cartas.indices) jogador.cartas[it] else null
    }

    if(cartaMonstroSelec is CartaMonstro){

        println("Escolha uma carta do tipo equipamento para equipar ao ${cartaMonstroSelec.name}")
        jogador.printCartas()
        val indexCartaEquip = readlnOrNull()?.toInt()
        val cartaEquipSelec = indexCartaEquip?.let { jogador.cartas[it] }

        if(cartaEquipSelec is CartaEquip){
            cartaMonstroSelec.equipamentos.add(cartaEquipSelec)
            jogador.cartas.remove(cartaEquipSelec)

        } else{

            return println("Erro! A carta deve ser do tipo equipamento.")
        }

    } else{
        return println("Erro! Somente monstros podem receber um equipamento.")
    }
}

//função para carregar cartas de um arquivo .csv
fun loadCards(): List<Carta> {
    val path = "/home/raziel/facul/PDM/cartas.csv"
    val fileLines: List<String>? = fileReader(path)

    // Usa map para filtrar e transformar em uma única operação
    val cardList: List<Carta> = fileLines?.map { line ->
        val tokens = line.split(";")
        if (tokens[4] == "equipamento") {
            CartaEquip(tokens[0], tokens[1], tokens[2].toInt(), tokens[3].toInt(), tokens[4])
        } else {
            CartaMonstro(tokens[0], tokens[1], tokens[2].toInt(), tokens[3].toInt(), tokens[4])
        }
    } ?: emptyList() // Retorna lista vazia se `fileLines` for null

    return cardList
}

//função para ler arquivo .csv
fun fileReader(path: String): List<String>? {
    val file = File(path)

    if (file.exists() && file.isFile) {
        try {
            return file.bufferedReader().readLines()
        } catch (e: FileNotFoundException) {
            println("Erro na especificação do caminho")
        } catch (e: IOException) {
            println("O arquivo está corrompido ou sendo utilizado por outro processo")
        }
    }
    return null
}
