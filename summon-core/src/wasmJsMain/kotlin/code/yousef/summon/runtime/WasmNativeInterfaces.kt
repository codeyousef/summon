package code.yousef.summon.runtime

/**
 * Type-safe external interfaces for WASM DOM manipulation.
 *
 * These interfaces replace the use of 'dynamic' type which is not supported in WASM.
 * All external functions must use only primitive types or external interfaces.
 */

/**
 * Represents a native DOM element in the WASM context.
 * This is a strongly-typed replacement for dynamic element references.
 */
external interface NativeElement

/**
 * Represents a native DOM event in the WASM context.
 * This is a strongly-typed replacement for dynamic event references.
 */
external interface NativeEvent

/**
 * Utility functions for converting between native types and identifiers.
 * These functions bridge the gap between WASM's type restrictions and JavaScript's flexibility.
 */

// Element creation and basic manipulation
external fun wasmCreateElementById(tagName: String): String  // Returns element ID
external fun wasmGetElementById(id: String): String?         // Returns element ID or null
external fun wasmSetElementAttribute(elementId: String, name: String, value: String): Boolean
external fun wasmGetElementAttribute(elementId: String, name: String): String?
external fun wasmRemoveElementAttribute(elementId: String, name: String): Boolean

// Text content manipulation
external fun wasmSetElementTextContent(elementId: String, text: String): Boolean
external fun wasmGetElementTextContent(elementId: String): String?
external fun wasmSetElementInnerHTML(elementId: String, html: String): Boolean
external fun wasmGetElementInnerHTML(elementId: String): String?

// DOM tree manipulation
external fun wasmAppendChildById(parentId: String, childId: String): Boolean
external fun wasmRemoveChildById(parentId: String, childId: String): Boolean
external fun wasmRemoveElementById(elementId: String): Boolean

// CSS class manipulation
external fun wasmAddClassToElement(elementId: String, className: String): Boolean
external fun wasmRemoveClassFromElement(elementId: String, className: String): Boolean
external fun wasmElementHasClass(elementId: String, className: String): Boolean
external fun wasmGetElementClassName(elementId: String): String?

// Element properties
external fun wasmGetElementTagName(elementId: String): String?
external fun wasmGetElementId(elementId: String): String?
external fun wasmSetElementId(elementId: String, newId: String): Boolean

// Element hierarchy
external fun wasmGetElementParentId(elementId: String): String?
external fun wasmGetElementChildren(elementId: String): String // Returns comma-separated IDs

// Event handling
external fun wasmAddEventListenerById(elementId: String, eventType: String, handlerId: String): Boolean
external fun wasmRemoveEventListenerById(elementId: String, eventType: String, handlerId: String): Boolean

// Event properties (when event occurs, these get the event data by handler ID)
external fun wasmGetEventType(handlerId: String): String?
external fun wasmGetEventTargetId(handlerId: String): String?
external fun wasmGetEventValue(handlerId: String): String?
external fun wasmPreventEventDefault(handlerId: String): Boolean
external fun wasmStopEventPropagation(handlerId: String): Boolean

// Element queries
external fun wasmQuerySelectorGetId(selector: String): String?
external fun wasmQuerySelectorAllGetIds(selector: String): String // Returns comma-separated IDs

// Form element specifics
external fun wasmGetElementValue(elementId: String): String?
external fun wasmSetElementValue(elementId: String, value: String): Boolean
external fun wasmGetElementChecked(elementId: String): Boolean
external fun wasmSetElementChecked(elementId: String, checked: Boolean): Boolean
external fun wasmGetElementDisabled(elementId: String): Boolean
external fun wasmSetElementDisabled(elementId: String, disabled: Boolean): Boolean

// Select element specifics
external fun wasmGetSelectedIndex(elementId: String): Int
external fun wasmSetSelectedIndex(elementId: String, index: Int): Boolean

// Document operations
external fun wasmGetDocumentBodyId(): String?
external fun wasmGetDocumentHeadId(): String?
external fun wasmInsertHTMLIntoHead(html: String): Boolean

// Browser capabilities
external fun wasmGetUserAgent(): String?
external fun wasmGetLocationHref(): String?
external fun wasmGetScreenWidth(): Int
external fun wasmGetScreenHeight(): Int
external fun wasmGetDevicePixelRatio(): Double
external fun wasmGetColorDepth(): Int

// Feature detection
external fun wasmHasTouchSupport(): Boolean
external fun wasmHasWebWorkers(): Boolean
external fun wasmHasModuleSupport(): Boolean
external fun wasmHasDynamicImport(): Boolean
external fun wasmHasWasmSIMD(): Boolean
external fun wasmHasWasmThreads(): Boolean
external fun wasmHasWebGL(): Boolean
external fun wasmHasWebGL2(): Boolean

// User agent testing
external fun wasmTestMobileUserAgent(userAgent: String): Boolean
external fun wasmTestTabletUserAgent(userAgent: String): Boolean

// Style operations
external fun wasmScrollElementIntoView(elementId: String, behavior: String): Boolean
external fun wasmGetComputedStyleProperty(elementId: String, property: String): String?

// Performance
external fun wasmPerformanceNow(): Double

// Memory and batch operations
external fun wasmStartBatch(): Boolean
external fun wasmEndBatch(): Boolean

// Console logging (WASM-compatible)
external fun wasmConsoleLog(message: String)
external fun wasmConsoleWarn(message: String)
external fun wasmConsoleError(message: String)
external fun wasmConsoleDebug(message: String)

// Additional missing functions referenced in other files
external fun wasmGetDocumentBody(): String?
external fun wasmIsNotNull(value: String?): Boolean
external fun wasmGetEventTargetValue(handlerId: String): String?
external fun wasmGetDocumentHead(): String?
external fun wasmInsertAdjacentHTML(elementId: String, position: String, html: String): Boolean
external fun wasmGetOuterHTML(elementId: String): String?
external fun wasmSetInnerHTML(elementId: String, html: String): Boolean

// Additional external functions for WASM DOM manipulation
external fun wasmApplyStyleProperty(elementId: String, property: String, value: String): Boolean
external fun wasmGetElementStyle(elementId: String, property: String): String?
external fun wasmSetElementStyle(elementId: String, cssText: String): Boolean
external fun wasmAddEventHandler(elementId: String, eventType: String, handlerId: String): Boolean
external fun wasmRemoveEventHandler(elementId: String, eventType: String, handlerId: String): Boolean

// Element creation with specific options
external fun wasmCreateElementWithOptions(tagName: String, options: String): String // Returns element ID
external fun wasmCloneElement(sourceElementId: String, deep: Boolean): String? // Returns cloned element ID

// Advanced DOM queries
external fun wasmFindElementsBySelector(selector: String): String // Returns comma-separated IDs
external fun wasmGetElementsByTagName(tagName: String): String // Returns comma-separated IDs
external fun wasmGetElementsByClassName(className: String): String // Returns comma-separated IDs

// Element visibility and positioning
external fun wasmIsElementVisible(elementId: String): Boolean
external fun wasmGetElementPosition(elementId: String): String // Returns "x,y,width,height"
external fun wasmSetElementPosition(elementId: String, x: Double, y: Double, width: Double, height: Double): Boolean

// Advanced element operations
external fun wasmReplaceElement(oldElementId: String, newElementId: String): Boolean
external fun wasmMoveElement(elementId: String, newParentId: String, beforeElementId: String?): Boolean

// WASM-specific performance and debugging
external fun wasmStartPerformanceMeasure(measureName: String): Boolean
external fun wasmEndPerformanceMeasure(measureName: String): Double
external fun wasmLogElementTree(rootElementId: String): Boolean
external fun wasmValidateElementId(elementId: String): Boolean