import controllers.CartaController
import model.Campo
import model.Jogador
import model.cartas.Carta
import model.cartas.CartaEquip
import model.cartas.CartaMonstro
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.random.Random

object Turno {
    var vez = 1

    fun trocarVez() {
        vez = if (vez == 1) {
            2
        } else {
            1
        }
    }
}

fun main() {
    //variável para armazenar as cartas já carregadas
    val cardList: MutableList<Carta> = loadCards()

    var rodada = 1

    println("Informe os nomes dos jogadores: ")

    val jogador1 = Jogador(readlnOrNull().toString());
    val jogador2 = Jogador(readlnOrNull().toString());

    val tabuleiro = Campo()
    val ladoCampo = if(Turno.vez == 1)tabuleiro.ladoAzul else tabuleiro.ladoVermelho

    //distribuição de cartas para cada jogador
    for (i in 0..<5) {
        var index = Random.nextInt(cardList.size)
        jogador1.cartas.add(cardList[index])
        cardList.removeAt(index)
        index = Random.nextInt(cardList.size)
        jogador2.cartas.add(cardList[index])
        cardList.removeAt(index)
    }

    while (jogador1.getVida() > 0 && jogador2.getVida() > 0 || cardList.size == 0) {
        val jogadorVez = if (Turno.vez == 1) jogador1 else jogador2

        println("Rodada: $rodada")
        println("Vez do jogador ${Turno.vez} : ${jogadorVez.nome}")

        //seleciona qual o jogador do turno
        println(
            "Selecione uma ação para realizar: \n" +
                    "1 - Posicionar um novo monstro no tabuleiro;\n" +
                    "2 - Equipar um monstro com um equipamento;\n" +
                    "3 - Descartar uma carta da sua mão;\n" +
                    "4 - Realizar um ataque;\n" +
                    "5 - Alterar o estado de uma carta(ataque/defesa)\n" +
                    "6 - Passar a vez"
        )

            val opcao: Int? = readlnOrNull()?.toIntOrNull()

        if(opcao == null){
            println("Erro! A opção deve ser um numero.")
        }
        //verificar a escolha de opcao do usuário e executa de acordo
        when (opcao) {

            1 -> {
                CartaController.invocarCarta(jogadorVez, tabuleiro)
            }

            2 -> {
                CartaController.equiparCarta(jogadorVez, tabuleiro)
            }

            3 -> {
                CartaController.descartarCarta(jogadorVez)
            }

            4 -> {
                if (rodada >= 2) {
                    println("Selecione uma carta do tipo monstro para atacar: ")
                    tabuleiro.printCartasCampo(Turno.vez)
                    val indexCarta = readlnOrNull()?.toInt()
                    if (indexCarta == null || indexCarta !in jogadorVez.cartas.indices) {
                        println("Erro! Valor inválido, tente novamente.")
                        return
                    }
                    val cartaSlecionada = ladoCampo[indexCarta]
                    if (cartaSlecionada is CartaMonstro) {
                        val jogadorInimigo = if (Turno.vez == 1) jogador2 else jogador1
                        cartaSlecionada.atacar(tabuleiro, Turno.vez, jogadorInimigo, jogadorVez)
                    } else {
                        println("Somente cartas do tipo monstro podem atacar!")
                    }
                } else {
                    println("Ataques só podem ser feitos a partir da segunda rodada")
                }
            }

            5 -> {
                println("Escolha uma carta para alterar o estado: ")

                tabuleiro.printCartasCampo(Turno.vez)
                val indexCarta = readlnOrNull()?.toInt()

                if (indexCarta == null || indexCarta !in ladoCampo.indices){
                    println("Erro! Valor inválido, tente novamente.")
                    return
                }

                val cartaSelecionada = ladoCampo[indexCarta]
                println("Carta em estado de ataque ou defesa? (A/D)")
                val estado: String = readlnOrNull().orEmpty().uppercase()
                cartaSelecionada?.setPosicao(estado)
                println("Estado de ${cartaSelecionada?.name} alterada com sucesso!")
            }

            6 -> {
                if (Turno.vez == 2) {
                    addCartaTurno(jogador1, jogador2, cardList)
                    rodada++
                    //renova os ataques dos jogadores
                    resetAtaques(jogador1, jogador2)
                }
                Turno.trocarVez()
            }

        }
    }

    declararGanhador(jogador1, jogador2)
}

fun addCartaTurno(jogador1: Jogador, jogador2: Jogador, cardList: MutableList<Carta>){
    var index = Random.nextInt(cardList.size)
    jogador1.cartas.add(cardList[index])
    cardList.removeAt(index)
    index = Random.nextInt(cardList.size)
    jogador2.cartas.add(cardList[index])
    cardList.removeAt(index)
}

fun declararGanhador(jogador1: Jogador, jogador2: Jogador){
    if(jogador1.getVida() > jogador2.getVida()){
        println("O ganhador é ${jogador1.nome}")
    } else{
        println("O ganhador é ${jogador2.nome}")
    }
}

fun resetAtaques(jogador1: Jogador, jogador2: Jogador) {
    jogador1.cartas.forEach { if (it is CartaMonstro) it.ataqueTurno = false }
    jogador2.cartas.forEach { if (it is CartaMonstro) it.ataqueTurno = false }
}

fun Campo.getLadoTabuleiro(vez: Int): MutableList<CartaMonstro?> {
    return if (vez == 1) ladoAzul else ladoVermelho
}

//função para carregar cartas de um arquivo .csv
fun loadCards(): MutableList<Carta> {
    val path = "/home/raziel/facul/PDM/cartas.csv"
    val fileLines: List<String>? = fileReader(path)

    // Converte para MutableList ao final
    val cardList: MutableList<Carta> = fileLines?.map { line ->
        val tokens = line.split(";")
        if (tokens[4] == "equipamento") {
            CartaEquip(tokens[0], tokens[1], tokens[2].toInt(), tokens[3].toInt(), tokens[4])
        } else {
            CartaMonstro(tokens[0], tokens[1], tokens[2].toInt(), tokens[3].toInt(), tokens[4])
        }
    }?.toMutableList() ?: mutableListOf() // Converte para MutableList ou retorna lista mutável vazia

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
