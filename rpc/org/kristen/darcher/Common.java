// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: common.proto

package org.kristen.darcher;

public final class Common {
  private Common() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * Protobuf enum {@code darcher.Role}
   */
  public enum Role
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>DOER = 0;</code>
     */
    DOER(0),
    /**
     * <code>TALKER = 1;</code>
     */
    TALKER(1),
    /**
     * <code>DAPP = 2;</code>
     */
    DAPP(2),
    /**
     * <code>DBMONITOR = 3;</code>
     */
    DBMONITOR(3),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>DOER = 0;</code>
     */
    public static final int DOER_VALUE = 0;
    /**
     * <code>TALKER = 1;</code>
     */
    public static final int TALKER_VALUE = 1;
    /**
     * <code>DAPP = 2;</code>
     */
    public static final int DAPP_VALUE = 2;
    /**
     * <code>DBMONITOR = 3;</code>
     */
    public static final int DBMONITOR_VALUE = 3;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static Role valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Role forNumber(int value) {
      switch (value) {
        case 0: return DOER;
        case 1: return TALKER;
        case 2: return DAPP;
        case 3: return DBMONITOR;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Role>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Role> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Role>() {
            public Role findValueByNumber(int number) {
              return Role.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return Common.getDescriptor().getEnumTypes().get(0);
    }

    private static final Role[] VALUES = values();

    public static Role valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Role(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:darcher.Role)
  }

  /**
   * Protobuf enum {@code darcher.Error}
   */
  public enum Error
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>NilErr = 0;</code>
     */
    NilErr(0),
    /**
     * <code>InternalErr = 1;</code>
     */
    InternalErr(1),
    /**
     * <code>TimeoutErr = 2;</code>
     */
    TimeoutErr(2),
    /**
     * <code>ServiceNotAvailableErr = 3;</code>
     */
    ServiceNotAvailableErr(3),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>NilErr = 0;</code>
     */
    public static final int NilErr_VALUE = 0;
    /**
     * <code>InternalErr = 1;</code>
     */
    public static final int InternalErr_VALUE = 1;
    /**
     * <code>TimeoutErr = 2;</code>
     */
    public static final int TimeoutErr_VALUE = 2;
    /**
     * <code>ServiceNotAvailableErr = 3;</code>
     */
    public static final int ServiceNotAvailableErr_VALUE = 3;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static Error valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static Error forNumber(int value) {
      switch (value) {
        case 0: return NilErr;
        case 1: return InternalErr;
        case 2: return TimeoutErr;
        case 3: return ServiceNotAvailableErr;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<Error>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        Error> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<Error>() {
            public Error findValueByNumber(int number) {
              return Error.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return Common.getDescriptor().getEnumTypes().get(1);
    }

    private static final Error[] VALUES = values();

    public static Error valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private Error(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:darcher.Error)
  }

  /**
   * Protobuf enum {@code darcher.TxState}
   */
  public enum TxState
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <code>CREATED = 0;</code>
     */
    CREATED(0),
    /**
     * <code>PENDING = 1;</code>
     */
    PENDING(1),
    /**
     * <code>EXECUTED = 2;</code>
     */
    EXECUTED(2),
    /**
     * <code>DROPPED = 3;</code>
     */
    DROPPED(3),
    /**
     * <code>CONFIRMED = 4;</code>
     */
    CONFIRMED(4),
    UNRECOGNIZED(-1),
    ;

    /**
     * <code>CREATED = 0;</code>
     */
    public static final int CREATED_VALUE = 0;
    /**
     * <code>PENDING = 1;</code>
     */
    public static final int PENDING_VALUE = 1;
    /**
     * <code>EXECUTED = 2;</code>
     */
    public static final int EXECUTED_VALUE = 2;
    /**
     * <code>DROPPED = 3;</code>
     */
    public static final int DROPPED_VALUE = 3;
    /**
     * <code>CONFIRMED = 4;</code>
     */
    public static final int CONFIRMED_VALUE = 4;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static TxState valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static TxState forNumber(int value) {
      switch (value) {
        case 0: return CREATED;
        case 1: return PENDING;
        case 2: return EXECUTED;
        case 3: return DROPPED;
        case 4: return CONFIRMED;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<TxState>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        TxState> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<TxState>() {
            public TxState findValueByNumber(int number) {
              return TxState.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return Common.getDescriptor().getEnumTypes().get(2);
    }

    private static final TxState[] VALUES = values();

    public static TxState valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private TxState(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:darcher.TxState)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\014common.proto\022\007darcher*5\n\004Role\022\010\n\004DOER\020" +
      "\000\022\n\n\006TALKER\020\001\022\010\n\004DAPP\020\002\022\r\n\tDBMONITOR\020\003*P" +
      "\n\005Error\022\n\n\006NilErr\020\000\022\017\n\013InternalErr\020\001\022\016\n\n" +
      "TimeoutErr\020\002\022\032\n\026ServiceNotAvailableErr\020\003" +
      "*M\n\007TxState\022\013\n\007CREATED\020\000\022\013\n\007PENDING\020\001\022\014\n" +
      "\010EXECUTED\020\002\022\013\n\007DROPPED\020\003\022\r\n\tCONFIRMED\020\004B" +
      "8Z6github.com/Troublor/darcher-go-ethere" +
      "um/ethmonitor/rpcb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
