// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: google/protobuf/type.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package com.google.protobuf;

@kotlin.jvm.JvmName("-initializeoption")
public inline fun option(block: com.google.protobuf.OptionKt.Dsl.() -> kotlin.Unit): com.google.protobuf.Option =
  com.google.protobuf.OptionKt.Dsl._create(com.google.protobuf.Option.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `google.protobuf.Option`
 */
public object OptionKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.google.protobuf.Option.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.google.protobuf.Option.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.google.protobuf.Option = _builder.build()

    /**
     * `string name = 1 [json_name = "name"];`
     */
    public var name: kotlin.String
      @JvmName("getName")
      get() = _builder.name
      @JvmName("setName")
      set(value) {
        _builder.name = value
      }
    /**
     * `string name = 1 [json_name = "name"];`
     */
    public fun clearName() {
      _builder.clearName()
    }

    /**
     * `.google.protobuf.Any value = 2 [json_name = "value"];`
     */
    public var value: com.google.protobuf.Any
      @JvmName("getValue")
      get() = _builder.value
      @JvmName("setValue")
      set(value) {
        _builder.value = value
      }
    /**
     * `.google.protobuf.Any value = 2 [json_name = "value"];`
     */
    public fun clearValue() {
      _builder.clearValue()
    }
    /**
     * `.google.protobuf.Any value = 2 [json_name = "value"];`
     * @return Whether the value field is set.
     */
    public fun hasValue(): kotlin.Boolean {
      return _builder.hasValue()
    }
    public val OptionKt.Dsl.valueOrNull: com.google.protobuf.Any?
      get() = _builder.valueOrNull
  }
}
public inline fun com.google.protobuf.Option.copy(block: `com.google.protobuf`.OptionKt.Dsl.() -> kotlin.Unit): com.google.protobuf.Option =
  `com.google.protobuf`.OptionKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val com.google.protobuf.OptionOrBuilder.valueOrNull: com.google.protobuf.Any?
  get() = if (hasValue()) getValue() else null
