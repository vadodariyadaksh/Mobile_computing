package com.example.cellulargameapp// Updated to match your exact project name!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CellularGameApp()
                }
            }
        }
    }
}

enum class GameState { PLAYING, REVEALED }

@Composable
fun CellularGameApp() {
    // Game State Variables
    var score by remember { mutableIntStateOf(0) }
    var iParam by remember { mutableIntStateOf(1) }
    var jParam by remember { mutableIntStateOf(1) }
    var userInput by remember { mutableStateOf("") }
    var gameState by remember { mutableStateOf(GameState.PLAYING) }
    var feedbackText by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(Color.Black) }

    // Function to generate a new round
    fun generateNewRound() {
        iParam = Random.nextInt(1, 3)
        jParam = Random.nextInt(1, 3)
        userInput = ""
        gameState = GameState.PLAYING
        feedbackText = ""
    }

    // Initialize first round
    LaunchedEffect(Unit) { generateNewRound() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Exp 2: Cellular Game", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Score: $score", fontSize = 18.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 16.dp))

        // Game Instructions
        Card(modifier = Modifier.fillMaxWidth().padding(8.dp), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Locate the Co-Channel Cells", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Move i = $iParam cells, turn 60°, move j = $jParam cells.", modifier = Modifier.padding(top = 8.dp))
                Text("What is the Cluster Size (N)?", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))

                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Enter N") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.padding(top = 8.dp).width(150.dp),
                    enabled = gameState == GameState.PLAYING
                )

                Button(
                    onClick = {
                        if (gameState == GameState.PLAYING) {
                            val userN = userInput.toIntOrNull()
                            val actualN = (iParam * iParam) + (iParam * jParam) + (jParam * jParam)

                            if (userN == actualN) {
                                score += 10
                                feedbackText = "Correct! N = $actualN. Visualizing cells..."
                                feedbackColor = Color(0xFF2E7D32) // Dark Green
                                gameState = GameState.REVEALED
                            } else {
                                feedbackText = "Incorrect! N = i² + ij + j² = $actualN"
                                feedbackColor = Color.Red
                                gameState = GameState.REVEALED
                            }
                        } else {
                            generateNewRound()
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(if (gameState == GameState.PLAYING) "Check Answer & Reveal Map" else "Next Round")
                }

                if (feedbackText.isNotEmpty()) {
                    Text(text = feedbackText, color = feedbackColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Text("🟥 Reference Cell", fontSize = 14.sp)
            Text("🟦 Co-Channel Cells", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Hexagonal Cellular Grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val hexRadius = 32f
            val cx = size.width / 2
            val cy = size.height / 2

            // 1. Draw Background Grid (using Axial Coordinates q, r)
            for (q in -5..5) {
                for (r in -5..5) {
                    if (abs(q) + abs(r) + abs(-q - r) <= 10) {
                        val x = cx + hexRadius * sqrt(3f) * (q + r / 2f)
                        val y = cy + hexRadius * 3f / 2f * r
                        drawHexagon(x, y, hexRadius, Color.LightGray.copy(alpha = 0.3f), Color.Gray)
                    }
                }
            }

            // 2. Draw Center Reference Cell (Red)
            drawHexagon(cx, cy, hexRadius, Color.Red.copy(alpha = 0.6f), Color.Red)

            // 3. Draw Target Co-Channel Cells if Revealed (Blue)
            if (gameState == GameState.REVEALED) {
                val qBase = iParam + jParam
                val rBase = -jParam

                val coChannels = listOf(
                    Pair(qBase, rBase),
                    Pair(-rBase, qBase + rBase),
                    Pair(-qBase - rBase, qBase),
                    Pair(-qBase, -rBase),
                    Pair(rBase, -qBase - rBase),
                    Pair(qBase + rBase, -qBase)
                )

                for ((q, r) in coChannels) {
                    val targetX = cx + hexRadius * sqrt(3f) * (q + r / 2f)
                    val targetY = cy + hexRadius * 3f / 2f * r
                    drawHexagon(targetX, targetY, hexRadius, Color.Blue.copy(alpha = 0.6f), Color.Blue)
                }
            }
        }
    }
}

// Extension function to draw a mathematical Pointy-Topped Hexagon
fun DrawScope.drawHexagon(x: Float, y: Float, radius: Float, fillColor: Color, strokeColor: Color) {
    val path = Path()
    for (i in 0..5) {
        val angleDeg = 60f * i - 30f
        val angleRad = PI / 180f * angleDeg
        val px = x + radius * cos(angleRad).toFloat()
        val py = y + radius * sin(angleRad).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path = path, color = fillColor)
    drawPath(path = path, color = strokeColor, style = Stroke(width = 2f))
}