// Generated by the protocol buffer compiler. DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: google/protobuf/descriptor.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package com.google.protobuf;

@kotlin.jvm.JvmName("-initializedescriptorProto")
public inline fun descriptorProto(block: com.google.protobuf.DescriptorProtoKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.DescriptorProto =
  com.google.protobuf.DescriptorProtoKt.Dsl._create(com.google.protobuf.DescriptorProtos.DescriptorProto.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `google.protobuf.DescriptorProto`
 */
public object DescriptorProtoKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: com.google.protobuf.DescriptorProtos.DescriptorProto.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: com.google.protobuf.DescriptorProtos.DescriptorProto.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): com.google.protobuf.DescriptorProtos.DescriptorProto = _builder.build()

    /**
     * `optional string name = 1 [json_name = "name"];`
     */
    public var name: kotlin.String
      @JvmName("getName")
      get() = _builder.name
      @JvmName("setName")
      set(value) {
        _builder.name = value
      }
    /**
     * `optional string name = 1 [json_name = "name"];`
     */
    public fun clearName() {
      _builder.clearName()
    }
    /**
     * `optional string name = 1 [json_name = "name"];`
     * @return Whether the name field is set.
     */
    public fun hasName(): kotlin.Boolean {
      return _builder.hasName()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class FieldProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     */
     public val field: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.fieldList
      )
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     * @param value The field to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addField")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>.add(value: com.google.protobuf.DescriptorProtos.FieldDescriptorProto) {
      _builder.addField(value)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     * @param value The field to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignField")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.FieldDescriptorProto) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     * @param values The field to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllField")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.FieldDescriptorProto>) {
      _builder.addAllField(values)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     * @param values The field to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllField")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.FieldDescriptorProto>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     * @param index The index to set the value at.
     * @param value The field to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setField")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.FieldDescriptorProto) {
      _builder.setField(index, value)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto field = 2 [json_name = "field"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearField")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, FieldProxy>.clear() {
      _builder.clearField()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class ExtensionProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     */
     public val extension: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.extensionList
      )
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     * @param value The extension to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addExtension")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>.add(value: com.google.protobuf.DescriptorProtos.FieldDescriptorProto) {
      _builder.addExtension(value)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     * @param value The extension to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignExtension")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.FieldDescriptorProto) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     * @param values The extension to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllExtension")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.FieldDescriptorProto>) {
      _builder.addAllExtension(values)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     * @param values The extension to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllExtension")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.FieldDescriptorProto>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     * @param index The index to set the value at.
     * @param value The extension to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setExtension")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.FieldDescriptorProto) {
      _builder.setExtension(index, value)
    }
    /**
     * `repeated .google.protobuf.FieldDescriptorProto extension = 6 [json_name = "extension"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearExtension")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.FieldDescriptorProto, ExtensionProxy>.clear() {
      _builder.clearExtension()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class NestedTypeProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     */
     public val nestedType: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.nestedTypeList
      )
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     * @param value The nestedType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addNestedType")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>.add(value: com.google.protobuf.DescriptorProtos.DescriptorProto) {
      _builder.addNestedType(value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     * @param value The nestedType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignNestedType")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.DescriptorProto) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     * @param values The nestedType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllNestedType")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.DescriptorProto>) {
      _builder.addAllNestedType(values)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     * @param values The nestedType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllNestedType")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.DescriptorProto>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     * @param index The index to set the value at.
     * @param value The nestedType to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setNestedType")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.DescriptorProto) {
      _builder.setNestedType(index, value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto nested_type = 3 [json_name = "nestedType"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearNestedType")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto, NestedTypeProxy>.clear() {
      _builder.clearNestedType()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class EnumTypeProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     */
     public val enumType: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.enumTypeList
      )
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     * @param value The enumType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addEnumType")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>.add(value: com.google.protobuf.DescriptorProtos.EnumDescriptorProto) {
      _builder.addEnumType(value)
    }
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     * @param value The enumType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignEnumType")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.EnumDescriptorProto) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     * @param values The enumType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllEnumType")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.EnumDescriptorProto>) {
      _builder.addAllEnumType(values)
    }
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     * @param values The enumType to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllEnumType")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.EnumDescriptorProto>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     * @param index The index to set the value at.
     * @param value The enumType to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setEnumType")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.EnumDescriptorProto) {
      _builder.setEnumType(index, value)
    }
    /**
     * `repeated .google.protobuf.EnumDescriptorProto enum_type = 4 [json_name = "enumType"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearEnumType")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.EnumDescriptorProto, EnumTypeProxy>.clear() {
      _builder.clearEnumType()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class ExtensionRangeProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     */
     public val extensionRange: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.extensionRangeList
      )
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     * @param value The extensionRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addExtensionRange")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>.add(value: com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange) {
      _builder.addExtensionRange(value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     * @param value The extensionRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignExtensionRange")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     * @param values The extensionRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllExtensionRange")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange>) {
      _builder.addAllExtensionRange(values)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     * @param values The extensionRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllExtensionRange")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     * @param index The index to set the value at.
     * @param value The extensionRange to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setExtensionRange")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange) {
      _builder.setExtensionRange(index, value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ExtensionRange extension_range = 5 [json_name = "extensionRange"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearExtensionRange")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange, ExtensionRangeProxy>.clear() {
      _builder.clearExtensionRange()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class OneofDeclProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     */
     public val oneofDecl: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.oneofDeclList
      )
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     * @param value The oneofDecl to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addOneofDecl")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>.add(value: com.google.protobuf.DescriptorProtos.OneofDescriptorProto) {
      _builder.addOneofDecl(value)
    }
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     * @param value The oneofDecl to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignOneofDecl")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.OneofDescriptorProto) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     * @param values The oneofDecl to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllOneofDecl")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.OneofDescriptorProto>) {
      _builder.addAllOneofDecl(values)
    }
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     * @param values The oneofDecl to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllOneofDecl")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.OneofDescriptorProto>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     * @param index The index to set the value at.
     * @param value The oneofDecl to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setOneofDecl")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.OneofDescriptorProto) {
      _builder.setOneofDecl(index, value)
    }
    /**
     * `repeated .google.protobuf.OneofDescriptorProto oneof_decl = 8 [json_name = "oneofDecl"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearOneofDecl")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.OneofDescriptorProto, OneofDeclProxy>.clear() {
      _builder.clearOneofDecl()
    }

    /**
     * `optional .google.protobuf.MessageOptions options = 7 [json_name = "options"];`
     */
    public var options: com.google.protobuf.DescriptorProtos.MessageOptions
      @JvmName("getOptions")
      get() = _builder.options
      @JvmName("setOptions")
      set(value) {
        _builder.options = value
      }
    /**
     * `optional .google.protobuf.MessageOptions options = 7 [json_name = "options"];`
     */
    public fun clearOptions() {
      _builder.clearOptions()
    }
    /**
     * `optional .google.protobuf.MessageOptions options = 7 [json_name = "options"];`
     * @return Whether the options field is set.
     */
    public fun hasOptions(): kotlin.Boolean {
      return _builder.hasOptions()
    }
    public val DescriptorProtoKt.Dsl.optionsOrNull: com.google.protobuf.DescriptorProtos.MessageOptions?
      get() = _builder.optionsOrNull

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class ReservedRangeProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     */
     public val reservedRange: com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.reservedRangeList
      )
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     * @param value The reservedRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addReservedRange")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>.add(value: com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange) {
      _builder.addReservedRange(value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     * @param value The reservedRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignReservedRange")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>.plusAssign(value: com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange) {
      add(value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     * @param values The reservedRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllReservedRange")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>.addAll(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange>) {
      _builder.addAllReservedRange(values)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     * @param values The reservedRange to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllReservedRange")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>.plusAssign(values: kotlin.collections.Iterable<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange>) {
      addAll(values)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     * @param index The index to set the value at.
     * @param value The reservedRange to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setReservedRange")
    public operator fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>.set(index: kotlin.Int, value: com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange) {
      _builder.setReservedRange(index, value)
    }
    /**
     * `repeated .google.protobuf.DescriptorProto.ReservedRange reserved_range = 9 [json_name = "reservedRange"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearReservedRange")
    public fun com.google.protobuf.kotlin.DslList<com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange, ReservedRangeProxy>.clear() {
      _builder.clearReservedRange()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class ReservedNameProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     * @return A list containing the reservedName.
     */
    public val reservedName: com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
      get() = com.google.protobuf.kotlin.DslList(
        _builder.reservedNameList
      )
    /**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     * @param value The reservedName to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addReservedName")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>.add(value: kotlin.String) {
      _builder.addReservedName(value)
    }
    /**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     * @param value The reservedName to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignReservedName")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>.plusAssign(value: kotlin.String) {
      add(value)
    }
    /**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     * @param values The reservedName to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllReservedName")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>.addAll(values: kotlin.collections.Iterable<kotlin.String>) {
      _builder.addAllReservedName(values)
    }
    /**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     * @param values The reservedName to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllReservedName")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>.plusAssign(values: kotlin.collections.Iterable<kotlin.String>) {
      addAll(values)
    }
    /**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     * @param index The index to set the value at.
     * @param value The reservedName to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setReservedName")
    public operator fun com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>.set(index: kotlin.Int, value: kotlin.String) {
      _builder.setReservedName(index, value)
    }/**
     * `repeated string reserved_name = 10 [json_name = "reservedName"];`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearReservedName")
    public fun com.google.protobuf.kotlin.DslList<kotlin.String, ReservedNameProxy>.clear() {
      _builder.clearReservedName()
    }}
  @kotlin.jvm.JvmName("-initializeextensionRange")
  public inline fun extensionRange(block: com.google.protobuf.DescriptorProtoKt.ExtensionRangeKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange =
    com.google.protobuf.DescriptorProtoKt.ExtensionRangeKt.Dsl._create(com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange.newBuilder()).apply { block() }._build()
  /**
   * Protobuf type `google.protobuf.DescriptorProto.ExtensionRange`
   */
  public object ExtensionRangeKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange = _builder.build()

      /**
       * <code>optional int32 start = 1 [json_name = "start"];</code>
       */
      public var start: kotlin.Int
        @JvmName("getStart")
        get() = _builder.start
        @JvmName("setStart")
        set(value) {
          _builder.start = value
        }
      /**
       * `optional int32 start = 1 [json_name = "start"];`
       */
      public fun clearStart() {
        _builder.clearStart()
      }
      /**
       * `optional int32 start = 1 [json_name = "start"];`
       * @return Whether the start field is set.
       */
      public fun hasStart(): kotlin.Boolean {
        return _builder.hasStart()
      }

      /**
       * <code>optional int32 end = 2 [json_name = "end"];</code>
       */
      public var end: kotlin.Int
        @JvmName("getEnd")
        get() = _builder.end
        @JvmName("setEnd")
        set(value) {
          _builder.end = value
        }
      /**
       * `optional int32 end = 2 [json_name = "end"];`
       */
      public fun clearEnd() {
        _builder.clearEnd()
      }
      /**
       * `optional int32 end = 2 [json_name = "end"];`
       * @return Whether the end field is set.
       */
      public fun hasEnd(): kotlin.Boolean {
        return _builder.hasEnd()
      }

      /**
       * `optional .google.protobuf.ExtensionRangeOptions options = 3 [json_name = "options"];`
       */
      public var options: com.google.protobuf.DescriptorProtos.ExtensionRangeOptions
        @JvmName("getOptions")
        get() = _builder.options
        @JvmName("setOptions")
        set(value) {
          _builder.options = value
        }
      /**
       * `optional .google.protobuf.ExtensionRangeOptions options = 3 [json_name = "options"];`
       */
      public fun clearOptions() {
        _builder.clearOptions()
      }
      /**
       * `optional .google.protobuf.ExtensionRangeOptions options = 3 [json_name = "options"];`
       * @return Whether the options field is set.
       */
      public fun hasOptions(): kotlin.Boolean {
        return _builder.hasOptions()
      }
      public val ExtensionRangeKt.Dsl.optionsOrNull: com.google.protobuf.DescriptorProtos.ExtensionRangeOptions?
        get() = _builder.optionsOrNull
    }
  }
  @kotlin.jvm.JvmName("-initializereservedRange")
  public inline fun reservedRange(block: com.google.protobuf.DescriptorProtoKt.ReservedRangeKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange =
    com.google.protobuf.DescriptorProtoKt.ReservedRangeKt.Dsl._create(com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange.newBuilder()).apply { block() }._build()
  /**
   * Protobuf type `google.protobuf.DescriptorProto.ReservedRange`
   */
  public object ReservedRangeKt {
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    @com.google.protobuf.kotlin.ProtoDslMarker
    public class Dsl private constructor(
      private val _builder: com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange.Builder
    ) {
      public companion object {
        @kotlin.jvm.JvmSynthetic
        @kotlin.PublishedApi
        internal fun _create(builder: com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange.Builder): Dsl = Dsl(builder)
      }

      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _build(): com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange = _builder.build()

      /**
       * <code>optional int32 start = 1 [json_name = "start"];</code>
       */
      public var start: kotlin.Int
        @JvmName("getStart")
        get() = _builder.start
        @JvmName("setStart")
        set(value) {
          _builder.start = value
        }
      /**
       * `optional int32 start = 1 [json_name = "start"];`
       */
      public fun clearStart() {
        _builder.clearStart()
      }
      /**
       * `optional int32 start = 1 [json_name = "start"];`
       * @return Whether the start field is set.
       */
      public fun hasStart(): kotlin.Boolean {
        return _builder.hasStart()
      }

      /**
       * <code>optional int32 end = 2 [json_name = "end"];</code>
       */
      public var end: kotlin.Int
        @JvmName("getEnd")
        get() = _builder.end
        @JvmName("setEnd")
        set(value) {
          _builder.end = value
        }
      /**
       * `optional int32 end = 2 [json_name = "end"];`
       */
      public fun clearEnd() {
        _builder.clearEnd()
      }
      /**
       * `optional int32 end = 2 [json_name = "end"];`
       * @return Whether the end field is set.
       */
      public fun hasEnd(): kotlin.Boolean {
        return _builder.hasEnd()
      }
    }
  }
}
public inline fun com.google.protobuf.DescriptorProtos.DescriptorProto.copy(block: `com.google.protobuf`.DescriptorProtoKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.DescriptorProto =
  `com.google.protobuf`.DescriptorProtoKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public inline fun com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange.copy(block: `com.google.protobuf`.DescriptorProtoKt.ExtensionRangeKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRange =
  `com.google.protobuf`.DescriptorProtoKt.ExtensionRangeKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val com.google.protobuf.DescriptorProtos.DescriptorProto.ExtensionRangeOrBuilder.optionsOrNull: com.google.protobuf.DescriptorProtos.ExtensionRangeOptions?
  get() = if (hasOptions()) getOptions() else null

public inline fun com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange.copy(block: `com.google.protobuf`.DescriptorProtoKt.ReservedRangeKt.Dsl.() -> kotlin.Unit): com.google.protobuf.DescriptorProtos.DescriptorProto.ReservedRange =
  `com.google.protobuf`.DescriptorProtoKt.ReservedRangeKt.Dsl._create(this.toBuilder()).apply { block() }._build()

public val com.google.protobuf.DescriptorProtos.DescriptorProtoOrBuilder.optionsOrNull: com.google.protobuf.DescriptorProtos.MessageOptions?
  get() = if (hasOptions()) getOptions() else null

