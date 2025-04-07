package code.yousef.summon.extensions

import code.yousef.summon.runtime.getPlatformRenderer

/**
 * Extensions for Number to provide CSS unit helpers
 */

/**
 * Converts a number to a CSS pixel value (e.g., 16.px -> "16px")
 */
val Number.px: String
    get() = "${this}px"

/**
 * Converts a number to a CSS rem value (e.g., 1.5.rem -> "1.5rem")
 */
val Number.rem: String
    get() = "${this}rem"

/**
 * Converts a number to a CSS em value (e.g., 1.2.em -> "1.2em")
 */
val Number.em: String
    get() = "${this}em"

/**
 * Converts a number to a CSS percentage value (e.g., 50.percent -> "50%")
 */
val Number.percent: String
    get() = "$this%"

/**
 * Converts a number to a CSS viewport width value (e.g., 100.vw -> "100vw")
 */
val Number.vw: String
    get() = "${this}vw"

/**
 * Converts a number to a CSS viewport height value (e.g., 100.vh -> "100vh")
 */
val Number.vh: String
    get() = "${this}vh"

/**
 * Converts a number to a CSS viewport min value (e.g., 50.vmin -> "50vmin")
 */
val Number.vmin: String
    get() = "${this}vmin"

/**
 * Converts a number to a CSS viewport max value (e.g., 50.vmax -> "50vmax")
 */
val Number.vmax: String
    get() = "${this}vmax"

/**
 * Converts a number to a CSS scale-independent pixels value (e.g., 14.sp -> "14sp")
 * Typically used for font sizes that should scale with user preference
 */
val Number.sp: String
    get() = "${this}sp"

/**
 * Converts a number to a CSS character unit value (e.g., 2.ch -> "2ch")
 * Represents the width of the "0" character in the current font
 */
val Number.ch: String
    get() = "${this}ch"

/**
 * Converts a number to a CSS ex unit value (e.g., 3.ex -> "3ex") 
 * Represents the x-height of the current font (height of the letter 'x')
 */
val Number.ex: String
    get() = "${this}ex"

/**
 * Converts a number to a CSS point value (e.g., 12.pt -> "12pt")
 * Traditional print measurement, 1pt = 1/72 of an inch
 */
val Number.pt: String
    get() = "${this}pt"

/**
 * Converts a number to a CSS pica value (e.g., 6.pc -> "6pc")
 * Traditional print measurement, 1pc = 12pt
 */
val Number.pc: String
    get() = "${this}pc" 
