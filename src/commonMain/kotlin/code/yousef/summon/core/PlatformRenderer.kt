package code.yousef.summon.core

import code.yousef.summon.animation.AnimatedContentData
import code.yousef.summon.animation.AnimatedVisibilityData
import code.yousef.summon.components.display.IconData
import code.yousef.summon.components.display.ImageData
import code.yousef.summon.components.display.TextData
import code.yousef.summon.components.feedback.Alert
import code.yousef.summon.components.feedback.Badge
import code.yousef.summon.components.feedback.Progress
import code.yousef.summon.components.feedback.Tooltip
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.navigation.LinkData
import code.yousef.summon.components.navigation.TabLayoutData
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
    fun <T> renderText(textData: TextData, consumer: TagConsumer<T>): T

    /**
     * Renders a Button component to the appropriate platform output.
     */
    fun <T> renderButton(buttonData: ButtonData, consumer: TagConsumer<T>): T

    /**
     * Renders a Spacer component to the appropriate platform output.
     */
    fun <T> renderSpacer(spacerData: SpacerData, consumer: TagConsumer<T>): T

    /**
     * Renders a TextField component to the appropriate platform output.
     */
    fun <T> renderTextField(textFieldData: TextFieldData, consumer: TagConsumer<T>): T

    /**
     * Renders a TextArea component to the appropriate platform output.
     */
    fun <T> renderTextArea(textArea: TextAreaData, consumer: TagConsumer<T>): T

    /**
     * Renders a Checkbox component to the appropriate platform output.
     */
    fun <T> renderCheckbox(checkbox: CheckboxData, consumer: TagConsumer<T>): T

    /**
     * Renders a RadioButton component to the appropriate platform output.
     */
    fun <T> renderRadioButton(radioButton: RadioButtonData, consumer: TagConsumer<T>): T

    /**
     * Renders a Select component to the appropriate platform output.
     */
    fun <T> renderSelect(select: SelectData<Any>, consumer: TagConsumer<T>): T

    /**
     * Renders a Switch component to the appropriate platform output.
     */
    fun <T> renderSwitch(switchData: SwitchData, consumer: TagConsumer<T>): T

    /**
     * Renders a FileUpload component to the appropriate platform output.
     */
    fun <T> renderFileUpload(fileUpload: FileUpload, consumer: TagConsumer<T>): T

    /**
     * Renders a RangeSlider component to the appropriate platform output.
     */
    fun <T> renderSlider(sliderData: SliderData, consumer: TagConsumer<T>): T

    /**
     * Renders a Card component to the appropriate platform output.
     */
    fun <T> renderForm(formData: FormData, consumer: TagConsumer<T>): T

    /**
     * Renders a Card component to the appropriate platform output.
     */
    fun <T> renderCard(cardData: CardData, consumer: TagConsumer<T>): T

    /**
     * Renders a Router component to the appropriate platform output.
     */
    fun <T> renderRouter(router: Router, consumer: TagConsumer<T>): T

    /**
     * Renders an Image component to the appropriate platform output.
     */
    fun <T> renderImage(imageData: ImageData, consumer: TagConsumer<T>): T

    /**
     * Renders a Divider component to the appropriate platform output.
     */
    fun <T> renderDivider(dividerData: DividerData, consumer: TagConsumer<T>): T

    /**
     * Renders a Link component to the appropriate platform output.
     */
    fun <T> renderLink(linkData: LinkData, consumer: TagConsumer<T>): T

    /**
     * Renders a 404 Not Found page.
     */
    fun <T> renderNotFound(consumer: TagConsumer<T>): T

    /**
     * Renders an Icon component to the appropriate platform output.
     */
    fun <T> renderIcon(iconData: IconData, consumer: TagConsumer<T>): T

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
    fun <T> renderBox(boxData: BoxData, consumer: TagConsumer<T>): T

    /**
     * Renders a Grid component to the appropriate platform output.
     */
    fun <T> renderGrid(gridData: GridData, consumer: TagConsumer<T>): T

    /**
     * Renders an AspectRatio component to the appropriate platform output.
     */
    fun <T> renderAspectRatio(aspectRatioData: AspectRatioData, consumer: TagConsumer<T>): T

    /**
     * Renders a ResponsiveLayout component to the appropriate platform output.
     */
    fun <T> renderResponsiveLayout(responsiveLayoutData: ResponsiveLayoutData, consumer: TagConsumer<T>): T

    /**
     * Renders a LazyColumn component to the appropriate platform output.
     */
    fun <T> renderLazyColumn(lazyColumnData: LazyColumnData<*>, consumer: TagConsumer<T>): T

    /**
     * Renders a LazyRow component to the appropriate platform output.
     */
    fun <T> renderLazyRow(lazyRowData: LazyRowData<*>, consumer: TagConsumer<T>): T

    /**
     * Renders a TabLayout component to the appropriate platform output.
     */
    fun <T> renderTabLayout(tabLayoutData: TabLayoutData, consumer: TagConsumer<T>): T

    /**
     * Renders an ExpansionPanel component to the appropriate platform output.
     */
    fun <T> renderExpansionPanel(expansionPanelData: ExpansionPanelData, consumer: TagConsumer<T>): T

    /**
     * Renders a DatePicker component to the appropriate platform output.
     */
    fun <T> renderDatePicker(datePickerData: DatePickerData, consumer: TagConsumer<T>): T

    /**
     * Renders a TimePicker component to the appropriate platform output.
     */
    fun <T> renderTimePicker(timePickerData: TimePickerData, consumer: TagConsumer<T>): T

    /**
     * Renders an AnimatedVisibility component to the appropriate platform output.
     */
    fun <T> renderAnimatedVisibility(animatedVisibilityData: AnimatedVisibilityData, consumer: TagConsumer<T>): T

    /**
     * Renders an AnimatedContent component to the appropriate platform output.
     */
    fun <T> renderAnimatedContent(animatedContentData: AnimatedContentData<*>, consumer: TagConsumer<T>): T

    /**
     * Renders a Div component to the appropriate platform output.
     */
    fun <T> renderDiv(divData: DivData, consumer: TagConsumer<T>): T

    /**
     * Renders a Span component to the appropriate platform output.
     */
    fun <T> renderSpan(spanData: SpanData, consumer: TagConsumer<T>): T
} 