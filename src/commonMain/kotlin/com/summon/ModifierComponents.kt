package com.summon

/**
 * Interface markers to define component capabilities for type-specific modifiers.
 * These interfaces help to ensure that certain modifiers are only applied
 * to components that support those modifications.
 */

/**
 * Marker interface for components that can be scrolled.
 */
interface ScrollableComponent

/**
 * Marker interface for input field components.
 */
interface InputComponent

/**
 * Marker interface for text-based components.
 */
interface TextComponent

/**
 * Marker interface for layout container components.
 */
interface LayoutComponent

/**
 * Marker interface for clickable components.
 */
interface ClickableComponent

/**
 * Marker interface for components that can receive focus events.
 */
interface FocusableComponent

/**
 * Marker interface for media components like images and videos.
 */
interface MediaComponent 