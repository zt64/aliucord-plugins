package com.aliucord.plugins.bettermediaviewer

import android.animation.ValueAnimator
import com.discord.databinding.WidgetMediaBinding
import com.discord.player.MediaSource
import com.discord.widgets.media.WidgetMedia
import rx.Subscription
import java.lang.reflect.Field
import java.lang.reflect.Method

private val controlsAnimatorField: Field =
        WidgetMedia::class.java.getDeclaredField("controlsAnimator").apply { isAccessible = true }
var WidgetMedia.controlsAnimator
    get() = controlsAnimatorField[this] as ValueAnimator?
    set(valueAnimator) = controlsAnimatorField.set(this, valueAnimator)

private val mediaSourceField: Field =
        WidgetMedia::class.java.getDeclaredField("mediaSource").apply { isAccessible = true }
val WidgetMedia.mediaSource
    get() = mediaSourceField[this] as MediaSource?

val WidgetMedia.controlsVisibilitySubscription: Subscription?
    get() = WidgetMedia.`access$getControlsVisibilitySubscription$p`(this)

val WidgetMedia.playerControlsHeight
    get() = WidgetMedia.`access$getPlayerControlsHeight$p`(this)

val WidgetMedia.toolbarHeight
    get() = WidgetMedia.`access$getToolbarHeight$p`(this)

val WidgetMedia.binding: WidgetMediaBinding
    get() = WidgetMedia.`access$getBinding$p`(this)

private val controlsAnimationActionField: Field = WidgetMedia::class.java.getDeclaredField("controlsAnimationAction")
        .apply { isAccessible = true }
var WidgetMedia.controlsAnimationAction: WidgetMedia.ControlsAnimationAction
    get() = WidgetMedia.`access$getControlsAnimationAction$p`(this)
    set(v) = controlsAnimationActionField.set(this, v)

private val getToolbarTranslationY: Method =
        WidgetMedia::class.java.getDeclaredMethod("getToolbarTranslationY")
                .apply { isAccessible = true }
fun WidgetMedia.getToolbarTranslationY() = getToolbarTranslationY.invoke(this) as Float

private val configureAndStartControlsAnimation: Method =
        WidgetMedia::class.java.getDeclaredMethod("configureAndStartControlsAnimation", ValueAnimator::class.java)
                .apply { isAccessible = true }
fun WidgetMedia.configureAndStartControlsAnimation(valueAnimator: ValueAnimator): Any? = configureAndStartControlsAnimation.invoke(this, valueAnimator)

fun WidgetMedia.isVideo(): Boolean = WidgetMedia.`access$isVideo`(this)