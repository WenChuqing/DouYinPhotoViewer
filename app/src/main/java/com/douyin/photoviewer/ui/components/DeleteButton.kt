package com.douyin.photoviewer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 删除按钮组件
 * 抖音式的右侧悬浮按钮，双击删除
 */
@Composable
fun DeleteButton(
    onClick: () -> Unit,
    showHint: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 删除提示文字（双击时显示）
        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Text(
                text = "双击删除",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(
                        color = Color(0xCCFF4444),
                        shape = CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 删除按钮
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0x80000000))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        bounded = true,
                        color = Color(0xFFFF4444)
                    ),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_delete),
                contentDescription = "删除",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Text(
            text = "删除",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
