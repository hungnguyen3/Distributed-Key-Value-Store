// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: KeyValueResponse.proto

package ca.NetSysLab.ProtocolBuffers;

public final class KeyValueResponse {
  private KeyValueResponse() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface KVResponseOrBuilder extends
      // @@protoc_insertion_point(interface_extends:KVResponse)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>uint32 errCode = 1;</code>
     * @return The errCode.
     */
    int getErrCode();

    /**
     * <code>optional bytes value = 2;</code>
     * @return Whether the value field is set.
     */
    boolean hasValue();
    /**
     * <code>optional bytes value = 2;</code>
     * @return The value.
     */
    com.google.protobuf.ByteString getValue();

    /**
     * <code>optional int32 pid = 3;</code>
     * @return Whether the pid field is set.
     */
    boolean hasPid();
    /**
     * <code>optional int32 pid = 3;</code>
     * @return The pid.
     */
    int getPid();

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

    /**
     * <code>optional int32 overloadWaitTime = 5;</code>
     * @return Whether the overloadWaitTime field is set.
     */
    boolean hasOverloadWaitTime();
    /**
     * <code>optional int32 overloadWaitTime = 5;</code>
     * @return The overloadWaitTime.
     */
    int getOverloadWaitTime();

    /**
     * <code>optional int32 membershipCount = 6;</code>
     * @return Whether the membershipCount field is set.
     */
    boolean hasMembershipCount();
    /**
     * <code>optional int32 membershipCount = 6;</code>
     * @return The membershipCount.
     */
    int getMembershipCount();
  }
  /**
   * Protobuf type {@code KVResponse}
   */
  public static final class KVResponse extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:KVResponse)
      KVResponseOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use KVResponse.newBuilder() to construct.
    private KVResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private KVResponse() {
      value_ = com.google.protobuf.ByteString.EMPTY;
    }

    @Override
    @SuppressWarnings({"unused"})
    protected Object newInstance(
        UnusedPrivateParameter unused) {
      return new KVResponse();
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return KeyValueResponse.internal_static_KVResponse_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return KeyValueResponse.internal_static_KVResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              KVResponse.class, Builder.class);
    }

    private int bitField0_;
    public static final int ERRCODE_FIELD_NUMBER = 1;
    private int errCode_ = 0;
    /**
     * <code>uint32 errCode = 1;</code>
     * @return The errCode.
     */
    @Override
    public int getErrCode() {
      return errCode_;
    }

    public static final int VALUE_FIELD_NUMBER = 2;
    private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>optional bytes value = 2;</code>
     * @return Whether the value field is set.
     */
    @Override
    public boolean hasValue() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional bytes value = 2;</code>
     * @return The value.
     */
    @Override
    public com.google.protobuf.ByteString getValue() {
      return value_;
    }

    public static final int PID_FIELD_NUMBER = 3;
    private int pid_ = 0;
    /**
     * <code>optional int32 pid = 3;</code>
     * @return Whether the pid field is set.
     */
    @Override
    public boolean hasPid() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional int32 pid = 3;</code>
     * @return The pid.
     */
    @Override
    public int getPid() {
      return pid_;
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

    public static final int OVERLOADWAITTIME_FIELD_NUMBER = 5;
    private int overloadWaitTime_ = 0;
    /**
     * <code>optional int32 overloadWaitTime = 5;</code>
     * @return Whether the overloadWaitTime field is set.
     */
    @Override
    public boolean hasOverloadWaitTime() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional int32 overloadWaitTime = 5;</code>
     * @return The overloadWaitTime.
     */
    @Override
    public int getOverloadWaitTime() {
      return overloadWaitTime_;
    }

    public static final int MEMBERSHIPCOUNT_FIELD_NUMBER = 6;
    private int membershipCount_ = 0;
    /**
     * <code>optional int32 membershipCount = 6;</code>
     * @return Whether the membershipCount field is set.
     */
    @Override
    public boolean hasMembershipCount() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>optional int32 membershipCount = 6;</code>
     * @return The membershipCount.
     */
    @Override
    public int getMembershipCount() {
      return membershipCount_;
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
      if (errCode_ != 0) {
        output.writeUInt32(1, errCode_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeBytes(2, value_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeInt32(3, pid_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        output.writeInt32(4, version_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        output.writeInt32(5, overloadWaitTime_);
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        output.writeInt32(6, membershipCount_);
      }
      getUnknownFields().writeTo(output);
    }

    @Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (errCode_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, errCode_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, value_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, pid_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, version_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, overloadWaitTime_);
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(6, membershipCount_);
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
      if (!(obj instanceof KVResponse)) {
        return super.equals(obj);
      }
      KVResponse other = (KVResponse) obj;

      if (getErrCode()
          != other.getErrCode()) return false;
      if (hasValue() != other.hasValue()) return false;
      if (hasValue()) {
        if (!getValue()
            .equals(other.getValue())) return false;
      }
      if (hasPid() != other.hasPid()) return false;
      if (hasPid()) {
        if (getPid()
            != other.getPid()) return false;
      }
      if (hasVersion() != other.hasVersion()) return false;
      if (hasVersion()) {
        if (getVersion()
            != other.getVersion()) return false;
      }
      if (hasOverloadWaitTime() != other.hasOverloadWaitTime()) return false;
      if (hasOverloadWaitTime()) {
        if (getOverloadWaitTime()
            != other.getOverloadWaitTime()) return false;
      }
      if (hasMembershipCount() != other.hasMembershipCount()) return false;
      if (hasMembershipCount()) {
        if (getMembershipCount()
            != other.getMembershipCount()) return false;
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
      hash = (37 * hash) + ERRCODE_FIELD_NUMBER;
      hash = (53 * hash) + getErrCode();
      if (hasValue()) {
        hash = (37 * hash) + VALUE_FIELD_NUMBER;
        hash = (53 * hash) + getValue().hashCode();
      }
      if (hasPid()) {
        hash = (37 * hash) + PID_FIELD_NUMBER;
        hash = (53 * hash) + getPid();
      }
      if (hasVersion()) {
        hash = (37 * hash) + VERSION_FIELD_NUMBER;
        hash = (53 * hash) + getVersion();
      }
      if (hasOverloadWaitTime()) {
        hash = (37 * hash) + OVERLOADWAITTIME_FIELD_NUMBER;
        hash = (53 * hash) + getOverloadWaitTime();
      }
      if (hasMembershipCount()) {
        hash = (37 * hash) + MEMBERSHIPCOUNT_FIELD_NUMBER;
        hash = (53 * hash) + getMembershipCount();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static KVResponse parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static KVResponse parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static KVResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static KVResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static KVResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static KVResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static KVResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static KVResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static KVResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static KVResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static KVResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static KVResponse parseFrom(
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
    public static Builder newBuilder(KVResponse prototype) {
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
     * Protobuf type {@code KVResponse}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:KVResponse)
        KVResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return KeyValueResponse.internal_static_KVResponse_descriptor;
      }

      @Override
      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return KeyValueResponse.internal_static_KVResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                KVResponse.class, Builder.class);
      }

      // Construct using ca.NetSysLab.ProtocolBuffers.KeyValueResponse.KVResponse.newBuilder()
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
        errCode_ = 0;
        value_ = com.google.protobuf.ByteString.EMPTY;
        pid_ = 0;
        version_ = 0;
        overloadWaitTime_ = 0;
        membershipCount_ = 0;
        return this;
      }

      @Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return KeyValueResponse.internal_static_KVResponse_descriptor;
      }

      @Override
      public KVResponse getDefaultInstanceForType() {
        return KVResponse.getDefaultInstance();
      }

      @Override
      public KVResponse build() {
        KVResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @Override
      public KVResponse buildPartial() {
        KVResponse result = new KVResponse(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(KVResponse result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.errCode_ = errCode_;
        }
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.value_ = value_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.pid_ = pid_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000008) != 0)) {
          result.version_ = version_;
          to_bitField0_ |= 0x00000004;
        }
        if (((from_bitField0_ & 0x00000010) != 0)) {
          result.overloadWaitTime_ = overloadWaitTime_;
          to_bitField0_ |= 0x00000008;
        }
        if (((from_bitField0_ & 0x00000020) != 0)) {
          result.membershipCount_ = membershipCount_;
          to_bitField0_ |= 0x00000010;
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
        if (other instanceof KVResponse) {
          return mergeFrom((KVResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(KVResponse other) {
        if (other == KVResponse.getDefaultInstance()) return this;
        if (other.getErrCode() != 0) {
          setErrCode(other.getErrCode());
        }
        if (other.hasValue()) {
          setValue(other.getValue());
        }
        if (other.hasPid()) {
          setPid(other.getPid());
        }
        if (other.hasVersion()) {
          setVersion(other.getVersion());
        }
        if (other.hasOverloadWaitTime()) {
          setOverloadWaitTime(other.getOverloadWaitTime());
        }
        if (other.hasMembershipCount()) {
          setMembershipCount(other.getMembershipCount());
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
                errCode_ = input.readUInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 18: {
                value_ = input.readBytes();
                bitField0_ |= 0x00000002;
                break;
              } // case 18
              case 24: {
                pid_ = input.readInt32();
                bitField0_ |= 0x00000004;
                break;
              } // case 24
              case 32: {
                version_ = input.readInt32();
                bitField0_ |= 0x00000008;
                break;
              } // case 32
              case 40: {
                overloadWaitTime_ = input.readInt32();
                bitField0_ |= 0x00000010;
                break;
              } // case 40
              case 48: {
                membershipCount_ = input.readInt32();
                bitField0_ |= 0x00000020;
                break;
              } // case 48
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

      private int errCode_ ;
      /**
       * <code>uint32 errCode = 1;</code>
       * @return The errCode.
       */
      @Override
      public int getErrCode() {
        return errCode_;
      }
      /**
       * <code>uint32 errCode = 1;</code>
       * @param value The errCode to set.
       * @return This builder for chaining.
       */
      public Builder setErrCode(int value) {

        errCode_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <code>uint32 errCode = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrCode() {
        bitField0_ = (bitField0_ & ~0x00000001);
        errCode_ = 0;
        onChanged();
        return this;
      }

      private com.google.protobuf.ByteString value_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>optional bytes value = 2;</code>
       * @return Whether the value field is set.
       */
      @Override
      public boolean hasValue() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional bytes value = 2;</code>
       * @return The value.
       */
      @Override
      public com.google.protobuf.ByteString getValue() {
        return value_;
      }
      /**
       * <code>optional bytes value = 2;</code>
       * @param value The value to set.
       * @return This builder for chaining.
       */
      public Builder setValue(com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        value_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <code>optional bytes value = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearValue() {
        bitField0_ = (bitField0_ & ~0x00000002);
        value_ = getDefaultInstance().getValue();
        onChanged();
        return this;
      }

      private int pid_ ;
      /**
       * <code>optional int32 pid = 3;</code>
       * @return Whether the pid field is set.
       */
      @Override
      public boolean hasPid() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional int32 pid = 3;</code>
       * @return The pid.
       */
      @Override
      public int getPid() {
        return pid_;
      }
      /**
       * <code>optional int32 pid = 3;</code>
       * @param value The pid to set.
       * @return This builder for chaining.
       */
      public Builder setPid(int value) {

        pid_ = value;
        bitField0_ |= 0x00000004;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 pid = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearPid() {
        bitField0_ = (bitField0_ & ~0x00000004);
        pid_ = 0;
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

      private int overloadWaitTime_ ;
      /**
       * <code>optional int32 overloadWaitTime = 5;</code>
       * @return Whether the overloadWaitTime field is set.
       */
      @Override
      public boolean hasOverloadWaitTime() {
        return ((bitField0_ & 0x00000010) != 0);
      }
      /**
       * <code>optional int32 overloadWaitTime = 5;</code>
       * @return The overloadWaitTime.
       */
      @Override
      public int getOverloadWaitTime() {
        return overloadWaitTime_;
      }
      /**
       * <code>optional int32 overloadWaitTime = 5;</code>
       * @param value The overloadWaitTime to set.
       * @return This builder for chaining.
       */
      public Builder setOverloadWaitTime(int value) {

        overloadWaitTime_ = value;
        bitField0_ |= 0x00000010;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 overloadWaitTime = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearOverloadWaitTime() {
        bitField0_ = (bitField0_ & ~0x00000010);
        overloadWaitTime_ = 0;
        onChanged();
        return this;
      }

      private int membershipCount_ ;
      /**
       * <code>optional int32 membershipCount = 6;</code>
       * @return Whether the membershipCount field is set.
       */
      @Override
      public boolean hasMembershipCount() {
        return ((bitField0_ & 0x00000020) != 0);
      }
      /**
       * <code>optional int32 membershipCount = 6;</code>
       * @return The membershipCount.
       */
      @Override
      public int getMembershipCount() {
        return membershipCount_;
      }
      /**
       * <code>optional int32 membershipCount = 6;</code>
       * @param value The membershipCount to set.
       * @return This builder for chaining.
       */
      public Builder setMembershipCount(int value) {

        membershipCount_ = value;
        bitField0_ |= 0x00000020;
        onChanged();
        return this;
      }
      /**
       * <code>optional int32 membershipCount = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearMembershipCount() {
        bitField0_ = (bitField0_ & ~0x00000020);
        membershipCount_ = 0;
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


      // @@protoc_insertion_point(builder_scope:KVResponse)
    }

    // @@protoc_insertion_point(class_scope:KVResponse)
    private static final KVResponse DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new KVResponse();
    }

    public static KVResponse getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<KVResponse>
        PARSER = new com.google.protobuf.AbstractParser<KVResponse>() {
      @Override
      public KVResponse parsePartialFrom(
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

    public static com.google.protobuf.Parser<KVResponse> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<KVResponse> getParserForType() {
      return PARSER;
    }

    @Override
    public KVResponse getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_KVResponse_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_KVResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\026KeyValueResponse.proto\"\335\001\n\nKVResponse\022" +
      "\017\n\007errCode\030\001 \001(\r\022\022\n\005value\030\002 \001(\014H\000\210\001\001\022\020\n\003" +
      "pid\030\003 \001(\005H\001\210\001\001\022\024\n\007version\030\004 \001(\005H\002\210\001\001\022\035\n\020" +
      "overloadWaitTime\030\005 \001(\005H\003\210\001\001\022\034\n\017membershi" +
      "pCount\030\006 \001(\005H\004\210\001\001B\010\n\006_valueB\006\n\004_pidB\n\n\010_" +
      "versionB\023\n\021_overloadWaitTimeB\022\n\020_members" +
      "hipCountB0\n\034ca.NetSysLab.ProtocolBuffers" +
      "B\020KeyValueResponseb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_KVResponse_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_KVResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_KVResponse_descriptor,
        new String[] { "ErrCode", "Value", "Pid", "Version", "OverloadWaitTime", "MembershipCount", "Value", "Pid", "Version", "OverloadWaitTime", "MembershipCount", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
