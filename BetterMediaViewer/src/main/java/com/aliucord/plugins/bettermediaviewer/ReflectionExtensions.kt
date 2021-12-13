package com.aliucord.plugins.bettermediaviewer

import android.animation.ValueAnimator
import com.discord.databinding.WidgetMediaBinding
import com.discord.player.MediaSource
import com.discord.widgets.media.WidgetMedia
import rx.Subscription

private val controlsAnimatorField = WidgetMedia::class.java.getDeclaredField("controlsAnimator")
    .apply { isAccessible = true }
var WidgetMedia.controlsAnimator
    get() = controlsAnimatorField[this] as ValueAnimator?
    set(valueAnimator) = controlsAnimatorField.set(this, valueAnimator)

private val mediaSourceField = WidgetMedia::class.java.getDeclaredField("mediaSource")
    .apply { isAccessible = true }
val WidgetMedia.mediaSource
    get() = mediaSourceField[this] as MediaSource?

val WidgetMedia.playerControlsHeight
    get() = WidgetMedia.`access$getPlayerControlsHeight$p`(this)

val WidgetMedia.toolbarHeight
    get() = WidgetMedia.`access$getToolbarHeight$p`(this)

val WidgetMedia.binding: WidgetMediaBinding
    get() = WidgetMedia.`access$getBinding$p`(this)

val WidgetMedia.controlsVisibilitySubscription: Subscription?
    get() = WidgetMedia.`access$getControlsVisibilitySubscription$p`(this)

private val controlsAnimationActionField = WidgetMedia::class.java.getDeclaredField("controlsAnimationAction")
    .apply { isAccessible = true }
var WidgetMedia.controlsAnimationAction: WidgetMedia.ControlsAnimationAction
    get() = WidgetMedia.`access$getControlsAnimationAction$p`(this)
    set(v) = controlsAnimationActionField.set(this, v)

private val getToolbarTranslationY = WidgetMedia::class.java.getDeclaredMethod("getToolbarTranslationY")
    .apply { isAccessible = true }

fun WidgetMedia.getToolbarTranslationY() = getToolbarTranslationY.invoke(this) as Float

private val configureAndStartControlsAnimation = WidgetMedia::class.java.getDeclaredMethod("configureAndStartControlsAnimation", ValueAnimator::class.java)
    .apply { isAccessible = true }

fun WidgetMedia.configureAndStartControlsAnimation(valueAnimator: ValueAnimator): Any? = configureAndStartControlsAnimation.invoke(this, valueAnimator)

fun WidgetMedia.isVideo() = WidgetMedia.`access$isVideo`(this)