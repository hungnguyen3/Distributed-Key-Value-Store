// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: KeyValueRequest.proto

package ca.NetSysLab.ProtocolBuffers;

public final class KeyValueRequest {
  private KeyValueRequest() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface KVRequestOrBuilder extends
      // @@protoc_insertion_point(interface_extends:KVRequest)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 command = 1;</code>
     * @return The command.
     */
    int getCommand();

    /**
     * <code>optional bytes key = 2;</code>
     * @return Whether the key field is set.
     */
    boolean hasKey();
    /**
     * <code>optional bytes key = 2;</code>
     * @return The key.
     */
    com.google.protobuf.ByteString getKey();

    /**
     * <code>optional bytes value = 3;</code>
     * @return Whether the value field is set.
     */
    boolean hasValue();
    /**
     * <code>optional bytes value = 3;</code>
     * @return The value.
     */
    com.google.protobuf.ByteString getValue();

    /**
     * <code>optional int32 version = 4;</code>
     * @return Whether the version field is set.
     */
    boolean hasVersion();
    /**
     * <code>optional int32 version = 4;</code>
     * @return The version.
     */
    int getVersion();
  }
  /**
   * Protobuf type {@code KVRequest}
   */
  public static final class KVRequest extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:KVRequest)
      KVRequestOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use KVRequest.newBuilder() to construct.
    private KVRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private KVRequest() {
      key_ = com.google.protobuf.ByteString.EMPTY;
      value_ = com.google.protobuf.ByteString.EMPTY;
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new KVRequest();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return KeyValueRequest.internal_static_KVRequest_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return KeyValueRequest.internal_static_KVRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              KVRequest.class, Builder.class);
    }

    private int bitField0_;
    public static final int COMMAND_FIELD_NUMBER = 1;
    private int command_ = 0;
    /**
     * <code>uint32 command = 1;</code>
     * @return The command.
     */
    @Override
    public int getCommand() {
      return command_;
    }

    public static final int KEY_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString key_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes key = 2;</code>
     * @return Whether the key field is set.
     */
    @Override
    public boolean hasKey() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional bytes key = 2;</code>
     * @return The key.
     */
    @Override
    public com.google.protobuf.ByteString getKey() {
      return key_;
    }

    public static final int VALUE_FIELD_NUMBER = 3;
    private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes value = 3;</code>
     * @return Whether the value field is set.
     */
    @Override
    public boolean hasValue() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional bytes value = 3;</code>
     * @return The value.
     */
    @Override
    public com.google.protobuf.ByteString getValue() {
      return value_;
    }

    public static final int VERSION_FIELD_NUMBER = 4;
    private int version_ = 0;
    /**
     * <code>optional int32 version = 4;</code>
     * @return Whether the version field is set.
     */
    @Override
    public boolean hasVersion() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional int32 version = 4;</code>
     * @return The version.
     */
    @Override
    public int getVersion() {
      return version_;
    }

    private byte memoizedIsInitialized = -1;
    @Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (command_ != 0) {
        output.writeUInt32(1, command_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeBytes(2, key_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeBytes(3, value_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        output.writeInt32(4, version_);
      }
      getUnknownFields().writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (command_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, command_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, key_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, value_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, version_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof KVRequest)) {
        return super.equals(obj);
      }
      KVRequest other = (KVRequest) obj;

      if (getCommand()
          != other.getCommand()) return false;
      if (hasKey() != other.hasKey()) return false;
      if (hasKey()) {
        if (!getKey()
            .equals(other.getKey())) return false;
      }
      if (hasValue() != other.hasValue()) return false;
      if (hasValue()) {
        if (!getValue()
            .equals(other.getValue())) return false;
      }
      if (hasVersion() != other.hasVersion()) return false;
      if (hasVersion()) {
        if (getVersion()
            != other.getVersion()) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + COMMAND_FIELD_NUMBER;
      hash = (53 * hash) + getCommand();
      if (hasKey()) {
        hash = (37 * hash) + KEY_FIELD_NUMBER;
        hash = (53 * hash) + getKey().hashCode();
      }
      if (hasValue()) {
        hash = (37 * hash) + VALUE_FIELD_NUMBER;
        hash = (53 * hash) + getValue().hashCode();
      }
      if (hasVersion()) {
        hash = (37 * hash) + VERSION_FIELD_NUMBER;
        hash = (53 * hash) + getVersion();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static KVRequest parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static KVRequest parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static KVRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static KVRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static KVRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static KVRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static KVRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static KVRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static KVRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static KVRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static KVRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static KVRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(KVRequest prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code KVRequest}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:KVRequest)
        KVRequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return KeyValueRequest.internal_static_KVRequest_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return KeyValueRequest.internal_static_KVRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                KVRequest.class, Builder.class);
      }

      // Construct using ca.NetSysLab.ProtocolBuffers.KeyValueRequest.KVRequest.newBuilder()
      private Builder() {

      }

      private Builder(
          BuilderParent parent) {
        super(parent);

      }
      @Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        command_ = 0;
        key_ = com.google.protobuf.ByteString.EMPTY;
        value_ = com.google.protobuf.ByteString.EMPTY;
        version_ = 0;
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return KeyValueRequest.internal_static_KVRequest_descriptor;
      }

      @Override
      public KVRequest getDefaultInstanceForType() {
        return KVRequest.getDefaultInstance();
      }

      @Override
      public KVRequest build() {
        KVRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public KVRequest buildPartial() {
        KVRequest result = new KVRequest(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(KVRequest result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.command_ = command_;
        }
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.key_ = key_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.value_ = value_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000008) != 0)) {
          result.version_ = version_;
          to_bitField0_ |= 0x00000004;
        }
        result.bitField0_ |= to_bitField0_;
      }

      @Override
      public Builder clone() {
        return super.clone();
      }
      @Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.setField(field, value);
      }
      @Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return super.addRepeatedField(field, value);
      }
      @Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof KVRequest) {
          return mergeFrom((KVRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(KVRequest other) {
        if (other == KVRequest.getDefaultInstance()) return this;
        if (other.getCommand() != 0) {
          setCommand(other.getCommand());
        }
        if (other.hasKey()) {
          setKey(other.getKey());
        }
        if (other.hasValue()) {
          setValue(other.getValue());
        }
        if (other.hasVersion()) {
          setVersion(other.getVersion());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @Override
      public final boolean isInitialized() {
        return true;
      }

      @Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                command_ = input.readUInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 18: {
                key_ = input.readBytes();
                bitField0_ |= 0x00000002;
                break;
              } // case 18
              case 26: {
                value_ = input.readBytes();
                bitField0_ |= 0x00000004;
                break;
              } // case 26
              case 32: {
                version_ = input.readInt32();
                bitField0_ |= 0x00000008;
                break;
              } // case 32
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private int command_ ;
      /**
       * <code>uint32 command = 1;</code>
       * @return The command.
       */
      @Override
      public int getCommand() {
        return command_;
      }
      /**
       * <code>uint32 command = 1;</code>
       * @param value The command to set.
       * @return This builder for chaining.
       */
      public Builder setCommand(int value) {

        command_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 command = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearCommand() {
        bitField0_ = (bitField0_ & ~0x00000001);
        command_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString key_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes key = 2;</code>
       * @return Whether the key field is set.
       */
      @Override
      public boolean hasKey() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional bytes key = 2;</code>
       * @return The key.
       */
      @Override
      public com.google.protobuf.ByteString getKey() {
        return key_;
      }
      /**
       * <code>optional bytes key = 2;</code>
       * @param value The key to set.
       * @return This builder for chaining.
       */
      public Builder setKey(com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        key_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes key = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearKey() {
        bitField0_ = (bitField0_ & ~0x00000002);
        key_ = getDefaultInstance().getKey();
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes value = 3;</code>
       * @return Whether the value field is set.
       */
      @Override
      public boolean hasValue() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional bytes value = 3;</code>
       * @return The value.
       */
      @Override
      public com.google.protobuf.ByteString getValue() {
        return value_;
      }
      /**
       * <code>optional bytes value = 3;</code>
       * @param value The value to set.
       * @return This builder for chaining.
       */
      public Builder setValue(com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        value_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes value = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearValue() {
        bitField0_ = (bitField0_ & ~0x00000004);
        value_ = getDefaultInstance().getValue();
        onChanged();
        return this;
      }

      private int version_ ;
      /**
       * <code>optional int32 version = 4;</code>
       * @return Whether the version field is set.
       */
      @Override
      public boolean hasVersion() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>optional int32 version = 4;</code>
       * @return The version.
       */
      @Override
      public int getVersion() {
        return version_;
      }
      /**
       * <code>optional int32 version = 4;</code>
       * @param value The version to set.
       * @return This builder for chaining.
       */
      public Builder setVersion(int value) {

        version_ = value;
        bitField0_ |= 0x00000008;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 version = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearVersion() {
        bitField0_ = (bitField0_ & ~0x00000008);
        version_ = 0;
        onChanged();
        return this;
      }
      @Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:KVRequest)
    }

    // @@protoc_insertion_point(class_scope:KVRequest)
    private static final KVRequest DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new KVRequest();
    }

    public static KVRequest getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<KVRequest>
        PARSER = new com.google.protobuf.AbstractParser<KVRequest>() {
      @Override
      public KVRequest parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<KVRequest> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<KVRequest> getParserForType() {
      return PARSER;
    }

    @Override
    public KVRequest getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_KVRequest_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_KVRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\025KeyValueRequest.proto\"v\n\tKVRequest\022\017\n\007" +
      "command\030\001 \001(\r\022\020\n\003key\030\002 \001(\014H\000\210\001\001\022\022\n\005value" +
      "\030\003 \001(\014H\001\210\001\001\022\024\n\007version\030\004 \001(\005H\002\210\001\001B\006\n\004_ke" +
      "yB\010\n\006_valueB\n\n\010_versionB/\n\034ca.NetSysLab." +
      "ProtocolBuffersB\017KeyValueRequestb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_KVRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_KVRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_KVRequest_descriptor,
        new String[] { "Command", "Key", "Value", "Version", "Key", "Value", "Version", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
