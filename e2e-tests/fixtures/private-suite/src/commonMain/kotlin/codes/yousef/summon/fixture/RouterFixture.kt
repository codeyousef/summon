package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.routing.RouterComponent
import codes.yousef.summon.routing.createRouter

class RouterFixture {
    private val router = createRouter {
        route("/fixture") {
            Column {
                Text("Fixture route", Modifier().attribute("data-testid", "route-value"))
                Button(onClick = { navigate("/fixture/item/42") }, label = "Open fixture item")
            }
        }
        route("/fixture/item/:id") { params ->
            Column {
                Text("Fixture item ${params["id"]}", Modifier().attribute("data-testid", "route-value"))
                Button(onClick = { navigate("/fixture") }, label = "Return to fixture route")
            }
        }
    }
    private fun navigate(path: String) {
        router.navigate(path)
    }


    @Composable
    fun Content() {
        RouterComponent(router = router, initialPath = "/fixture")
    }
}
