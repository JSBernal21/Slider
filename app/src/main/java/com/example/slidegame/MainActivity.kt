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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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

    var board by remember { mutableStateOf(logic.createGrid(0)) }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    var moves by remember { mutableIntStateOf(0) }

    var goal by remember { mutableIntStateOf(0) }

    var expanded by remember { mutableStateOf(false) }
    val items = listOf("3x3", "4x4", "5x5","6x6")
    var dimension by remember { mutableIntStateOf(0) }

    when (gameState){
        GameStateEnum.START -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text("Welcome to the slider game")
                Text("Create by Oscar Gonzales and Jonathan Bernal")
                Spacer(modifier = Modifier.height(20.dp))
                // Ejemplo básico en Jetpack Compose


                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .width(140.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            "Dificultad",
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        items.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    dimension = index+3
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Text("El index del item es: $dimension")
                Button(onClick = {
                    board=logic.createGrid(dimension)
                    board=logic.shuffleGrid(dimension)
                    goal = logic.calculateMinimumMoves(board,dimension)
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
                Spacer(modifier = Modifier.height(1.dp))

                PuzzleGrid(
                    board = board,
                    selectedIndex = selectedIndex,
                    Dimension = dimension,
                    onCellClick = { index ->

                        if (selectedIndex == null) {

                            selectedIndex = index

                        } else{

                            if (logic.isValidMove(selectedIndex!!, index,dimension,board.elementAt(index))) {

                                board = logic.swap(board, selectedIndex!!, index)

                                moves++
                            }

                            selectedIndex = null
                        }
                    }
                )
                if (logic.isSolved(board,dimension)){
                    gameState= GameStateEnum.FINISH
                }
                Button(onClick = {

                    board = logic.shuffleGrid(dimension)
                    goal = logic.calculateMinimumMoves(board,dimension)
                    moves = 0

                }) { Text("reset") }
                Button(onClick = {
                    gameState= GameStateEnum.START
                }) { Text("Finish round") }
            }
        }

        GameStateEnum.FINISH->{
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Congratulation for winning")
                Text("Tu cantidad de movimientos fueron: $moves")
                Text("Meta mínima: $goal")
                if(moves<goal){
                    Text("Felicidades superaste la meta")
                }else if (moves>goal){
                    Text("Casi lo logras, la meta es un punto al que alcanzar")
                }else{
                    Text("igualaste la meta")
                }

                Button(onClick = {
                    moves = 0
                    gameState= GameStateEnum.START
                }) { Text("NEW GAME") }
            }
        }
    }
}

@Composable
fun PuzzleGrid(
    board: List<Int?>,
    selectedIndex: Int?,
    onCellClick: (Int) -> Unit,
    Dimension: Int
) {

    val boardSize = 340.dp
    val cellSize = boardSize / Dimension

    LazyVerticalGrid(
        columns = GridCells.Fixed(Dimension),
        modifier = Modifier.size(boardSize),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        itemsIndexed(board) { index, value ->

            PuzzleCell(
                value = value,
                selected = selectedIndex == index,
                cellSize = cellSize,
                onClick = { onCellClick(index) }
            )
        }
    }
}

@Composable
fun PuzzleCell(
    value: Int?,
    selected: Boolean,
    onClick: () -> Unit,
    cellSize: Dp
) {

    Box(
        modifier = Modifier
            .size(cellSize).clickable(enabled = value != null) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        if (value != 0) {

            Text(
                text = value.toString(),
                fontSize = 24.sp
            )
        }
    }
}
