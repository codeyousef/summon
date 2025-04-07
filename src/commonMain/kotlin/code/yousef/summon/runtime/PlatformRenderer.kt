package code.yousef.summon.runtime

import code.yousef.summon.core.LocalDate
import code.yousef.summon.core.LocalTime
import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.Modifier

/**
 * Interface for platform-specific renderers.
 * This interface provides methods for rendering various UI components.
 */
interface PlatformRenderer {
    /**
     * Render a composable to the specified consumer
     *
     * @param composable The composable to render
     * @param consumer The consumer to render to
     */
    fun <T> renderComposable(composable: Composable, consumer: T)
    
    // Core components
    fun renderText(text: String, modifier: Modifier, style: Any)
    fun renderBox(modifier: Modifier, content: () -> Unit)
    fun renderColumn(modifier: Modifier, verticalArrangement: Any, horizontalAlignment: Any, content: () -> Unit)
    fun renderRow(modifier: Modifier, horizontalArrangement: Any, verticalAlignment: Any, content: () -> Unit)
    fun renderDivider(modifier: Modifier, color: Color, thickness: Float, vertical: Boolean)
    fun renderLazyColumn(modifier: Modifier, content: () -> Unit)
    fun renderLazyRow(modifier: Modifier, content: () -> Unit)
    fun renderGrid(modifier: Modifier, columns: Int, content: () -> Unit)
    fun renderResponsiveLayout(modifier: Modifier, breakpoints: Map<String, () -> Unit>, content: () -> Unit)
    fun renderAspectRatio(ratio: Float, modifier: Modifier, content: () -> Unit)
    fun renderSpacer(modifier: Modifier)
    
    // Input components
    fun renderTextField(value: String, onValueChange: (String) -> Unit, modifier: Modifier, 
                       placeholder: String, isError: Boolean, type: String)
    fun renderTextArea(value: String, onValueChange: (String) -> Unit, modifier: Modifier, 
                      placeholder: String, maxLines: Int)
    fun renderCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier, 
                      enabled: Boolean)
    fun renderRadioButton(selected: Boolean, onClick: () -> Unit, modifier: Modifier, 
                         enabled: Boolean)
    fun renderSelect(value: String, onValueChange: (String) -> Unit, options: List<String>, 
                    modifier: Modifier, placeholder: String)
    fun renderSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier, 
                    enabled: Boolean)
    fun renderDatePicker(date: LocalDate?, onDateChange: (LocalDate) -> Unit, modifier: Modifier)
    fun renderTimePicker(time: LocalTime?, onTimeChange: (LocalTime) -> Unit, modifier: Modifier, 
                        is24Hour: Boolean)
    fun renderFileUpload(onFileSelected: (List<Any>) -> Unit, modifier: Modifier, multiple: Boolean, 
                        acceptedFileTypes: List<String>)
    fun renderRangeSlider(value: ClosedFloatingPointRange<Float>, onValueChange: (ClosedFloatingPointRange<Float>) -> Unit, 
                         modifier: Modifier, valueRange: ClosedFloatingPointRange<Float>)
    
    // Navigation components
    fun renderLink(url: String, modifier: Modifier, content: () -> Unit)
    fun renderTabLayout(tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit, 
                       modifier: Modifier, content: () -> Unit)
    
    // Feedback components
    fun renderAlertContainer(type: String, isDismissible: Boolean, onDismiss: () -> Unit, 
                           modifier: Modifier, actionText: String?, onAction: (() -> Unit)?, content: () -> Unit)
    fun renderBadge(count: Int, modifier: Modifier, maxCount: Int, content: () -> Unit)
    fun renderProgressIndicator(progress: Float, modifier: Modifier)
    fun renderCircularProgress(progress: Float, modifier: Modifier)
    fun renderTooltipContainer(text: String, placement: String, showArrow: Boolean, 
                              showOnClick: Boolean, showDelay: Int, hideDelay: Int, 
                              modifier: Modifier, content: () -> Unit)
    
    // Animation
    fun renderAnimatedContent(content: () -> Unit, modifier: Modifier)
    fun renderAnimatedVisibility(visible: Boolean, modifier: Modifier, content: () -> Unit)
    fun renderHtmlTag(tag: String, attrs: Map<String, String>, content: () -> Unit)
    fun renderCard(elevation: Float, modifier: Modifier, content: () -> Unit)
} 
