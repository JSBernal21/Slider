package com.example.slidegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slidegame.ui.theme.SlideGameTheme
import com.example.slidegame.logic.Puzzle_Logic
import com.example.slidegame.logic.GameStateEnum

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlideGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Slider()
                }
            }
        }
    }
}

@Composable
fun Slider() {
    val logic = remember { Puzzle_Logic() }
    var gameState by remember { mutableStateOf(GameStateEnum.START) }

    var board by remember { mutableStateOf(logic.createGrid()) }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    var moves by remember { mutableStateOf(0) }

    var goal by remember { mutableStateOf(0) }

    when (gameState){
        GameStateEnum.START -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text("Welcome to the slider game")
                Text("Create by Oscar Gonzales and Jonathan ernal")
                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {

                    board=logic.shuffleGrid()
                    goal = logic.calculateMinimumMoves(board)
                    gameState= GameStateEnum.PLAYING

                }) { Text("start") }
            }
        }
        GameStateEnum.PLAYING->{
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text("Movimientos: $moves")
                Text("Meta mínima: $goal")
                Spacer(modifier = Modifier.height(20.dp))

                PuzzleGrid(
                    board = board,
                    selectedIndex = selectedIndex,
                    onCellClick = { index ->

                        if (selectedIndex == null) {

                            selectedIndex = index

                        } else{

                            if (logic.isValidMove(selectedIndex!!, index)) {

                                board = logic.swap(board, selectedIndex!!, index)

                                moves++
                            }

                            selectedIndex = null
                        }
                    }
                )
                Button(onClick = {

                    board = logic.shuffleGrid()
                    goal = logic.calculateMinimumMoves(board)
                    moves = 0

                }) { Text("reset") }
                Button(onClick = {
                    gameState= GameStateEnum.START
                }) { Text("Finish round") }
            }
        }
    }
}

@Composable
fun PuzzleGrid(
    board: List<Int?>,
    selectedIndex: Int?,
    onCellClick: (Int) -> Unit
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.size(260.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        itemsIndexed(board) { index, value ->

            PuzzleCell(
                value = value,
                selected = selectedIndex == index,
                onClick = { onCellClick(index) }
            )
        }
    }
}

@Composable
fun PuzzleCell(
    value: Int?,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(80.dp).clickable(enabled = value != null) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        if (value != null) {

            Text(
                text = value.toString(),
                fontSize = 24.sp
            )
        }
    }
}
