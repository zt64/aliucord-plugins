// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: google/protobuf/descriptor.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package com.google.protobuf;

@kotlin.jvm.JvmName("-initializeenumValueOptions")
public inline fun enumValueOptions(block: com.google.protobuf.EnumValueOptionsKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.EnumValueOptions =
  com.google.protobuf.EnumValueOptionsKt.Dsl._create(com.google.protobuf.DescriptorProtos.EnumValueOptions.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `google.protobuf.EnumValueOptions`
 */
public object EnumValueOptionsKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.google.protobuf.DescriptorProtos.EnumValueOptions.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.google.protobuf.DescriptorProtos.EnumValueOptions.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.google.protobuf.DescriptorProtos.EnumValueOptions = _builder.build()

    /**
     * <code>optional bool deprecated = 1 [default = false, json_name = "deprecated"];</code>
     */
    public var deprecated: kotlin.Boolean
      @JvmName("getDeprecated")
      get() = _builder.deprecated
      @JvmName("setDeprecated")
      set(value) {
        _builder.deprecated = value
      }
    /**
     * `optional bool deprecated = 1 [default = false, json_name = "deprecated"];`
     */
    public fun clearDeprecated() {
      _builder.clearDeprecated()
    }
    /**
     * `optional bool deprecated = 1 [default = false, json_name = "deprecated"];`
     * @return Whether the deprecated field is set.
     */
    public fun hasDeprecated(): kotlin.Boolean {
      return _builder.hasDeprecated()
    }

    /**
     * `optional .google.protobuf.FeatureSet features = 2 [json_name = "features"];`
     */
    public var features: com.google.protobuf.DescriptorProtos.FeatureSet
      @JvmName("getFeatures")
      get() = _builder.features
      @JvmName("setFeatures")
      set(value) {
        _builder.features = value
      }
    /**
     * `optional .google.protobuf.FeatureSet features = 2 [json_name = "features"];`
     */
    public fun clearFeatures() {
      _builder.clearFeatures()
    }
    /**
     * `optional .google.protobuf.FeatureSet features = 2 [json_name = "features"];`
     * @return Whether the features field is set.
     */
    public fun hasFeatures(): kotlin.Boolean {
      return _builder.hasFeatures()
    }
    public val EnumValueOptionsKt.Dsl.featuresOrNull: com.google.protobuf.DescriptorProtos.FeatureSet?
      get() = _builder.featuresOrNull

    /**
     * <code>optional bool debug_redact = 3 [default = false, json_name = "debugRedact"];</code>
     */
    public var debugRedact: kotlin.Boolean
      @JvmName("getDebugRedact")
      get() = _builder.debugRedact
      @JvmName("setDebugRedact")
      set(value) {
        _builder.debugRedact = value
      }
    /**
     * `optional bool debug_redact = 3 [default = false, json_name = "debugRedact"];`
     */
    public fun clearDebugRedact() {
      _builder.clearDebugRedact()
    }
    /**
     * `optional bool debug_redact = 3 [default = false, json_name = "debugRedact"];`
     * @return Whether the debugRedact field is set.
     */
    public fun hasDebugRedact(): kotlin.Boolean {
      return _builder.hasDebugRedact()
    }

    /**
     * `optional .google.protobuf.FieldOptions.FeatureSupport feature_support = 4 [json_name = "featureSupport"];`
     */
    public var featureSupport: com.google.protobuf.DescriptorProtos.FieldOptions.FeatureSupport
      @JvmName("getFeatureSupport")
      get() = _builder.featureSupport
      @JvmName("setFeatureSupport")
      set(value) {
        _builder.featureSupport = value
      }
    /**
     * `optional .google.protobuf.FieldOptions.FeatureSupport feature_support = 4 [json_name = "featureSupport"];`
     */
    public fun clearFeatureSupport() {
      _builder.clearFeatureSupport()
    }
    /**
     * `optional .google.protobuf.FieldOptions.FeatureSupport feature_support = 4 [json_name = "featureSupport"];`
     * @return Whether the featureSupport field is set.
     */
    public fun hasFeatureSupport(): kotlin.Boolean {
      return _builder.hasFeatureSupport()
    }
    public val EnumValueOptionsKt.Dsl.featureSupportOrNull: com.google.protobuf.DescriptorProtos.FieldOptions.FeatureSupport?
      get() = _builder.featureSupportOrNull

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class UninterpretedOptionProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     */
     public val uninterpretedOption: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.uninterpretedOptionList
      )
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     * @param value The uninterpretedOption to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addUninterpretedOption")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>.add(value: com.google.protobuf.DescriptorProtos.UninterpretedOption) {
      _builder.addUninterpretedOption(value)
    }
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     * @param value The uninterpretedOption to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignUninterpretedOption")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.UninterpretedOption) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     * @param values The uninterpretedOption to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllUninterpretedOption")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.UninterpretedOption>) {
      _builder.addAllUninterpretedOption(values)
    }
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     * @param values The uninterpretedOption to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllUninterpretedOption")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.UninterpretedOption>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     * @param index The index to set the value at.
     * @param value The uninterpretedOption to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setUninterpretedOption")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.UninterpretedOption) {
      _builder.setUninterpretedOption(index, value)
    }
    /**
     * `repeated .google.protobuf.UninterpretedOption uninterpreted_option = 999 [json_name = "uninterpretedOption"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearUninterpretedOption")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.UninterpretedOption, UninterpretedOptionProxy>.clear() {
      _builder.clearUninterpretedOption()
    }
    @Suppress("UNCHECKED_CAST")
    @kotlin.jvm.JvmSynthetic
    public operator fun <T : kotlin.Any> get(extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, T>): T {
      return if (extension.isRepeated) {
        get(extension as com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, kotlin.collections.List<*>>) as T
      } else {
        _builder.getExtension(extension)
      }
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @kotlin.jvm.JvmName("-getRepeatedExtension")
    public operator fun <E : kotlin.Any> get(
      extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, kotlin.collections.List<E>>
    ): com.google.protobuf.kotlin.ExtensionList<E, com.google.protobuf.DescriptorProtos.EnumValueOptions> {
      return com.google.protobuf.kotlin.ExtensionList(extension, _builder.getExtension(extension))
    }

    @kotlin.jvm.JvmSynthetic
    public operator fun contains(extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, *>): Boolean {
      return _builder.hasExtension(extension)
    }

    @kotlin.jvm.JvmSynthetic
    public fun clear(extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, *>) {
      _builder.clearExtension(extension)
    }

    @kotlin.jvm.JvmSynthetic
    public fun <T : kotlin.Any> setExtension(extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, T>, value: T) {
      _builder.setExtension(extension, value)
    }

    @kotlin.jvm.JvmSynthetic
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun <T : Comparable<T>> set(
      extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, T>,
      value: T
    ) {
      setExtension(extension, value)
    }

    @kotlin.jvm.JvmSynthetic
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun set(
      extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, com.google.protobuf.ByteString>,
      value: com.google.protobuf.ByteString
    ) {
      setExtension(extension, value)
    }

    @kotlin.jvm.JvmSynthetic
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun <T : com.google.protobuf.MessageLite> set(
      extension: com.google.protobuf.ExtensionLite<com.google.protobuf.DescriptorProtos.EnumValueOptions, T>,
      value: T
    ) {
      setExtension(extension, value)
    }

    @kotlin.jvm.JvmSynthetic
    public fun<E : kotlin.Any> com.google.protobuf.kotlin.ExtensionList<E, com.google.protobuf.DescriptorProtos.EnumValueOptions>.add(value: E) {
      _builder.addExtension(this.extension, value)
    }

    @kotlin.jvm.JvmSynthetic
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun <E : kotlin.Any> com.google.protobuf.kotlin.ExtensionList<E, com.google.protobuf.DescriptorProtos.EnumValueOptions>.plusAssign(value: E) {
      add(value)
    }

    @kotlin.jvm.JvmSynthetic
    public fun<E : kotlin.Any> com.google.protobuf.kotlin.ExtensionList<E, com.google.protobuf.DescriptorProtos.EnumValueOptions>.addAll(values: Iterable<E>) {
      for (value in values) {
        add(value)
      }
    }

    @kotlin.jvm.JvmSynthetic
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun <E : kotlin.Any> com.google.protobuf.kotlin.ExtensionList<E, com.google.protobuf.DescriptorProtos.EnumValueOptions>.plusAssign(values: Iterable<E>) {
      addAll(values)
    }

    @kotlin.jvm.JvmSynthetic
    public operator fun <E : kotlin.Any> com.google.protobuf.kotlin.ExtensionList<E, com.google.protobuf.DescriptorProtos.EnumValueOptions>.set(index: Int, value: E) {
      _builder.setExtension(this.extension, index, value)
    }

    @kotlin.jvm.JvmSynthetic
    @Suppress("NOTHING_TO_INLINE")
    public inline fun com.google.protobuf.kotlin.ExtensionList<*, com.google.protobuf.DescriptorProtos.EnumValueOptions>.clear() {
      clear(extension)
    }

  }
}
public inline fun com.google.protobuf.DescriptorProtos.EnumValueOptions.copy(block: `com.google.protobuf`.EnumValueOptionsKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.EnumValueOptions =
  `com.google.protobuf`.EnumValueOptionsKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val com.google.protobuf.DescriptorProtos.EnumValueOptionsOrBuilder.featuresOrNull: com.google.protobuf.DescriptorProtos.FeatureSet?
  get() = if (hasFeatures()) getFeatures() else null

public val com.google.protobuf.DescriptorProtos.EnumValueOptionsOrBuilder.featureSupportOrNull: com.google.protobuf.DescriptorProtos.FieldOptions.FeatureSupport?
  get() = if (hasFeatureSupport()) getFeatureSupport() else null
