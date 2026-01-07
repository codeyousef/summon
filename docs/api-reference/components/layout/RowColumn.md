# Row and Column Components

Row and Column are fundamental layout components in Summon that arrange their children horizontally and vertically respectively. They now support alignment and arrangement parameters for precise control over child positioning.

## Row Component

Arranges children in a horizontal sequence with control over spacing and alignment.

### Basic Usage

```kotlin
import codes.yousef.summon.components.layout.*

@Composable
fun SimpleRow() {
    Row {
        Text("First")
        Text("Second")
        Text("Third")
    }
}
```

### With Alignment

```kotlin
@Composable
fun AlignedRow() {
    Row(
        modifier = Modifier()
            .fillMaxWidth()
            .height("100px")
            .background("#f0f0f0"),
        horizontalArrangement = Arrangement.Horizontal.Center,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Text("Centered Content")
    }
}
```

### Horizontal Arrangement Options

- `Arrangement.Horizontal.Start` - Align children to the start (default)
- `Arrangement.Horizontal.End` - Align children to the end
- `Arrangement.Horizontal.Center` - Center children horizontally
- `Arrangement.Horizontal.SpaceBetween` - Distribute children with space between
- `Arrangement.Horizontal.SpaceAround` - Distribute children with space around
- `Arrangement.Horizontal.SpaceEvenly` - Distribute children with equal space

### Vertical Alignment Options

- `Alignment.Vertical.Top` - Align children to the top (default)
- `Alignment.Vertical.CenterVertically` - Center children vertically
- `Alignment.Vertical.Bottom` - Align children to the bottom

### Examples

```kotlin
// Space between items
Row(
    modifier = Modifier().fillMaxWidth(),
    horizontalArrangement = Arrangement.Horizontal.SpaceBetween
) {
    Button(onClick = {}) { Text("Cancel") }
    Button(onClick = {}) { Text("Save") }
}

// Center everything
Row(
    modifier = Modifier()
        .fillMaxWidth()
        .height("200px"),
    horizontalArrangement = Arrangement.Horizontal.Center,
    verticalAlignment = Alignment.Vertical.CenterVertically
) {
    Icon("home")
    Spacer(modifier = Modifier().width("8px"))
    Text("Home")
}

// Bottom aligned with end arrangement
Row(
    modifier = Modifier()
        .fillMaxWidth()
        .height("80px")
        .padding("16px"),
    horizontalArrangement = Arrangement.Horizontal.End,
    verticalAlignment = Alignment.Vertical.Bottom
) {
    Text("Status: ")
    Text("Active", modifier = Modifier().color("green"))
}
```

## Column Component

Arranges children in a vertical sequence with control over spacing and alignment.

### Basic Usage

```kotlin
@Composable
fun SimpleColumn() {
    Column {
        Text("Header")
        Text("Body")
        Text("Footer")
    }
}
```

### With Alignment

```kotlin
@Composable
fun AlignedColumn() {
    Column(
        modifier = Modifier()
            .fillMaxHeight()
            .width("300px")
            .background("#f5f5f5"),
        verticalArrangement = Arrangement.Vertical.Center,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Text("Centered Content")
    }
}
```

### Vertical Arrangement Options

- `Arrangement.Vertical.Top` - Align children to the top (default)
- `Arrangement.Vertical.Bottom` - Align children to the bottom
- `Arrangement.Vertical.Center` - Center children vertically
- `Arrangement.Vertical.SpaceBetween` - Distribute children with space between
- `Arrangement.Vertical.SpaceAround` - Distribute children with space around
- `Arrangement.Vertical.SpaceEvenly` - Distribute children with equal space

### Horizontal Alignment Options

- `Alignment.Horizontal.Start` - Align children to the start (default)
- `Alignment.Horizontal.CenterHorizontally` - Center children horizontally
- `Alignment.Horizontal.End` - Align children to the end

### Examples

```kotlin
// Card with centered content
Column(
    modifier = Modifier()
        .width("300px")
        .padding("24px")
        .background("white")
        .shadow("0", "2px", "4px", "rgba(0,0,0,0.1)")
        .borderRadius("8px"),
    horizontalAlignment = Alignment.Horizontal.CenterHorizontally
) {
    Image("profile.jpg", modifier = Modifier().size("100px").borderRadius("50%"))
    Spacer(modifier = Modifier().height("16px"))
    Text("John Doe", modifier = Modifier().fontSize("1.2rem").fontWeight("bold"))
    Text("Software Developer", modifier = Modifier().color("#666"))
}

// Form with space between
Column(
    modifier = Modifier()
        .width("400px")
        .height("500px")
        .padding("20px"),
    verticalArrangement = Arrangement.Vertical.SpaceBetween
) {
    Column {
        Text("Login", modifier = Modifier().fontSize("2rem"))
        TextField(value = email, onValueChange = { email = it }, placeholder = "Email")
        TextField(value = password, onValueChange = { password = it }, placeholder = "Password", type = TextFieldType.Password)
    }
    
    Button(onClick = { /* login */ }, modifier = Modifier().fillMaxWidth()) {
        Text("Sign In")
    }
}

// Navigation menu
Column(
    modifier = Modifier()
        .width("250px")
        .fillMaxHeight()
        .background("#2c3e50")
        .padding("20px"),
    verticalArrangement = Arrangement.Vertical.Top,
    horizontalAlignment = Alignment.Horizontal.Start
) {
    Text("Menu", modifier = Modifier().color("white").fontSize("1.5rem").marginBottom("20px"))
    
    NavItem("Dashboard", icon = "dashboard")
    NavItem("Profile", icon = "person")
    NavItem("Settings", icon = "settings")
    
    Spacer(modifier = Modifier().weight(1f))
    
    NavItem("Logout", icon = "logout")
}
```

## Combining Row and Column

Create complex layouts by nesting Row and Column components:

```kotlin
@Composable
fun ComplexLayout() {
    Column(
        modifier = Modifier().fillMaxSize(),
        verticalArrangement = Arrangement.Vertical.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier()
                .fillMaxWidth()
                .height("60px")
                .background("#3498db")
                .padding("0", "20px"),
            horizontalArrangement = Arrangement.Horizontal.SpaceBetween,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text("Logo", modifier = Modifier().color("white").fontSize("1.2rem"))
            Row {
                NavLink("Home")
                NavLink("About")
                NavLink("Contact")
            }
        }
        
        // Content
        Row(
            modifier = Modifier()
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.Horizontal.Center,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                Text("Welcome", modifier = Modifier().fontSize("3rem"))
                Text("Build amazing UIs with Summon")
            }
        }
        
        // Footer
        Row(
            modifier = Modifier()
                .fillMaxWidth()
                .height("40px")
                .background("#34495e")
                .padding("0", "20px"),
            horizontalArrangement = Arrangement.Horizontal.Center,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text("© 2024 Summon Framework", modifier = Modifier().color("#95a5a6"))
        }
    }
}
```

## Performance Considerations

1. **Avoid Deep Nesting**: Too many nested Row/Column components can impact performance
2. **Use Spacer**: For fixed spacing, use Spacer instead of empty containers
3. **Lazy Variants**: For large lists, consider LazyColumn or LazyRow

## Migration from Old API

If you were using manual CSS styling for alignment:

```kotlin
// Old way
Row(
    modifier = Modifier()
        .style("justify-content", "center")
        .style("align-items", "center")
) {
    // content
}

// New way
Row(
    horizontalArrangement = Arrangement.Horizontal.Center,
    verticalAlignment = Alignment.Vertical.CenterVertically
) {
    // content
}
```

## API Reference

### Row
```kotlin
@Composable
fun Row(
    modifier: Modifier = Modifier(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Horizontal.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Vertical.Top,
    content: @Composable FlowContent.() -> Unit
)
```

### Column
```kotlin
@Composable
fun Column(
    modifier: Modifier = Modifier(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Vertical.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Horizontal.Start,
    content: @Composable () -> Unit
)
```