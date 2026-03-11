package com.example.slidegame.logic

import kotlin.math.abs

class Puzzle_Logic {
    fun createGrid(n: Int): List<Int?> {
        val grid = MutableList<Int?>(n*n){if(it == (n*n)-1) 0 else it+1}
        return grid
    }

    fun shuffleGrid(n: Int): List<Int?> {
        var shuffled: List<Int?>
        do {
            shuffled = createGrid(n).shuffled()
        }while (!isSolvable(shuffled,n))
        return shuffled
    }

    // Verifica si el puzzle es resoluble

    private fun isSolvable(board: List<Int?>, n: Int): Boolean {

        val numbers = board.filter{it !=0}
        var inversions = 0

        for (i in numbers.indices) {
            for (j in i + 1 until numbers.size) {
                numbers[i]?.let {
                    if (it > numbers[j]!!) {
                        inversions++
                    }
                }
            }
        }

        // caso tablero impar (3x3,5x5...)
        if (n % 2 != 0) {
            return inversions % 2 == 0
        }

        // caso tablero par (4x4,6x6...)
        val blankIndex = board.indexOf(0)
        val blankRowFromBottom = n - (blankIndex / n)

        return (blankRowFromBottom + inversions) % 2 == 1
    }

    fun isValidMove(i1: Int, i2: Int, n: Int, value: Int?): Boolean {

        val r1 = i1 / n
        val c1 = i1 % n

        val r2 = i2 / n
        val c2 = i2 % n

        return (r1 == r2 && abs(c1 - c2) == 1 && value==0) ||
                (c1 == c2 && abs(r1 - r2) == 1 && value==0)
    }

    fun swap(board: List<Int?>, i1: Int, i2: Int): List<Int?> {

        val newBoard = board.toMutableList()

        val temp = newBoard[i1]
        newBoard[i1] = newBoard[i2]
        newBoard[i2] = temp

        return newBoard
    }

    fun calculateMinimumMoves(board: List<Int?>, n: Int): Int {//https://www.dcode.fr/sliding-puzzle-solver ideas algoritmo

        var manhattan = 0
        var conflict = 0

        board.forEachIndexed { index, value ->

            if (value != null && value != 0) {

                val correctRow = (value - 1) / n
                val correctCol = (value - 1) % n

                val currentRow = index / n
                val currentCol = index % n

                manhattan += abs(correctRow - currentRow) +
                        abs(correctCol - currentCol)
            }
        }

        // Linear conflict filas
        //---------------------------------------- recorremos cada valor de mi lista-talero
        for (row in 0 until n) {
            for (col1 in 0 until n) {
                //----------------------------------------
                val i1 = row * n + col1// se calcula la posicion (ficha)
                val v1 = board[i1] ?: continue // guardamos el valor de la ficha
                if (v1 == 0) continue // ignora el valor null (0)
                //-------------------------------- calculamos donde esta
                val goalRow1 = (v1 - 1) / n //fila
                val goalCol1 = (v1 - 1) % n //columna
                //------------------------------------
                if (goalRow1 != row) continue // si no le pertenece a esa fila lo ignora
                //------------------------------------ comparacion con otra ficha de la misma columna
                for (col2 in col1 + 1 until n) {
                    val i2 = row * n + col2 // obtenemos la posicion de la segunda ficha y su valor
                    val v2 = board[i2] ?: continue
                    if (v2 == 0) continue

                    val goalRow2 = (v2 - 1) / n //donde deberia estar
                    val goalCol2 = (v2 - 1) % n
                    // se verifica dos cosa, que esten en la misma fila (linea 103 pieza 1) y que esten en ORDEN INVERTIDO
                    if (goalRow2 == row && goalCol1 > goalCol2) {
                        conflict += 2// si hay conflicto entonces se le suman 2
                    }
                }
            }
        }

        return (manhattan + conflict)*((if(n==6) 3.58 else if (n==5) 3.2 else if (n==4) 3 else 2.4)).toInt()
    }
    fun isSolved(board: List<Int?>, n: Int): Boolean {
        //compara el estado del tablero con la grilla original (sin desordenar)
        return board == createGrid(n)
    }
}