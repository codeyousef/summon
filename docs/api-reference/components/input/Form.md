# Form

Form components provide structured data collection with validation, submission handling, and state management for
complex user inputs.

## Overview

The Form component serves as a container for input fields, managing form state, validation, and submission. It provides
a cohesive framework for building complex forms with proper validation, error handling, and user feedback.

### Key Features

- **State Management**: Centralized form state and field registration
- **Validation System**: Built-in validation with error handling
- **Submission Control**: Managed form submission with data collection
- **Field Integration**: Seamless integration with all input components
- **Accessibility**: Full ARIA support and semantic HTML
- **Error Handling**: Comprehensive error display and management
- **Data Collection**: Automatic form data serialization

## API Reference

### Form

```kotlin
@Composable
fun Form(
    onSubmit: (Map<String, String>) -> Unit = {},
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

**Parameters:**

- `onSubmit`: Callback invoked when form is successfully submitted
- `modifier`: Modifier for styling and layout
- `content`: Form content containing input fields and controls

### FormState

```kotlin
class FormState {
    fun registerField(field: FormField)
    fun validate(): Boolean
    fun submit(onSubmit: (Map<String, String>) -> Unit): Boolean
}
```

**Methods:**

- `registerField()`: Registers a field for validation and submission
- `validate()`: Validates all registered fields
- `submit()`: Submits form if validation passes

### FormField (Interface)

```kotlin
interface FormField {
    val label: String?
    val value: String
    fun validate(): Boolean
}
```

## Usage Examples

### Basic Form

```kotlin
@Composable
fun BasicFormExample() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Form(
        onSubmit = { formData ->
            println("Form submitted with data:")
            formData.forEach { (key, value) ->
                println("$key: $value")
            }
        }
    ) {
        FormField(label = "Contact Information") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name *",
                    validators = listOf(
                        Validator { value ->
                            if (value.isBlank()) {
                                ValidationResult.invalid("Name is required")
                            } else {
                                ValidationResult.valid()
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address *",
                    type = "email",
                    validators = listOf(
                        Validator { value ->
                            if (value.isBlank()) {
                                ValidationResult.invalid("Email is required")
                            } else if (!value.contains("@")) {
                                ValidationResult.invalid("Please enter a valid email")
                            } else {
                                ValidationResult.valid()
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                TextArea(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = "Enter your message...",
                    rows = 4,
                    modifier = Modifier().width(Width.FULL)
                )
            }
        }

        Button(
            text = "Send Message",
            type = ButtonType.SUBMIT,
            enabled = name.isNotBlank() && email.isNotBlank()
        )
    }
}
```

### Registration Form

```kotlin
@Composable
fun RegistrationFormExample() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var newsletter by remember { mutableStateOf(false) }

    Form(
        onSubmit = { formData ->
            println("Registration submitted:")
            println("Name: ${formData["firstName"]} ${formData["lastName"]}")
            println("Email: ${formData["email"]}")
            println("Newsletter: $newsletter")
        }
    ) {
        FormField(label = "Personal Information") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                Row(modifier = Modifier().gap(Spacing.MD)) {
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First Name *",
                        validators = listOf(requiredValidator()),
                        modifier = Modifier().weight(1f)
                    )

                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last Name *",
                        validators = listOf(requiredValidator()),
                        modifier = Modifier().weight(1f)
                    )
                }

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address *",
                    type = "email",
                    validators = listOf(
                        requiredValidator(),
                        emailValidator()
                    ),
                    modifier = Modifier().width(Width.FULL)
                )
            }
        }

        FormField(label = "Security") {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password *",
                    type = "password",
                    validators = listOf(
                        requiredValidator(),
                        passwordStrengthValidator()
                    ),
                    modifier = Modifier().width(Width.FULL)
                )

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password *",
                    type = "password",
                    validators = listOf(
                        requiredValidator(),
                        Validator { value ->
                            if (value == password) {
                                ValidationResult.valid()
                            } else {
                                ValidationResult.invalid("Passwords do not match")
                            }
                        }
                    ),
                    modifier = Modifier().width(Width.FULL)
                )
            }
        }

        FormField(label = "Preferences") {
            Column(modifier = Modifier().gap(Spacing.SM)) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    label = "I agree to the Terms of Service and Privacy Policy *",
                    validators = listOf(
                        Validator { value ->
                            if (value.toBoolean()) {
                                ValidationResult.valid()
                            } else {
                                ValidationResult.invalid("You must agree to the terms")
                            }
                        }
                    )
                )

                Checkbox(
                    checked = newsletter,
                    onCheckedChange = { newsletter = it },
                    label = "Subscribe to newsletter for updates and promotions"
                )
            }
        }

        Button(
            text = "Create Account",
            type = ButtonType.SUBMIT,
            enabled = firstName.isNotBlank() && lastName.isNotBlank() &&
                     email.isNotBlank() && password.isNotBlank() &&
                     confirmPassword == password && agreeTerms,
            modifier = Modifier().width(Width.FULL)
        )
    }
}

private fun requiredValidator() = Validator { value ->
    if (value.isBlank()) {
        ValidationResult.invalid("This field is required")
    } else {
        ValidationResult.valid()
    }
}

private fun emailValidator() = Validator { value ->
    val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
    if (emailRegex.matches(value)) {
        ValidationResult.valid()
    } else {
        ValidationResult.invalid("Please enter a valid email address")
    }
}

private fun passwordStrengthValidator() = Validator { value ->
    when {
        value.length < 8 -> ValidationResult.invalid("Password must be at least 8 characters")
        !value.any { it.isUpperCase() } -> ValidationResult.invalid("Password must contain an uppercase letter")
        !value.any { it.isLowerCase() } -> ValidationResult.invalid("Password must contain a lowercase letter")
        !value.any { it.isDigit() } -> ValidationResult.invalid("Password must contain a number")
        else -> ValidationResult.valid()
    }
}
```

### Multi-Step Form

```kotlin
@Composable
fun MultiStepFormExample() {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 3

    // Step 1: Personal Info
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Step 2: Address
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }

    // Step 3: Payment
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        // Progress indicator
        StepProgressIndicator(currentStep = currentStep, totalSteps = totalSteps)

        Form(
            onSubmit = { formData ->
                when (currentStep) {
                    totalSteps -> {
                        println("Order completed!")
                        println("Personal: $firstName $lastName ($email)")
                        println("Address: $street, $city $zipCode")
                        println("Payment: **** **** **** ${cardNumber.takeLast(4)}")
                    }
                    else -> {
                        currentStep++
                    }
                }
            }
        ) {
            when (currentStep) {
                1 -> PersonalInfoStep(
                    firstName = firstName,
                    onFirstNameChange = { firstName = it },
                    lastName = lastName,
                    onLastNameChange = { lastName = it },
                    email = email,
                    onEmailChange = { email = it }
                )
                2 -> AddressStep(
                    street = street,
                    onStreetChange = { street = it },
                    city = city,
                    onCityChange = { city = it },
                    zipCode = zipCode,
                    onZipCodeChange = { zipCode = it }
                )
                3 -> PaymentStep(
                    cardNumber = cardNumber,
                    onCardNumberChange = { cardNumber = it },
                    expiryDate = expiryDate,
                    onExpiryDateChange = { expiryDate = it },
                    cvv = cvv,
                    onCvvChange = { cvv = it }
                )
            }

            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.SpaceBetween)
                    .marginTop(Spacing.LG)
            ) {
                if (currentStep > 1) {
                    Button(
                        text = "Back",
                        type = ButtonType.SECONDARY,
                        onClick = { currentStep-- }
                    )
                } else {
                    Box {} // Empty space
                }

                Button(
                    text = if (currentStep == totalSteps) "Complete Order" else "Next",
                    type = ButtonType.SUBMIT,
                    enabled = isStepValid(currentStep, firstName, lastName, email, street, city, zipCode, cardNumber, expiryDate, cvv)
                )
            }
        }
    }
}

@Composable
fun StepProgressIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier()
            .width(Width.FULL)
            .justifyContent(JustifyContent.Center)
            .gap(Spacing.MD)
    ) {
        repeat(totalSteps) { index ->
            val stepNumber = index + 1
            val isActive = stepNumber == currentStep
            val isCompleted = stepNumber < currentStep

            Box(
                modifier = Modifier()
                    .width(40.px)
                    .height(40.px)
                    .borderRadius(BorderRadius.FULL)
                    .backgroundColor(
                        when {
                            isCompleted -> Colors.Success.MAIN
                            isActive -> Colors.Primary.MAIN
                            else -> Colors.Gray.LIGHT
                        }
                    )
                    .alignItems(AlignItems.Center)
                    .justifyContent(JustifyContent.Center)
            ) {
                if (isCompleted) {
                    Icon(name = "check", color = Colors.White)
                } else {
                    Text(
                        stepNumber.toString(),
                        style = Typography.BODY2.copy(
                            color = if (isActive) Colors.White else Colors.Gray.MAIN,
                            fontWeight = FontWeight.BOLD
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PersonalInfoStep(
    firstName: String, onFirstNameChange: (String) -> Unit,
    lastName: String, onLastNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit
) {
    FormField(label = "Personal Information") {
        Column(modifier = Modifier().gap(Spacing.MD)) {
            Row(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    label = "First Name *",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().weight(1f)
                )
                TextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    label = "Last Name *",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().weight(1f)
                )
            }
            TextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email Address *",
                type = "email",
                validators = listOf(requiredValidator(), emailValidator()),
                modifier = Modifier().width(Width.FULL)
            )
        }
    }
}

@Composable
fun AddressStep(
    street: String, onStreetChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    zipCode: String, onZipCodeChange: (String) -> Unit
) {
    FormField(label = "Shipping Address") {
        Column(modifier = Modifier().gap(Spacing.MD)) {
            TextField(
                value = street,
                onValueChange = onStreetChange,
                label = "Street Address *",
                validators = listOf(requiredValidator()),
                modifier = Modifier().width(Width.FULL)
            )
            Row(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = city,
                    onValueChange = onCityChange,
                    label = "City *",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().weight(1f)
                )
                TextField(
                    value = zipCode,
                    onValueChange = onZipCodeChange,
                    label = "ZIP Code *",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().width(120.px)
                )
            }
        }
    }
}

@Composable
fun PaymentStep(
    cardNumber: String, onCardNumberChange: (String) -> Unit,
    expiryDate: String, onExpiryDateChange: (String) -> Unit,
    cvv: String, onCvvChange: (String) -> Unit
) {
    FormField(label = "Payment Information") {
        Column(modifier = Modifier().gap(Spacing.MD)) {
            TextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                label = "Card Number *",
                placeholder = "1234 5678 9012 3456",
                validators = listOf(requiredValidator()),
                modifier = Modifier().width(Width.FULL)
            )
            Row(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = expiryDate,
                    onValueChange = onExpiryDateChange,
                    label = "Expiry Date *",
                    placeholder = "MM/YY",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().weight(1f)
                )
                TextField(
                    value = cvv,
                    onValueChange = onCvvChange,
                    label = "CVV *",
                    placeholder = "123",
                    type = "password",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().width(100.px)
                )
            }
        }
    }
}

private fun isStepValid(
    step: Int,
    firstName: String, lastName: String, email: String,
    street: String, city: String, zipCode: String,
    cardNumber: String, expiryDate: String, cvv: String
): Boolean {
    return when (step) {
        1 -> firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()
        2 -> street.isNotBlank() && city.isNotBlank() && zipCode.isNotBlank()
        3 -> cardNumber.isNotBlank() && expiryDate.isNotBlank() && cvv.isNotBlank()
        else -> false
    }
}
```

### Survey Form

```kotlin
@Composable
fun SurveyFormExample() {
    var name by remember { mutableStateOf("") }
    var satisfaction by remember { mutableStateOf<String?>(null) }
    var features by remember { mutableStateOf(setOf<String>()) }
    var recommendation by remember { mutableStateOf(5f) }
    var feedback by remember { mutableStateOf("") }

    Form(
        onSubmit = { formData ->
            println("Survey submitted:")
            println("Name: $name")
            println("Satisfaction: $satisfaction")
            println("Features: ${features.joinToString()}")
            println("Recommendation: ${recommendation.toInt()}/10")
            println("Feedback: $feedback")
        }
    ) {
        FormField(label = "Customer Satisfaction Survey") {
            Column(modifier = Modifier().gap(Spacing.LG)) {
                // Personal info
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name (Optional)",
                    modifier = Modifier().width(300.px)
                )

                // Satisfaction rating
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Overall Satisfaction *", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    val satisfactionOptions = listOf("Very Dissatisfied", "Dissatisfied", "Neutral", "Satisfied", "Very Satisfied")
                    satisfactionOptions.forEach { option ->
                        RadioButton(
                            selected = satisfaction == option,
                            onClick = { satisfaction = option },
                            label = option
                        )
                    }
                }

                // Feature selection
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Which features do you use most? (Select all that apply)", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    val featureOptions = listOf("Dashboard", "Reports", "Analytics", "Notifications", "Mobile App", "API")
                    featureOptions.forEach { feature ->
                        Checkbox(
                            checked = feature in features,
                            onCheckedChange = { isChecked ->
                                features = if (isChecked) {
                                    features + feature
                                } else {
                                    features - feature
                                }
                            },
                            label = feature
                        )
                    }
                }

                // Recommendation slider
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .justifyContent(JustifyContent.SpaceBetween)
                            .alignItems(AlignItems.Center)
                    ) {
                        Text("How likely are you to recommend us?", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Text("${recommendation.toInt()}/10", style = Typography.BODY2.copy(fontWeight = FontWeight.BOLD))
                    }

                    Slider(
                        value = recommendation,
                        onValueChange = { recommendation = it },
                        valueRange = 0f..10f,
                        steps = 9, // 11 values: 0-10
                        modifier = Modifier().width(400.px)
                    )

                    Row(
                        modifier = Modifier()
                            .width(400.px)
                            .justifyContent(JustifyContent.SpaceBetween)
                    ) {
                        Text("Not at all likely", style = Typography.CAPTION)
                        Text("Extremely likely", style = Typography.CAPTION)
                    }
                }

                // Additional feedback
                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Additional Comments", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    TextArea(
                        value = feedback,
                        onValueChange = { feedback = it },
                        placeholder = "Please share any additional feedback or suggestions...",
                        rows = 4,
                        modifier = Modifier().width(500.px)
                    )
                }
            }
        }

        Button(
            text = "Submit Survey",
            type = ButtonType.SUBMIT,
            enabled = satisfaction != null,
            modifier = Modifier().marginTop(Spacing.LG)
        )
    }
}
```

### Dynamic Form

```kotlin
@Composable
fun DynamicFormExample() {
    var formFields by remember { mutableStateOf(listOf<DynamicField>()) }
    var formTitle by remember { mutableStateOf("") }

    Column(modifier = Modifier().gap(Spacing.LG)) {
        Text("Dynamic Form Builder", style = Typography.H5)

        // Form builder controls
        Card(
            modifier = Modifier()
                .width(Width.FULL)
                .backgroundColor(Colors.Gray.LIGHT)
                .padding(Spacing.MD)
        ) {
            Column(modifier = Modifier().gap(Spacing.MD)) {
                TextField(
                    value = formTitle,
                    onValueChange = { formTitle = it },
                    label = "Form Title",
                    modifier = Modifier().width(300.px)
                )

                Row(modifier = Modifier().gap(Spacing.SM)) {
                    Button(
                        text = "Add Text Field",
                        type = ButtonType.OUTLINE,
                        onClick = {
                            formFields = formFields + DynamicField(
                                id = "field_${formFields.size}",
                                type = FieldType.TEXT,
                                label = "Text Field ${formFields.size + 1}",
                                value = ""
                            )
                        }
                    )

                    Button(
                        text = "Add Checkbox",
                        type = ButtonType.OUTLINE,
                        onClick = {
                            formFields = formFields + DynamicField(
                                id = "field_${formFields.size}",
                                type = FieldType.CHECKBOX,
                                label = "Checkbox ${formFields.size + 1}",
                                value = "false"
                            )
                        }
                    )

                    Button(
                        text = "Add Select",
                        type = ButtonType.OUTLINE,
                        onClick = {
                            formFields = formFields + DynamicField(
                                id = "field_${formFields.size}",
                                type = FieldType.SELECT,
                                label = "Select ${formFields.size + 1}",
                                value = "",
                                options = listOf("Option 1", "Option 2", "Option 3")
                            )
                        }
                    )
                }
            }
        }

        // Dynamic form
        if (formFields.isNotEmpty()) {
            Form(
                onSubmit = { formData ->
                    println("Dynamic form submitted:")
                    formData.forEach { (key, value) ->
                        println("$key: $value")
                    }
                }
            ) {
                FormField(label = formTitle.ifBlank { "Dynamic Form" }) {
                    Column(modifier = Modifier().gap(Spacing.MD)) {
                        formFields.forEachIndexed { index, field ->
                            DynamicFieldRenderer(
                                field = field,
                                onValueChange = { newValue ->
                                    formFields = formFields.mapIndexed { i, f ->
                                        if (i == index) f.copy(value = newValue) else f
                                    }
                                },
                                onRemove = {
                                    formFields = formFields.filterIndexed { i, _ -> i != index }
                                }
                            )
                        }
                    }
                }

                Button(
                    text = "Submit Form",
                    type = ButtonType.SUBMIT,
                    modifier = Modifier().marginTop(Spacing.MD)
                )
            }
        }
    }
}

data class DynamicField(
    val id: String,
    val type: FieldType,
    val label: String,
    val value: String,
    val options: List<String> = emptyList(),
    val required: Boolean = false
)

enum class FieldType {
    TEXT, CHECKBOX, SELECT, TEXTAREA
}

@Composable
fun DynamicFieldRenderer(
    field: DynamicField,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier()
            .width(Width.FULL)
            .alignItems(AlignItems.Start)
            .gap(Spacing.MD)
    ) {
        Column(modifier = Modifier().weight(1f)) {
            when (field.type) {
                FieldType.TEXT -> {
                    TextField(
                        value = field.value,
                        onValueChange = onValueChange,
                        label = field.label,
                        modifier = Modifier().width(Width.FULL)
                    )
                }
                FieldType.CHECKBOX -> {
                    Checkbox(
                        checked = field.value.toBoolean(),
                        onCheckedChange = { onValueChange(it.toString()) },
                        label = field.label
                    )
                }
                FieldType.SELECT -> {
                    Column(modifier = Modifier().gap(Spacing.SM)) {
                        Text(field.label, style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        Select(
                            selectedValue = remember { mutableStateOf(field.value) }.apply { value = field.value },
                            options = field.options.map { SelectOption(it, it) },
                            onSelectedChange = { onValueChange(it ?: "") },
                            placeholder = "Select an option",
                            modifier = Modifier().width(Width.FULL)
                        )
                    }
                }
                FieldType.TEXTAREA -> {
                    Column(modifier = Modifier().gap(Spacing.SM)) {
                        Text(field.label, style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                        TextArea(
                            value = field.value,
                            onValueChange = onValueChange,
                            rows = 3,
                            modifier = Modifier().width(Width.FULL)
                        )
                    }
                }
            }
        }

        Button(
            text = "×",
            size = ButtonSize.SMALL,
            type = ButtonType.OUTLINE,
            onClick = onRemove,
            modifier = Modifier().minWidth(32.px)
        )
    }
}
```

### Form with File Upload

```kotlin
@Composable
fun FormWithFileUploadExample() {
    var applicantName by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var coverLetter by remember { mutableStateOf("") }
    var resume by remember { mutableStateOf<FileInfo?>(null) }
    var portfolio by remember { mutableStateOf<List<FileInfo>>(emptyList()) }

    Form(
        onSubmit = { formData ->
            println("Job application submitted:")
            println("Name: $applicantName")
            println("Position: $position")
            println("Resume: ${resume?.name}")
            println("Portfolio: ${portfolio.map { it.name }}")
        }
    ) {
        FormField(label = "Job Application") {
            Column(modifier = Modifier().gap(Spacing.LG)) {
                TextField(
                    value = applicantName,
                    onValueChange = { applicantName = it },
                    label = "Full Name *",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().width(Width.FULL)
                )

                TextField(
                    value = position,
                    onValueChange = { position = it },
                    label = "Position Applied For *",
                    validators = listOf(requiredValidator()),
                    modifier = Modifier().width(Width.FULL)
                )

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Cover Letter *", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    TextArea(
                        value = coverLetter,
                        onValueChange = { coverLetter = it },
                        placeholder = "Tell us why you're interested in this position...",
                        rows = 5,
                        maxLength = 1000,
                        modifier = Modifier().width(Width.FULL)
                    )
                    Text("${coverLetter.length}/1000", style = Typography.CAPTION)
                }

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Resume *", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    FileUpload(
                        onFilesSelected = { files ->
                            resume = files.firstOrNull()
                        },
                        accept = ".pdf,.doc,.docx",
                        buttonLabel = "Upload Resume"
                    )
                    resume?.let { file ->
                        Text(
                            "Selected: ${file.name} (${formatFileSize(file.size)})",
                            style = Typography.CAPTION.copy(color = Colors.Success.MAIN)
                        )
                    }
                }

                Column(modifier = Modifier().gap(Spacing.SM)) {
                    Text("Portfolio (Optional)", style = Typography.BODY1.copy(fontWeight = FontWeight.MEDIUM))
                    FileUpload(
                        onFilesSelected = { files ->
                            portfolio = files
                        },
                        multiple = true,
                        accept = ".pdf,.doc,.docx,image/*",
                        buttonLabel = "Upload Portfolio"
                    )
                    if (portfolio.isNotEmpty()) {
                        Text(
                            "${portfolio.size} file${if (portfolio.size != 1) "s" else ""} selected",
                            style = Typography.CAPTION.copy(color = Colors.Info.MAIN)
                        )
                    }
                }
            }
        }

        Button(
            text = "Submit Application",
            type = ButtonType.SUBMIT,
            enabled = applicantName.isNotBlank() && position.isNotBlank() &&
                     coverLetter.isNotBlank() && resume != null,
            modifier = Modifier().width(Width.FULL)
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
```

## Form Validation

### Built-in Validators

```kotlin
object FormValidators {
    fun required() = Validator { value ->
        if (value.isBlank()) {
            ValidationResult.invalid("This field is required")
        } else {
            ValidationResult.valid()
        }
    }

    fun email() = Validator { value ->
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        if (value.isEmpty() || emailRegex.matches(value)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Please enter a valid email address")
        }
    }

    fun minLength(min: Int) = Validator { value ->
        if (value.length >= min) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Must be at least $min characters")
        }
    }

    fun maxLength(max: Int) = Validator { value ->
        if (value.length <= max) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Must be no more than $max characters")
        }
    }

    fun pattern(regex: Regex, message: String) = Validator { value ->
        if (value.isEmpty() || regex.matches(value)) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid(message)
        }
    }

    fun phone() = Validator { value ->
        val phoneRegex = "^[+]?[1-9]?[0-9]{7,15}$".toRegex()
        if (value.isEmpty() || phoneRegex.matches(value.replace(Regex("[\\s\\-\\(\\)]"), ""))) {
            ValidationResult.valid()
        } else {
            ValidationResult.invalid("Please enter a valid phone number")
        }
    }
}
```

## Accessibility Features

### ARIA Support

The Form component automatically includes:

- `role="form"` for semantic form structure
- Proper label associations with `aria-labelledby`
- Error message associations with `aria-describedby`
- Required field indicators with `aria-required`
- Invalid field marking with `aria-invalid`

### Keyboard Navigation

- **Tab**: Navigate between form fields
- **Shift+Tab**: Navigate backwards
- **Enter**: Submit form (when focused on submit button)
- **Escape**: Clear focus from current field

### Screen Reader Support

```kotlin
@Composable
fun AccessibleFormExample() {
    var name by remember { mutableStateOf("") }

    Form(
        modifier = Modifier()
            .accessibilityLabel("Contact form")
            .accessibilityRole("form")
    ) {
        FormField(
            label = "Contact Information",
            modifier = Modifier()
                .accessibilityLabel("Contact information section")
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                modifier = Modifier()
                    .accessibilityLabel("Enter your full name")
                    .accessibilityHint("Required field for contact form")
            )
        }
    }
}
```

## Platform Differences

### Browser (JS)

- Uses HTML `<form>` element
- Native form submission handling
- Browser validation integration
- FormData API support

### JVM

- Server-side form processing
- Generates appropriate HTML for SSR
- Server-side validation
- Request parameter handling

## Testing Strategies

### Unit Testing

```kotlin
class FormTest {
    @Test
    fun `form validates all fields before submission`() {
        var submittedData: Map<String, String>? = null

        composeTestRule.setContent {
            var name by remember { mutableStateOf("") }

            Form(
                onSubmit = { submittedData = it }
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    validators = listOf(requiredValidator())
                )
                Button(text = "Submit", type = ButtonType.SUBMIT)
            }
        }

        // Submit empty form - should not submit
        composeTestRule.onNodeWithText("Submit").performClick()
        assertNull(submittedData)

        // Fill form and submit - should submit
        composeTestRule.onNodeWithContentDescription("text field")
            .performTextInput("John Doe")
        composeTestRule.onNodeWithText("Submit").performClick()
        assertNotNull(submittedData)
    }
}
```

## Migration Guide

### From HTML Forms

```html
<!-- Before: HTML -->
<form onsubmit="handleSubmit(event)">
    <label for="name">Name:</label>
    <input type="text" id="name" name="name" required>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required>

    <button type="submit">Submit</button>
</form>
```

```kotlin
// After: Summon
@Composable
fun ContactForm() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Form(
        onSubmit = { formData ->
            handleSubmit(formData)
        }
    ) {
        FormField {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                validators = listOf(requiredValidator())
            )
        }

        FormField {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                type = "email",
                validators = listOf(requiredValidator(), emailValidator())
            )
        }

        Button(text = "Submit", type = ButtonType.SUBMIT)
    }
}
```

## Best Practices

### Do

- Always validate user input
- Provide clear error messages
- Use appropriate input types
- Group related fields logically
- Show required field indicators
- Provide submission feedback

### Don't

- Submit forms without validation
- Use generic error messages
- Make forms unnecessarily complex
- Forget accessibility considerations
- Skip proper field labeling

## Related Components

- [FormField](FormField.md) - For field structure
- [TextField](TextField.md) - For text input
- [Button](Button.md) - For form actions
- [Select](Select.md) - For dropdown selection
- [Checkbox](Checkbox.md) - For multiple choices