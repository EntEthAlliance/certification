package org.eea.certification.evm;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.fluent.SimpleAccount;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.log.Log;
import org.hyperledger.besu.evm.log.LogTopic;

public class JsonModule extends SimpleModule {

  static class AccountSerializer extends StdSerializer<SimpleAccount> {

    AccountSerializer() {
      super(SimpleAccount.class);
    }

    @Override
    public void serialize(SimpleAccount value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeStartObject();
      gen.writeFieldName("address");
      gen.writeString(value.getAddress().toHexString());
      gen.writeFieldName("balance");
      gen.writeString(value.getBalance().toShortHexString());
      gen.writeFieldName("code");
      gen.writeString(value.getCode().toHexString());
      gen.writeFieldName("nonce");
      gen.writeString(Bytes.ofUnsignedLong(value.getNonce()).toShortHexString());
      gen.writeArrayFieldStart("storage");
      for (Map.Entry<UInt256, UInt256> entry : value.getUpdatedStorage().entrySet()) {
        gen.writeStartObject();
        gen.writeStringField("key", entry.getKey().toHexString());
        gen.writeStringField("value", entry.getValue().toHexString());
        gen.writeEndObject();
      }
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  static class WeiSerializer extends StdSerializer<Wei> {

    WeiSerializer() {
      super(Wei.class);
    }

    @Override
    public void serialize(Wei value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(value.toShortHexString());
    }
  }

  static class BytesSerializer extends StdSerializer<Bytes> {

    BytesSerializer() {
      super(Bytes.class);
    }

    @Override
    public void serialize(Bytes value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString(value.toHexString());
    }
  }

  static class OptionalSerializer extends StdSerializer<Optional> {

    OptionalSerializer() {
      super(Optional.class);
    }

    @Override
    public void serialize(Optional value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeString((String) value.map(Object::toString).orElse(null));
    }
  }

  static class LogDeserializer extends StdDeserializer<Log> {

    protected LogDeserializer() {
      super(Log.class);
    }

    @Override
    public Log deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      String logger = null;
      String data = null;
      List<String> topicsStr = null;
      while (p.nextToken() != JsonToken.END_OBJECT) {
        String name = p.getCurrentName();
        if ("logger".equals(name)) {
          p.nextToken();
          logger = p.getText();
        } else if ("data".equals(name)) {
          p.nextToken();
          data = p.getText();
        } else if ("topics".equals(name)) {
          p.nextToken();
          topicsStr = p.readValueAs(List.class);
        }
      }
      List<LogTopic> topics = new ArrayList<>();
      for (String t : topicsStr) {
        topics.add(LogTopic.fromHexString(t));
      }
      return new Log(Address.fromHexString(logger), Bytes.fromHexString(data), topics);
    }
  }

  static class ExceptionalHaltReasonDeserializer extends StdDeserializer<ExceptionalHaltReason> {

    protected ExceptionalHaltReasonDeserializer() {
      super(ExceptionalHaltReason.class);
    }

    @Override
    public ExceptionalHaltReason deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return ExceptionalHaltReason.DefaultExceptionalHaltReason.valueOf(p.getValueAsString());
    }

  }

  static class Bytes32Deserializer extends StdDeserializer<Bytes32> {

    protected Bytes32Deserializer() {
      super(Bytes32.class);
    }

    @Override
    public Bytes32 deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return Bytes32.fromHexString(p.getValueAsString());
    }

  }

  static class AccountDeserializer extends StdDeserializer<Account> {

    protected AccountDeserializer() {
      super(Account.class);
    }

    @Override
    public Account deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      Address address = null;
      long nonce = 0L;
      Wei balance = null;
      Bytes code = null;
      while (p.nextToken() != JsonToken.END_OBJECT) {
        String name = p.getCurrentName();
        if ("address".equals(name)) {
          p.nextToken();
          address = Address.fromHexString(p.getText());
        } else if ("nonce".equals(name)) {
          p.nextToken();
          nonce = Bytes.fromHexStringLenient(p.getText()).toLong();
        } else if ("balance".equals(name)) {
          p.nextToken();
          balance = Wei.fromHexString(p.getText());
        } else if ("code".equals(name)) {
          p.nextToken();
          code = Bytes.fromHexString(p.getText());
        }
      }
      SimpleAccount account = new SimpleAccount(address, nonce, balance);
      account.setCode(code);
      return account;
    }

  }

  public JsonModule() {
    addSerializer(new AccountSerializer());
    addSerializer(new WeiSerializer());
    addSerializer(new BytesSerializer());
    addSerializer(new OptionalSerializer());
    addDeserializer(Log.class, new LogDeserializer());
    addDeserializer(ExceptionalHaltReason.class, new ExceptionalHaltReasonDeserializer());
    addDeserializer(Account.class, new AccountDeserializer());
    addDeserializer(Bytes32.class, new Bytes32Deserializer());
  }
}
