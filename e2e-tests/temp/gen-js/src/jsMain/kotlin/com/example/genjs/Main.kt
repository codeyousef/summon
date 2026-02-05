package com.example.genjs

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.BasicText
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.padding
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.renderComposableRoot

@Composable
fun App() {
    val counter = remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier().padding("16px")
    ) {
        BasicText(
            text = "Welcome to GenJs!",
            modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
        )
        
        BasicText(
            text = "Count: ${counter.value}",
            modifier = Modifier().padding(bottom = "16px", left = "0px", right = "0px", top = "0px")
        )
        
        Button(
            onClick = { 
                println("Button clicked! Current count: ${counter.value}")
                counter.value++ 
                println("New count: ${counter.value}")
            },
            label = "Click me!"
        )
    }
}

fun main() {
    renderComposableRoot("root") {
        App()
    }
}