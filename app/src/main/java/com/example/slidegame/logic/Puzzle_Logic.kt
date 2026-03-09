package com.example.slidegame.logic

import kotlin.math.abs

class Puzzle_Logic {
    fun createGrid(n: Int): List<Int?> {
        val grid = MutableList<Int?>(n*n){if(it == (n*n)-1) 0 else it+1}
        return grid
    }

    fun shuffleGrid(n: Int): List<Int?> {
        val shuffled: List<Int?> = createGrid(n).shuffled()
        return shuffled
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

    fun calculateMinimumMoves(board: List<Int?>, n: Int): Int {

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

        return distance
    }

    fun isSolved(board: List<Int?>, n: Int): Boolean {
        //compara el estado del tablero con la grilla original (sin desordenar)
        return board == createGrid(n)
    }
}