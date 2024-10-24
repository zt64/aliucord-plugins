// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: google/protobuf/struct.proto
// Protobuf Java Version: 4.28.3

package com.google.protobuf;

/**
 * Protobuf enum {@code google.protobuf.NullValue}
 */
public enum NullValue
    implements com.google.protobuf.Internal.EnumLite {
  /**
   * <code>NULL_VALUE = 0;</code>
   */
  NULL_VALUE(0),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>NULL_VALUE = 0;</code>
   */
  public static final int NULL_VALUE_VALUE = 0;


  @java.lang.Override
  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The number of the enum to look for.
   * @return The enum associated with the given number.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static NullValue valueOf(int value) {
    return forNumber(value);
  }

  public static NullValue forNumber(int value) {
    switch (value) {
      case 0: return NULL_VALUE;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<NullValue>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      NullValue> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<NullValue>() {
          @java.lang.Override
          public NullValue findValueByNumber(int number) {
            return NullValue.forNumber(number);
          }
        };

  public static com.google.protobuf.Internal.EnumVerifier 
      internalGetVerifier() {
    return NullValueVerifier.INSTANCE;
  }

  private static final class NullValueVerifier implements 
       com.google.protobuf.Internal.EnumVerifier { 
          static final com.google.protobuf.Internal.EnumVerifier           INSTANCE = new NullValueVerifier();
          @java.lang.Override
          public boolean isInRange(int number) {
            return NullValue.forNumber(number) != null;
          }
        };

  private final int value;

  private NullValue(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:google.protobuf.NullValue)
}

