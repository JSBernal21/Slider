package com.example.slidegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slidegame.logic.GameStateEnum
import com.example.slidegame.logic.Puzzle_Logic
import com.example.slidegame.ui.theme.SlideGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlideGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SliderGame()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderGame() {
    val logic = remember { Puzzle_Logic() }
    var gameState by remember { mutableStateOf(GameStateEnum.START) }
    var board by remember { mutableStateOf(logic.createGrid(0)) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var goal by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    val items = listOf("3x3", "4x4", "5x5", "6x6")
    var dimension by remember { mutableIntStateOf(0) }

    // Animación de entrada
    val enterTransition = fadeIn(animationSpec = tween(500)) +
            slideInVertically(animationSpec = tween(500))

    Scaffold(
        topBar = {
            if (gameState != GameStateEnum.START) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Slide Puzzle",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (gameState) {
                GameStateEnum.START -> StartScreen(
                    items = items,
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    dimension = dimension,
                    onDimensionSelect = { dimension = it },
                    onStartClick = {
                        board = logic.createGrid(dimension)
                        board = logic.shuffleGrid(dimension)
                        goal = logic.calculateMinimumMoves(board, dimension)
                        gameState = GameStateEnum.PLAYING
                    }
                )

                GameStateEnum.PLAYING -> PlayingScreen(
                    moves = moves,
                    goal = goal,
                    dimension = dimension,
                    board = board,
                    selectedIndex = selectedIndex,
                    onCellClick = { index ->
                        if (selectedIndex == null) {
                            selectedIndex = index
                        } else {
                            if (logic.isValidMove(selectedIndex!!, index, dimension, board.elementAt(index))) {
                                board = logic.swap(board, selectedIndex!!, index)
                                moves++
                            }
                            selectedIndex = null
                        }
                    },
                    onResetClick = {
                        board = logic.shuffleGrid(dimension)
                        goal = logic.calculateMinimumMoves(board, dimension)
                        moves = 0
                    },
                    onFinishClick = {
                        gameState = GameStateEnum.START
                        moves = 0
                    }
                )

                GameStateEnum.FINISH -> FinishScreen(
                    moves = moves,
                    goal = goal,
                    onNewGameClick = {
                        moves = 0
                        gameState = GameStateEnum.START
                    }
                )
            }
        }
    }
}

@Composable
fun StartScreen(
    items: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    dimension: Int,
    onDimensionSelect: (Int) -> Unit,
    onStartClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono animado
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Slide Puzzle",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Creado por Oscar Gonzales y Jonathan Bernal",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Selector de dificultad mejorado
        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona la dificultad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { onExpandedChange(true) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (dimension == 0) "Seleccionar..." else "${dimension}x${dimension}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        items.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    onDimensionSelect(index + 3)
                                    onExpandedChange(false)
                                },
                                leadingIcon = {
                                    Text(
                                        text = "🔢",
                                        fontSize = 20.sp
                                    )
                                }
                            )
                        }
                    }
                }

                if (dimension > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Dificultad: ${dimension}x${dimension}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = dimension > 0,
            enter = fadeIn() + expandVertically()
        ) {
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "¡JUGAR!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlayingScreen(
    moves: Int,
    goal: Int,
    dimension: Int,
    board: List<Int?>,
    selectedIndex: Int?,
    onCellClick: (Int) -> Unit,
    onResetClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Stats cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                title = "Movimientos",
                value = moves.toString(),
                color = MaterialTheme.colorScheme.primary
            )
            StatCard(
                title = "Meta mínima",
                value = goal.toString(),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Grid del puzzle
        Card(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            PuzzleGrid(
                board = board,
                selectedIndex = selectedIndex,
                dimension = dimension,
                onCellClick = onCellClick
            )
        }

        // Botones de acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = onResetClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar")
            }

            OutlinedButton(
                onClick = onFinishClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Terminar")
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = color.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun PuzzleGrid(
    board: List<Int?>,
    selectedIndex: Int?,
    dimension: Int,
    onCellClick: (Int) -> Unit
) {
    val boardSize = 320.dp
    val cellSize = (boardSize - (4.dp * (dimension - 1))) / dimension

    LazyVerticalGrid(
        columns = GridCells.Fixed(dimension),
        modifier = Modifier
            .size(boardSize)
            .padding(8.dp),
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
    val scale by animateFloatAsState(
        targetValue = if (selected) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val backgroundColor = when {
        value == 0 -> Color.Transparent
        selected -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = when {
        value == 0 -> Color.Transparent
        selected -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = Modifier
            .size(cellSize)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .then(
                if (value != 0) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .shadow(
                elevation = if (value != 0) 4.dp else 0.dp,
                shape = RoundedCornerShape(8.dp),
                clip = false
            ),
        contentAlignment = Alignment.Center
    ) {
        if (value != 0) {
            Text(
                text = value.toString(),
                fontSize = when {
                    cellSize < 50.dp -> 16.sp
                    cellSize < 70.dp -> 20.sp
                    else -> 24.sp
                },
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

@Composable
fun FinishScreen(
    moves: Int,
    goal: Int,
    onNewGameClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de celebración
        Box(
            modifier = Modifier.size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.primary
                            )
                        ),
                        shape = RoundedCornerShape(percent = 50)
                    )
                    .graphicsLayer { rotationZ = rotation }
            )

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¡Felicidades!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Has completado el puzzle",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Resultados
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ResultRow("Tus movimientos", moves.toString())
                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ResultRow("Meta mínima", goal.toString())

                Spacer(modifier = Modifier.height(20.dp))

                val (message, color) = when {
                    moves < goal -> "¡Superaste la meta! 🎉" to MaterialTheme.colorScheme.primary
                    moves > goal -> "Casi lo logras, ¡sigue practicando!" to MaterialTheme.colorScheme.secondary
                    else -> "¡Igualaste la meta perfectamente!" to MaterialTheme.colorScheme.tertiary
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNewGameClick,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "NUEVO JUEGO",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}