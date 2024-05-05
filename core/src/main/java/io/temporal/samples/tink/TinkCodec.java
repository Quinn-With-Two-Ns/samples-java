/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.samples.tink;

import com.google.crypto.tink.Aead;
import com.google.protobuf.ByteString;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.DataConverterException;
import io.temporal.common.converter.EncodingKeys;
import io.temporal.payload.codec.PayloadCodec;
import io.temporal.payload.codec.PayloadCodecException;
import io.temporal.payload.context.ActivitySerializationContext;
import io.temporal.payload.context.HasWorkflowSerializationContext;
import io.temporal.payload.context.SerializationContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class TinkCodec implements PayloadCodec {
  private static final byte[] EMPTY_ASSOCIATED_DATA = new byte[0];
  private static final Charset UTF_8 = StandardCharsets.UTF_8;

  static final ByteString METADATA_ENCODING = ByteString.copyFrom("binary/tink/encrypted", UTF_8);

  private final Aead aead;

  private final SerializationContext serializationContext;

  public TinkCodec(Aead aead) {
    Objects.requireNonNull(aead, "Aead is null");
    this.aead = aead;
    this.serializationContext = null;
  }

  public TinkCodec(Aead aead, SerializationContext serializationContext) {
    Objects.requireNonNull(aead, "Aead is null");
    this.aead = aead;
    this.serializationContext = serializationContext;
  }

  @NotNull
  @Override
  public List<Payload> encode(@NotNull List<Payload> payloads) {
    return payloads.stream().map(this::encodePayload).collect(Collectors.toList());
  }

  @NotNull
  @Override
  public List<Payload> decode(@NotNull List<Payload> payloads) {
    return payloads.stream().map(this::decodePayload).collect(Collectors.toList());
  }

  @NotNull
  @Override
  public PayloadCodec withContext(@Nonnull SerializationContext context) {
    return new TinkCodec(aead, context);
  }

  private Payload encodePayload(Payload payload) {
    byte[] encryptedData;
    try {
      encryptedData = aead.encrypt(payload.toByteArray(), getAssociateData());
    } catch (Throwable e) {
      throw new DataConverterException(e);
    }

    return Payload.newBuilder()
        .putMetadata(EncodingKeys.METADATA_ENCODING_KEY, METADATA_ENCODING)
        .setData(ByteString.copyFrom(encryptedData))
        .build();
  }

  private Payload decodePayload(Payload payload) {
    if (METADATA_ENCODING.equals(
        payload.getMetadataOrDefault(EncodingKeys.METADATA_ENCODING_KEY, null))) {
      byte[] plainData;
      Payload decryptedPayload;

      try {
        plainData = aead.decrypt(payload.getData().toByteArray(), getAssociateData());
        decryptedPayload = Payload.parseFrom(plainData);
        return decryptedPayload;
      } catch (Throwable e) {
        throw new PayloadCodecException(e);
      }
    } else {
      return payload;
    }
  }

  private byte[] getAssociateData() {
    if (serializationContext == null) {
      return EMPTY_ASSOCIATED_DATA;
    }
    // Create a unique signature based on the context
    String activityType = null;
    String namespace = ((HasWorkflowSerializationContext) serializationContext).getNamespace();
    String workflowId = ((HasWorkflowSerializationContext) serializationContext).getWorkflowId();
    if (serializationContext instanceof ActivitySerializationContext) {
      activityType = ((ActivitySerializationContext) serializationContext).getActivityType();
    }
    String signature =
        activityType != null ? namespace + workflowId + activityType : namespace + workflowId;
    return signature.getBytes(UTF_8);
  }
}
