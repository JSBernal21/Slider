package com.example.slidegame.logic

import kotlin.math.abs

class Puzzle_Logic {
    fun createGrid(): List<Int?> {
        return listOf(
            1,2,3,
            4,5,6,
            7,8,9
        )
    }

    fun shuffleGrid(): List<Int?> {
        val shuffled: List<Int?> = createGrid().shuffled()
        return shuffled
    }

    fun isValidMove(i1: Int, i2: Int): Boolean {

        val r1 = i1 / 3
        val c1 = i1 % 3

        val r2 = i2 / 3
        val c2 = i2 % 3

        return (r1 == r2 && abs(c1 - c2) == 1) ||
                (c1 == c2 && abs(r1 - r2) == 1)
    }

    fun swap(board: List<Int?>, i1: Int, i2: Int): List<Int?> {

        val newBoard = board.toMutableList()

        val temp = newBoard[i1]
        newBoard[i1] = newBoard[i2]
        newBoard[i2] = temp

        return newBoard
    }

    fun calculateMinimumMoves(board: List<Int?>): Int {

        var distance = 0

        board.forEachIndexed { index, value ->

            if (value != null) {

                val correctRow = (value - 1) / 3
                val correctCol = (value - 1) % 3

                val currentRow = index / 3
                val currentCol = index % 3

                distance += abs(correctRow - currentRow) +
                        abs(correctCol - currentCol)
            }
        }

        return distance
    }

    // ==========================================
    // 6️⃣ Verificar si ganó
    // ==========================================
    fun isSolved(board: List<Int?>): Boolean {

        return board == createGrid()
    }
}