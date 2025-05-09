package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.utils.boxShadow
import code.yousef.example.quarkus.utils.padding
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifierExtras.textAlign
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.attribute
import code.yousef.summon.modifier.justifyContent
import code.yousef.summon.runtime.Composable

// Removed local implementation of utility functions as they're now in ModifierUtils.kt

@Composable
fun ErrorPage(statusCode: Int = 404, message: String = "Page not found") {
    ThemeWrapper {
        Box(
            modifier = Modifier().fillMaxWidth().fillMaxHeight()
                .padding(all = "16px")
                .backgroundColor("#f8f9fa")
                .style("display", "flex")
                .justifyContent("center")
                .alignItems("center")
        ) {
            Column(
                modifier = Modifier().maxWidth("500px")
                    .padding(all = "32px")
                    .backgroundColor("#fff")
                    .borderRadius("8px")
                    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
                    .textAlign("center")
            ) {
                // Error icon or image
                Text(
                    text = statusCode.toString(),
                    modifier = Modifier().fontSize("72px")
                        .fontWeight("bold")
                        .color("#dc3545")
                        .marginBottom("16px")
                )

                // Error message
                Text(
                    text = message,
                    modifier = Modifier().fontSize("24px")
                        .fontWeight("medium")
                        .color("#212529")
                        .marginBottom("16px")
                )

                // Description
                Text(
                    text = "The page you're looking for might have been removed, had its name changed, or is temporarily unavailable.",
                    modifier = Modifier().fontSize("16px")
                        .color("#6c757d")
                        .marginBottom("24px")
                )

                // Go back home button
                Button(
                    onClick = {},
                    label = "Go back to homepage",
                    modifier = Modifier().paddingVH(vertical = "10px", horizontal = "24px")
                        .backgroundColor("#0d6efd")
                        .color("#fff")
                        .border("none", "", "")
                        .borderRadius("4px")
                        .cursor("pointer")
                        .attribute("hx-get", "/")
                        .attribute("hx-target", "body")
                        .attribute("hx-swap", "innerHTML")
                )
            }
        }
    }
}

@Composable
fun ErrorPage(message: String) {
    ErrorPage(statusCode = 500, message = message)
} 