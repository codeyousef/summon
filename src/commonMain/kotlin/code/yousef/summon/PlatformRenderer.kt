package code.yousef.summon

import code.yousef.summon.animation.AnimatedContent
import code.yousef.summon.animation.AnimatedVisibility
import code.yousef.summon.routing.Router
import kotlinx.html.TagConsumer

/**
 * Platform-specific renderer interface that serves as a common abstraction
 * for rendering UI components across platforms.
 *
 * Following section 8 of the guide: "Exploring Common Intermediate Representations or Abstraction Layers"
 */
interface PlatformRenderer {
    /**
     * Renders a Text component to the appropriate platform output.
     */
    fun <T> renderText(text: Text, consumer: TagConsumer<T>): T

    /**
     * Renders a Button component to the appropriate platform output.
     */
    fun <T> renderButton(button: Button, consumer: TagConsumer<T>): T

    /**
     * Renders a Row component to the appropriate platform output.
     */
    fun <T> renderRow(row: Row, consumer: TagConsumer<T>): T

    /**
     * Renders a Column component to the appropriate platform output.
     */
    fun <T> renderColumn(column: Column, consumer: TagConsumer<T>): T

    /**
     * Renders a Spacer component to the appropriate platform output.
     */
    fun <T> renderSpacer(spacer: Spacer, consumer: TagConsumer<T>): T

    /**
     * Renders a TextField component to the appropriate platform output.
     */
    fun <T> renderTextField(textField: TextField, consumer: TagConsumer<T>): T

    /**
     * Renders a TextArea component to the appropriate platform output.
     */
    fun <T> renderTextArea(textArea: TextArea, consumer: TagConsumer<T>): T

    /**
     * Renders a Checkbox component to the appropriate platform output.
     */
    fun <T> renderCheckbox(checkbox: Checkbox, consumer: TagConsumer<T>): T

    /**
     * Renders a RadioButton component to the appropriate platform output.
     */
    fun <T> renderRadioButton(radioButton: RadioButton<Any>, consumer: TagConsumer<T>): T

    /**
     * Renders a Select component to the appropriate platform output.
     */
    fun <T> renderSelect(select: Select<Any>, consumer: TagConsumer<T>): T

    /**
     * Renders a FormField component to the appropriate platform output.
     */
    fun <T> renderFormField(formField: FormField, consumer: TagConsumer<T>): T

    /**
     * Renders a Switch component to the appropriate platform output.
     */
    fun <T> renderSwitch(switch: Switch, consumer: TagConsumer<T>): T

    /**
     * Renders a FileUpload component to the appropriate platform output.
     */
    fun <T> renderFileUpload(fileUpload: FileUpload, consumer: TagConsumer<T>): T

    /**
     * Renders a RangeSlider component to the appropriate platform output.
     */
    fun <T> renderRangeSlider(rangeSlider: RangeSlider, consumer: TagConsumer<T>): T

    /**
     * Renders a Form component to the appropriate platform output.
     */
    fun <T> renderForm(form: Form, consumer: TagConsumer<T>): T

    /**
     * Renders a Card component to the appropriate platform output.
     */
    fun <T> renderCard(card: Card, consumer: TagConsumer<T>): T

    /**
     * Renders a Router component to the appropriate platform output.
     */
    fun <T> renderRouter(router: Router, consumer: TagConsumer<T>): T

    /**
     * Renders an Image component to the appropriate platform output.
     */
    fun <T> renderImage(image: Image, consumer: TagConsumer<T>): T

    /**
     * Renders a Divider component to the appropriate platform output.
     */
    fun <T> renderDivider(divider: Divider, consumer: TagConsumer<T>): T

    /**
     * Renders a Link component to the appropriate platform output.
     */
    fun <T> renderLink(link: Link, consumer: TagConsumer<T>): T

    /**
     * Renders a 404 Not Found page.
     */
    fun <T> renderNotFound(consumer: TagConsumer<T>): T

    /**
     * Renders an Icon component to the appropriate platform output.
     */
    fun <T> renderIcon(icon: Icon, consumer: TagConsumer<T>): T

    /**
     * Renders an Alert component to the appropriate platform output.
     */
    fun <T> renderAlert(alert: Alert, consumer: TagConsumer<T>): T

    /**
     * Renders a Badge component to the appropriate platform output.
     */
    fun <T> renderBadge(badge: Badge, consumer: TagConsumer<T>): T

    /**
     * Renders a Tooltip component to the appropriate platform output.
     */
    fun <T> renderTooltip(tooltip: Tooltip, consumer: TagConsumer<T>): T

    /**
     * Renders a Progress component to the appropriate platform output.
     */
    fun <T> renderProgress(progress: Progress, consumer: TagConsumer<T>): T

    /**
     * Renders a Box component to the appropriate platform output.
     */
    fun <T> renderBox(box: Box, consumer: TagConsumer<T>): T

    /**
     * Renders a Grid component to the appropriate platform output.
     */
    fun <T> renderGrid(grid: Grid, consumer: TagConsumer<T>): T

    /**
     * Renders an AspectRatio component to the appropriate platform output.
     */
    fun <T> renderAspectRatio(aspectRatio: AspectRatio, consumer: TagConsumer<T>): T

    /**
     * Renders a ResponsiveLayout component to the appropriate platform output.
     */
    fun <T> renderResponsiveLayout(responsiveLayout: ResponsiveLayout, consumer: TagConsumer<T>): T

    /**
     * Renders a LazyColumn component to the appropriate platform output.
     */
    fun <T> renderLazyColumn(lazyColumn: LazyColumn<*>, consumer: TagConsumer<T>): T

    /**
     * Renders a LazyRow component to the appropriate platform output.
     */
    fun <T> renderLazyRow(lazyRow: LazyRow<*>, consumer: TagConsumer<T>): T

    /**
     * Renders a TabLayout component to the appropriate platform output.
     */
    fun <T> renderTabLayout(tabLayout: TabLayout, consumer: TagConsumer<T>): T

    /**
     * Renders an ExpansionPanel component to the appropriate platform output.
     */
    fun <T> renderExpansionPanel(expansionPanel: ExpansionPanel, consumer: TagConsumer<T>): T

    /**
     * Renders a DatePicker component to the appropriate platform output.
     */
    fun <T> renderDatePicker(datePicker: DatePicker, consumer: TagConsumer<T>): T

    /**
     * Renders a TimePicker component to the appropriate platform output.
     */
    fun <T> renderTimePicker(timePicker: TimePicker, consumer: TagConsumer<T>): T

    /**
     * Renders an AnimatedVisibility component to the appropriate platform output.
     */
    fun <T> renderAnimatedVisibility(animatedVisibility: AnimatedVisibility, consumer: TagConsumer<T>): T

    /**
     * Renders an AnimatedContent component to the appropriate platform output.
     */
    fun <T> renderAnimatedContent(animatedContent: AnimatedContent<*>, consumer: TagConsumer<T>): T
} 