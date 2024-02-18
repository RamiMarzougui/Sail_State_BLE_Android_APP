package com.example.bletutorial.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bletutorial.R

//Main Start Screen
@Composable
fun StartScreen(
    // Contrôleur de naviagtion entre Screen
    navController: NavController
) {
    // Définition d'une Box qui contient notre bouton
    Box(
        // Occupe tout l'écran
        modifier = Modifier.fillMaxSize(),
        // Centre tout
        contentAlignment = Alignment.Center
    ){


        Column(
            modifier = Modifier.fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AddImage(R.drawable.sens2sail, 270.dp, 100.dp) //Display the image

            Text(
                text = "v1.2.0",
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )

            Box(
                // Définition de l'apparence du bouton
                modifier = Modifier
                    .size(230.dp)
                    .clip(CircleShape)
                    // Définit l'action au clic, ici on va à l'écran Temperature et humidity
                    .clickable {
                        navController.navigate(Screen.ConnectionScreen.route){
                            popUpTo(Screen.StartScreen.route){
                                inclusive = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ){
                PulsatingCircles("SCAN")
            }


            AddImage(R.drawable.nwt_logo_quadri, 280.dp, 100.dp)
        }





    }

}

@Composable
fun SimpleCircleShape2(
    size: Dp,
    color: Color = Color.White,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.LightGray.copy(alpha = 0.0f)
) {
    Column(
        modifier = Modifier
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    color
                )
                .border(borderWidth, borderColor)
        )
    }
}

@Composable
fun PulsatingCircles_2(text: String) {
    Column {
        val infiniteTransition = rememberInfiniteTransition();       val size by infiniteTransition.animateValue(
        initialValue = 165.dp,
        targetValue = 135.dp,
        Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
        val smallCircle by infiniteTransition.animateValue(
        initialValue = 135.dp,
        targetValue = 113.dp,
        Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp),
        contentAlignment = Alignment.Center
        ) {
        SimpleCircleShape2(
            size = size,
            color = MaterialTheme.colors.primary.copy(alpha = 0.25f)
        )
        SimpleCircleShape2(
            size = smallCircle,
            color = MaterialTheme.colors.primary.copy(alpha = 0.25f)
        )
        SimpleCircleShape2(
            size = 113.dp,
            color = MaterialTheme.colors.onPrimary
        )
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    style = TextStyle(color =  MaterialTheme.colors.primaryVariant, fontSize = 25.sp,  textAlign = TextAlign.Center)//TextStyle().copy(color = MaterialTheme.colors.primary)
                )
            }
        }
    }
    }
}

@Composable
fun PulsatingCircles_3(text: String) {
    Column {
        val infiniteTransition = rememberInfiniteTransition();       val size by infiniteTransition.animateValue(
        initialValue = 145.dp,
        targetValue = 135.dp,
        Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
        val smallCircle by infiniteTransition.animateValue(
            initialValue = 135.dp,
            targetValue = 120.dp,
            Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp),
            contentAlignment = Alignment.Center
        ) {
            SimpleCircleShape2(
                size = size,
                color = MaterialTheme.colors.secondary.copy(alpha = 0.25f)
            )
            SimpleCircleShape2(
                size = smallCircle,
                color = MaterialTheme.colors.secondary.copy(alpha = 0.25f)
            )
            SimpleCircleShape2(
                size = 113.dp,
                color = MaterialTheme.colors.onPrimary
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = text,
                        style = TextStyle(color =  MaterialTheme.colors.secondaryVariant, fontSize = 25.sp,  textAlign = TextAlign.Center)//TextStyle().copy(color = MaterialTheme.colors.primary)
                    )
                }
            }
        }
    }
}


@Composable
fun PulsatingCircles(text: String) {
    Column {
        val infiniteTransition = rememberInfiniteTransition();       val size by infiniteTransition.animateValue(
        initialValue = 220.dp,
        targetValue = 180.dp,
        Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
        val smallCircle by infiniteTransition.animateValue(
            initialValue = 180.dp,
            targetValue = 150.dp,
            Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            SimpleCircleShape2(
                size = size,
                color = MaterialTheme.colors.primary.copy(alpha = 0.25f)
            )
            SimpleCircleShape2(
                size = smallCircle,
                color = MaterialTheme.colors.primary.copy(alpha = 0.25f)
            )
            SimpleCircleShape2(
                size = 150.dp,
                color = MaterialTheme.colors.onPrimary
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = text,
                        style = TextStyle(color =  MaterialTheme.colors.primaryVariant, fontSize = 25.sp,  textAlign = TextAlign.Center)//TextStyle().copy(color = MaterialTheme.colors.primary)
                    )
                }
            }
        }
    }
}






