package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.LazyColumn
import codes.yousef.summon.components.layout.LazyListDataProvider
import codes.yousef.summon.components.layout.LazyListItemResult
import codes.yousef.summon.components.layout.LazyListState
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.ariaAttribute
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.modifier.role
import codes.yousef.summon.modifier.style
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

private const val VIRTUAL_ITEM_COUNT = 100_000
private const val INSERTION_INDEX = 50_000
private const val INSERTED_ITEM_ID = 1_000_000

private data class PrivateFixtureItem(val id: Int)
private data class PrivateFixtureKey(val id: Int)

private class FixturePagedProvider(
    private val hasInsertion: () -> Boolean
) : LazyListDataProvider<PrivateFixtureItem> {
    val requestedIndices = mutableSetOf<Int>()

    override val itemCount: Int
        get() = VIRTUAL_ITEM_COUNT + if (hasInsertion()) 1 else 0

    override fun key(index: Int): Any = PrivateFixtureKey(idAt(index))

    override fun itemAt(index: Int): LazyListItemResult<PrivateFixtureItem> {
        requestedIndices += index
        val id = idAt(index)
        return when (id) {
            1 -> LazyListItemResult.Loading
            2 -> LazyListItemResult.Empty
            3 -> LazyListItemResult.Locked
            4 -> LazyListItemResult.PermissionDenied
            5 -> LazyListItemResult.Error("synthetic")
            else -> LazyListItemResult.Data(PrivateFixtureItem(id))
        }
    }

    fun indexOf(id: Int): Int = when {
        id == INSERTED_ITEM_ID && hasInsertion() -> INSERTION_INDEX
        hasInsertion() && id >= INSERTION_INDEX -> id + 1
        else -> id
    }

    private fun idAt(index: Int): Int = when {
        hasInsertion() && index == INSERTION_INDEX -> INSERTED_ITEM_ID
        hasInsertion() && index > INSERTION_INDEX -> index - 1
        else -> index
    }
}

class VirtualizationFixture {
    @Composable
    fun Content() {
        val inserted = remember { mutableStateOf(false) }
        val visible = remember { mutableStateOf(true) }
        val selectedId = remember { mutableStateOf(-1) }
        val openedId = remember { mutableStateOf(-1) }
        val readSnapshot = remember { mutableStateOf("Reads: 0") }
        val provider = remember { FixturePagedProvider { inserted.value } }
        val state = remember {
            LazyListState().also {
                it.setItemSize(40f)
                it.setOverscrollItems(10)
                it.setContainerSize(600f)
            }
        }

        Column {
            Text("Virtualization fixture", Modifier().attribute("data-testid", "virtual-title"))
            Text("Selected: ${selectedId.value}", Modifier().attribute("data-testid", "virtual-selected"))
            Text("Opened: ${openedId.value}", Modifier().attribute("data-testid", "virtual-opened"))
            Text(readSnapshot.value, Modifier().attribute("data-testid", "virtual-reads"))
            Button(
                label = "Insert before selection",
                onClick = {
                    inserted.value = true
                    if (selectedId.value >= 0) {
                        state.ensureItemVisible(provider.indexOf(selectedId.value), provider.itemCount)
                    }
                }
            )
            Button(
                label = "Remove inserted item",
                onClick = {
                    inserted.value = false
                    if (selectedId.value >= 0) {
                        state.ensureItemVisible(provider.indexOf(selectedId.value), provider.itemCount)
                    }
                }
            )
            Button(
                label = "Snapshot provider reads",
                onClick = { readSnapshot.value = "Reads: ${provider.requestedIndices.size}" }
            )
            Button(
                label = "Hide virtual list",
                onClick = { visible.value = false }
            )

            if (visible.value) {
                LazyColumn(
                    state = state,
                    modifier = Modifier()
                        .attribute("data-testid", "virtual-list")
                        .style("height", "600px")
                        .style("width", "360px")
                ) {
                    items(provider) { index, result ->
                        when (result) {
                            is LazyListItemResult.Data -> {
                                val item = result.value
                                val rowHeight = if (item.id != 0 && item.id % 997 == 0) 80 else 40
                                Button(
                                    label = "Open item ${item.id}",
                                    onClick = {
                                        selectedId.value = item.id
                                        openedId.value = item.id
                                    },
                                    modifier = Modifier()
                                        .attribute("data-testid", "virtual-row-${item.id}")
                                        .style("display", "block")
                                        .style("height", "${rowHeight}px")
                                        .style("width", "100%")
                                )
                            }
                            LazyListItemResult.Loading -> StateRow(index, "loading")
                            LazyListItemResult.Empty -> StateRow(index, "empty")
                            LazyListItemResult.Locked -> StateRow(index, "locked")
                            LazyListItemResult.PermissionDenied -> StateRow(index, "permission-denied")
                            is LazyListItemResult.Error -> StateRow(index, "error")
                        }
                    }
                }
            }

            Column(
                modifier = Modifier()
                    .attribute("data-testid", "virtual-reference")
                    .role("list")
                    .ariaAttribute("label", "Reference rows")
            ) {
                for (index in 0..5) {
                    Text(
                        referenceLabel(index),
                        Modifier()
                            .role("listitem")
                            .ariaAttribute("posinset", (index + 1).toString())
                            .ariaAttribute("setsize", "6")
                    )
                }
            }
        }
    }

    @Composable
    private fun StateRow(index: Int, state: String) {
        Text(
            referenceLabel(index),
            Modifier()
                .attribute("data-testid", "virtual-state-$state")
                .attribute("data-state", state)
                .style("display", "block")
                .style("height", "40px")
        )
    }

    private fun referenceLabel(index: Int): String = when (index) {
        0 -> "Open item 0"
        1 -> "loading"
        2 -> "empty"
        3 -> "locked"
        4 -> "permission-denied"
        else -> "error"
    }
}
