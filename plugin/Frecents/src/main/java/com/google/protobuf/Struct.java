// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: google/protobuf/struct.proto
// Protobuf Java Version: 4.28.3

package com.google.protobuf;

/**
 * Protobuf type {@code google.protobuf.Struct}
 */
public  final class Struct extends
    com.google.protobuf.GeneratedMessageLite<
        Struct, Struct.Builder> implements
    // @@protoc_insertion_point(message_implements:google.protobuf.Struct)
    StructOrBuilder {
  private Struct() {
  }
  public static final int FIELDS_FIELD_NUMBER = 1;
  private static final class FieldsDefaultEntryHolder {
    static final com.google.protobuf.MapEntryLite<
        java.lang.String, com.google.protobuf.Value> defaultEntry =
            com.google.protobuf.MapEntryLite
            .<java.lang.String, com.google.protobuf.Value>newDefaultInstance(
                com.google.protobuf.WireFormat.FieldType.STRING,
                "",
                com.google.protobuf.WireFormat.FieldType.MESSAGE,
                com.google.protobuf.Value.getDefaultInstance());
  }
  private com.google.protobuf.MapFieldLite<
      java.lang.String, com.google.protobuf.Value> fields_ =
          com.google.protobuf.MapFieldLite.emptyMapField();
  private com.google.protobuf.MapFieldLite<java.lang.String, com.google.protobuf.Value>
  internalGetFields() {
    return fields_;
  }
  private com.google.protobuf.MapFieldLite<java.lang.String, com.google.protobuf.Value>
  internalGetMutableFields() {
    if (!fields_.isMutable()) {
      fields_ = fields_.mutableCopy();
    }
    return fields_;
  }
  @java.lang.Override

  public int getFieldsCount() {
    return internalGetFields().size();
  }
  /**
   * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
   */
  @java.lang.Override

  public boolean containsFields(
      java.lang.String key) {
    java.lang.Class<?> keyClass = key.getClass();
    return internalGetFields().containsKey(key);
  }
  /**
   * Use {@link #getFieldsMap()} instead.
   */
  @java.lang.Override
  @java.lang.Deprecated
  public java.util.Map<java.lang.String, com.google.protobuf.Value> getFields() {
    return getFieldsMap();
  }
  /**
   * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
   */
  @java.lang.Override

  public java.util.Map<java.lang.String, com.google.protobuf.Value> getFieldsMap() {
    return java.util.Collections.unmodifiableMap(
        internalGetFields());
  }
  /**
   * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
   */
  @java.lang.Override

  public /* nullable */
com.google.protobuf.Value getFieldsOrDefault(
      java.lang.String key,
      /* nullable */
com.google.protobuf.Value defaultValue) {
    java.lang.Class<?> keyClass = key.getClass();
    java.util.Map<java.lang.String, com.google.protobuf.Value> map =
        internalGetFields();
    return map.containsKey(key) ? map.get(key) : defaultValue;
  }
  /**
   * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
   */
  @java.lang.Override

  public com.google.protobuf.Value getFieldsOrThrow(
      java.lang.String key) {
    java.lang.Class<?> keyClass = key.getClass();
    java.util.Map<java.lang.String, com.google.protobuf.Value> map =
        internalGetFields();
    if (!map.containsKey(key)) {
      throw new java.lang.IllegalArgumentException();
    }
    return map.get(key);
  }
  /**
   * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
   */
  private java.util.Map<java.lang.String, com.google.protobuf.Value>
  getMutableFieldsMap() {
    return internalGetMutableFields();
  }

  public static com.google.protobuf.Struct parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static com.google.protobuf.Struct parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static com.google.protobuf.Struct parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static com.google.protobuf.Struct parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static com.google.protobuf.Struct parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data);
  }
  public static com.google.protobuf.Struct parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, data, extensionRegistry);
  }
  public static com.google.protobuf.Struct parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static com.google.protobuf.Struct parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static com.google.protobuf.Struct parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input);
  }

  public static com.google.protobuf.Struct parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
  }
  public static com.google.protobuf.Struct parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input);
  }
  public static com.google.protobuf.Struct parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageLite.parseFrom(
        DEFAULT_INSTANCE, input, extensionRegistry);
  }

  public static Builder newBuilder() {
    return (Builder) DEFAULT_INSTANCE.createBuilder();
  }
  public static Builder newBuilder(com.google.protobuf.Struct prototype) {
    return DEFAULT_INSTANCE.createBuilder(prototype);
  }

  /**
   * Protobuf type {@code google.protobuf.Struct}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageLite.Builder<
        com.google.protobuf.Struct, Builder> implements
      // @@protoc_insertion_point(builder_implements:google.protobuf.Struct)
      com.google.protobuf.StructOrBuilder {
    // Construct using com.google.protobuf.Struct.newBuilder()
    private Builder() {
      super(DEFAULT_INSTANCE);
    }


    @java.lang.Override

    public int getFieldsCount() {
      return instance.getFieldsMap().size();
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */
    @java.lang.Override

    public boolean containsFields(
        java.lang.String key) {
      java.lang.Class<?> keyClass = key.getClass();
      return instance.getFieldsMap().containsKey(key);
    }

    public Builder clearFields() {
      copyOnWrite();
      instance.getMutableFieldsMap().clear();
      return this;
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */

    public Builder removeFields(
        java.lang.String key) {
      java.lang.Class<?> keyClass = key.getClass();
      copyOnWrite();
      instance.getMutableFieldsMap().remove(key);
      return this;
    }
    /**
     * Use {@link #getFieldsMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, com.google.protobuf.Value> getFields() {
      return getFieldsMap();
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */
    @java.lang.Override
    public java.util.Map<java.lang.String, com.google.protobuf.Value> getFieldsMap() {
      return java.util.Collections.unmodifiableMap(
          instance.getFieldsMap());
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */
    @java.lang.Override

    public /* nullable */
com.google.protobuf.Value getFieldsOrDefault(
        java.lang.String key,
        /* nullable */
com.google.protobuf.Value defaultValue) {
      java.lang.Class<?> keyClass = key.getClass();
      java.util.Map<java.lang.String, com.google.protobuf.Value> map =
          instance.getFieldsMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */
    @java.lang.Override

    public com.google.protobuf.Value getFieldsOrThrow(
        java.lang.String key) {
      java.lang.Class<?> keyClass = key.getClass();
      java.util.Map<java.lang.String, com.google.protobuf.Value> map =
          instance.getFieldsMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */
    public Builder putFields(
        java.lang.String key,
        com.google.protobuf.Value value) {
      java.lang.Class<?> keyClass = key.getClass();
      java.lang.Class<?> valueClass = value.getClass();
      copyOnWrite();
      instance.getMutableFieldsMap().put(key, value);
      return this;
    }
    /**
     * <code>map&lt;string, .google.protobuf.Value&gt; fields = 1 [json_name = "fields"];</code>
     */
    public Builder putAllFields(
        java.util.Map<java.lang.String, com.google.protobuf.Value> values) {
      copyOnWrite();
      instance.getMutableFieldsMap().putAll(values);
      return this;
    }

    // @@protoc_insertion_point(builder_scope:google.protobuf.Struct)
  }
  @java.lang.Override
  @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
  protected final java.lang.Object dynamicMethod(
      com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
      java.lang.Object arg0, java.lang.Object arg1) {
    switch (method) {
      case NEW_MUTABLE_INSTANCE: {
        return new com.google.protobuf.Struct();
      }
      case NEW_BUILDER: {
        return new Builder();
      }
      case BUILD_MESSAGE_INFO: {
          java.lang.Object[] objects = new java.lang.Object[] {
            "fields_",
            FieldsDefaultEntryHolder.defaultEntry,
          };
          java.lang.String info =
              "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u00012";
          return newMessageInfo(DEFAULT_INSTANCE, info, objects);
      }
      // fall through
      case GET_DEFAULT_INSTANCE: {
        return DEFAULT_INSTANCE;
      }
      case GET_PARSER: {
        com.google.protobuf.Parser<com.google.protobuf.Struct> parser = PARSER;
        if (parser == null) {
          synchronized (com.google.protobuf.Struct.class) {
            parser = PARSER;
            if (parser == null) {
              parser =
                  new DefaultInstanceBasedParser<com.google.protobuf.Struct>(
                      DEFAULT_INSTANCE);
              PARSER = parser;
            }
          }
        }
        return parser;
    }
    case GET_MEMOIZED_IS_INITIALIZED: {
      return (byte) 1;
    }
    case SET_MEMOIZED_IS_INITIALIZED: {
      return null;
    }
    }
    throw new UnsupportedOperationException();
  }


  // @@protoc_insertion_point(class_scope:google.protobuf.Struct)
  private static final com.google.protobuf.Struct DEFAULT_INSTANCE;
  static {
    Struct defaultInstance = new Struct();
    // New instances are implicitly immutable so no need to make
    // immutable.
    DEFAULT_INSTANCE = defaultInstance;
    com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
      Struct.class, defaultInstance);
  }

  public static com.google.protobuf.Struct getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static volatile com.google.protobuf.Parser<Struct> PARSER;

  public static com.google.protobuf.Parser<Struct> parser() {
    return DEFAULT_INSTANCE.getParserForType();
  }
}
