// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: google/protobuf/type.proto
// Protobuf Java Version: 4.28.3

package com.google.protobuf;

/**
 * Protobuf enum {@code google.protobuf.Syntax}
 */
public enum Syntax
    implements com.google.protobuf.Internal.EnumLite {
  /**
   * <code>SYNTAX_PROTO2 = 0;</code>
   */
  SYNTAX_PROTO2(0),
  /**
   * <code>SYNTAX_PROTO3 = 1;</code>
   */
  SYNTAX_PROTO3(1),
  /**
   * <code>SYNTAX_EDITIONS = 2;</code>
   */
  SYNTAX_EDITIONS(2),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>SYNTAX_PROTO2 = 0;</code>
   */
  public static final int SYNTAX_PROTO2_VALUE = 0;
  /**
   * <code>SYNTAX_PROTO3 = 1;</code>
   */
  public static final int SYNTAX_PROTO3_VALUE = 1;
  /**
   * <code>SYNTAX_EDITIONS = 2;</code>
   */
  public static final int SYNTAX_EDITIONS_VALUE = 2;


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
  public static Syntax valueOf(int value) {
    return forNumber(value);
  }

  public static Syntax forNumber(int value) {
    switch (value) {
      case 0: return SYNTAX_PROTO2;
      case 1: return SYNTAX_PROTO3;
      case 2: return SYNTAX_EDITIONS;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<Syntax>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      Syntax> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<Syntax>() {
          @java.lang.Override
          public Syntax findValueByNumber(int number) {
            return Syntax.forNumber(number);
          }
        };

  public static com.google.protobuf.Internal.EnumVerifier 
      internalGetVerifier() {
    return SyntaxVerifier.INSTANCE;
  }

  private static final class SyntaxVerifier implements 
       com.google.protobuf.Internal.EnumVerifier { 
          static final com.google.protobuf.Internal.EnumVerifier           INSTANCE = new SyntaxVerifier();
          @java.lang.Override
          public boolean isInRange(int number) {
            return Syntax.forNumber(number) != null;
          }
        };

  private final int value;

  private Syntax(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:google.protobuf.Syntax)
}
