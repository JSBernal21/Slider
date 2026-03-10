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

    fun calculateMinimumMoves(board: List<Int?>, n: Int): Int {//https://www.dcode.fr/sliding-puzzle-solver -- Manhattan Method

        var distance = 0

        board.forEachIndexed { index, value ->

            if (value != null) {
                //posicion correcta
                val correctRow = (value - 1) / n
                val correctCol = (value - 1) % n
                //posicion en la que esta
                val currentRow = index / n
                val currentCol = index % n
                //distancia entre la posicion correcta y la actual (suma de todos los valores)
                distance += abs(correctRow - currentRow) +
                        abs(correctCol - currentCol)

            }
        }

        return distance+distance/n//margen de error humano
    }

    fun isSolved(board: List<Int?>, n: Int): Boolean {
        //compara el estado del tablero con la grilla original (sin desordenar)
        return board == createGrid(n)
    }
}