package org.eea.certification.evm;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.MainnetEVMs;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.fluent.SimpleAccount;
import org.hyperledger.besu.evm.frame.BlockValues;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.log.Log;

/**
 * Model representing a test of an EVM opcode.
 * <p>
 * This model can be serialized into a YAML document, to be consumed by implementers.
 */
@JsonPropertyOrder(value = {
    "name",
    "hardFork",
    "index",
    "before",
    "after",
    "sender",
    "receiver",
    "inputData",
    "value",
    "code",
    "gasPrice",
    "gasUsed",
    "allGasUsed",
    "gasAvailable",
    "gasLimit",
    "haltReason",
    "coinbase",
    "refunds",
    "number",
    "timestamp",
    "mixHashOrPrevRandao",
    "baseFee",
    "chainId",
    "difficultyBytes"})
public class OpcodeTestModel {

  private final String hardFork;
  private final List<Account> accounts;
  private final String name;
  private final long gasUsed;
  private final Object haltReason;
  private final List<Account> post;
  private final Bytes inputData;
  private final Wei gasPrice;
  private final List<Log> logs;
  private final List<Bytes> stackBefore;
  private final List<Bytes32> memoryBefore;
  private final List<Bytes> stackAfter;
  private final List<Bytes32> memoryAfter;
  private final BlockValues blockData;
  private final Address coinbase;
  private final long gasAvailable;
  private final long allGasUsed;
  private final Map<Address, Wei> refunds;
  private final Address receiver;
  private final UInt256 chainId;
  private int index;
  private final Address sender;
  private final Wei value;
  private final Bytes code;

  public static OpcodeTestModel fromJsonReferenceTest(String hardFork, String name, JsonReferenceTest test) {

    List<Account> preAccounts = new ArrayList<>();
    for (Map.Entry<Address, JsonReferenceTest.JsonAccountState> entry : test.getPre().entrySet()) {
      JsonReferenceTest.JsonAccountState state = entry.getValue();
      SimpleAccount account =
          new SimpleAccount(entry.getKey(), entry.getValue().getNonce().toLong(), entry.getValue().getBalance());
      for (Map.Entry<UInt256, UInt256> storageEntry : state.getStorage().entrySet()) {
        account.setStorageValue(storageEntry.getKey(), storageEntry.getValue());
      }
      account.setCode(state.getCode());
      preAccounts.add(account);
    }

    if (!test.getPre().containsKey(test.getExec().getCaller())) {
      SimpleAccount account = new SimpleAccount(test.getExec().getCaller(), 0L, Wei.wrap(test.getExec().getValue()));
      preAccounts.add(account);
    }

    List<Account> postAccounts = new ArrayList<>();
    if (test.getPost() != null) {
      for (Map.Entry<Address, JsonReferenceTest.JsonAccountState> entry : test.getPost().entrySet()) {
        postAccounts
            .add(
                new SimpleAccount(entry.getKey(), entry.getValue().getNonce().toLong(), entry.getValue().getBalance()));
      }
    }

    // we mangle the after state, we will recreate it anyway.
    After after = new After(new ArrayList<>(), new ArrayList<>(), postAccounts, new ArrayList<>());

    Before before = new Before(new ArrayList<>(), new ArrayList<>(), preAccounts);
    return new OpcodeTestModel(
        hardFork,
        name,
        after,
        before,
        test.getExec().getData(),
        test.getExec().getGasPrice(),
        Bytes.ofUnsignedLong(test.getExec().getGas()).toHexString(), // gas used by opcode, will be recomputed
        Bytes.ofUnsignedLong(test.getExec().getGas()).toHexString(), // all gas used, will be recomputed
        Bytes.ofUnsignedLong(test.getGas()).toHexString(),
        new HashMap<>(),
        ExceptionalHaltReason.NONE,
        test.getEnv().getCurrentDifficulty(),
        Bytes32.ZERO,
        test.getEnv().getCurrentGasLimit().toLong(),
        test.getEnv().getCurrentTimestamp().toLong(),
        Wei.ZERO,
        test.getEnv().getCurrentNumber().toLong(),
        test.getExec().getCaller(),
        test.getExec().getAddress(),
        Wei.wrap(test.getExec().getValue()),
        test.getExec().getCode(),
        test.getEnv().getCurrentCoinbase(),
        UInt256.valueOf(MainnetEVMs.DEV_NET_CHAIN_ID));
  }

  public String getHardFork() {
    return hardFork;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  @JsonPropertyOrder(alphabetic = true)
  public static class Before {

    private final List<Bytes> stackBefore;
    private final List<Bytes32> memoryBefore;
    private final List<Account> accounts;

    @JsonCreator
    public Before(
        @JsonProperty("stack") List<Bytes> stackBefore,
        @JsonProperty("memory") List<Bytes32> memoryBefore,
        @JsonProperty("accounts") List<Account> accounts) {
      this.stackBefore = stackBefore;
      this.memoryBefore = memoryBefore;
      this.accounts = accounts;
    }

    public List<Bytes> getStack() {
      return stackBefore;
    }

    public List<Bytes32> getMemory() {
      return memoryBefore;
    }

    public List<Account> getAccounts() {
      return accounts;
    }
  }

  @JsonPropertyOrder(alphabetic = true)
  public static class After {

    private final List<Bytes> stackAfter;
    private final List<Bytes32> memoryAfter;
    private final List<Account> accounts;
    private final List<Log> logs;

    @JsonCreator
    public After(
        @JsonProperty("stack") List<Bytes> stackAfter,
        @JsonProperty("memory") List<Bytes32> memoryAfter,
        @JsonProperty("accounts") List<Account> accounts,
        @JsonProperty("logs") List<Log> logs) {
      this.stackAfter = stackAfter;
      this.memoryAfter = memoryAfter;
      this.accounts = accounts;
      this.logs = logs;
    }

    public List<Bytes> getStack() {
      return stackAfter;
    }

    public List<Bytes32> getMemory() {
      return memoryAfter;
    }

    public List<Account> getAccounts() {
      return accounts;
    }

    public List<Log> getLogs() {
      return logs;
    }
  }

  @JsonCreator
  public OpcodeTestModel(
      @JsonProperty("hardFork") String hardFork,
      @JsonProperty("name") String name,
      @JsonProperty("after") After after,
      @JsonProperty("before") Before before,
      @JsonProperty("inputData") Bytes inputData,
      @JsonProperty("gasPrice") Wei gasPrice,
      @JsonProperty("gasAvailable") String gasAvailable,
      @JsonProperty("gasUsed") String gasUsed,
      @JsonProperty("allGasUsed") String allGasUsed,
      @JsonProperty("refunds") Map<Address, Wei> refunds,
      @JsonProperty("haltReason") ExceptionalHaltReason exceptionalHaltReason,
      @JsonProperty("difficultyBytes") Bytes difficultyBytes,
      @JsonProperty("mixHashOrPrevRandao") Bytes32 mixHashOrPrevRandao,
      @JsonProperty("gasLimit") long gasLimit,
      @JsonProperty("timestamp") long timestamp,
      @JsonProperty("baseFee") Wei baseFee,
      @JsonProperty("number") long number,
      @JsonProperty("sender") Address sender,
      @JsonProperty("receiver") Address receiver,
      @JsonProperty("value") Wei value,
      @JsonProperty("code") Bytes code,
      @JsonProperty("coinbase") Address coinbase,
      @JsonProperty("chainId") UInt256 chainId) {
    this.hardFork = hardFork;
    this.accounts = before.getAccounts();
    this.inputData = inputData;
    this.gasPrice = gasPrice;
    this.gasAvailable = Bytes.fromHexStringLenient(gasAvailable).toLong();
    this.gasUsed = Bytes.fromHexStringLenient(gasUsed).toLong();
    this.allGasUsed = Bytes.fromHexStringLenient(allGasUsed).toLong();
    this.refunds = refunds;
    this.haltReason = exceptionalHaltReason;
    this.blockData = new SettableBlockValues(
        difficultyBytes,
        mixHashOrPrevRandao,
        gasLimit,
        number,
        timestamp,
        Optional.ofNullable(baseFee));
    this.sender = sender;
    this.receiver = receiver;
    this.value = value;
    this.code = code;
    this.coinbase = coinbase;
    this.chainId = chainId;
    this.post = after.getAccounts();
    this.logs = after.getLogs();
    this.memoryAfter = after.getMemory();
    this.memoryBefore = before.getMemory();
    this.stackAfter = after.getStack();
    this.stackBefore = before.getStack();
    this.name = name;
  }

  public OpcodeTestModel(
      String hardFork,
      List<Account> accounts,
      String name,
      List<Bytes> stackAfter,
      List<Bytes32> memoryAfter,
      List<Bytes> stackBefore,
      List<Bytes32> memoryBefore,
      Bytes inputData,
      Wei gasPrice,
      List<Log> logs,
      long gasAvailable,
      OptionalLong gasUsed,
      long allGasUsed,
      Map<Address, Wei> refunds,
      ExceptionalHaltReason exceptionalHaltReason,
      Collection<? extends Account> touchedAccounts,
      BlockValues blockValues,
      Address sender,
      Address receiver,
      Wei value,
      Bytes code,
      Address coinbase,
      UInt256 chainId) {
    this.accounts = accounts;
    this.name = name;
    this.gasAvailable = gasAvailable;
    this.gasUsed = gasUsed.orElse(0L);
    this.allGasUsed = allGasUsed;
    this.refunds = refunds;
    this.haltReason = exceptionalHaltReason;
    this.post = new ArrayList<>(touchedAccounts);
    this.inputData = inputData;
    this.gasPrice = gasPrice;
    this.logs = logs;
    this.stackBefore = stackBefore;
    this.memoryBefore = memoryBefore;
    this.stackAfter = stackAfter;
    this.memoryAfter = memoryAfter;
    this.hardFork = hardFork;
    this.blockData = blockValues;
    this.sender = sender;
    this.value = value;
    this.code = code;
    this.coinbase = coinbase;
    this.receiver = receiver;
    this.chainId = chainId;
  }

  public Address getReceiver() {
    return receiver;
  }

  public String getName() {
    return name;
  }

  public long getGasUsed() {
    return gasUsed;
  }

  public Object getHaltReason() {
    return haltReason;
  }

  public Bytes getInputData() {
    return inputData;
  }

  public Wei getGasPrice() {
    return gasPrice;
  }

  public After getAfter() {
    return new After(stackAfter, memoryAfter, post, logs);
  }

  public Before getBefore() {
    return new Before(stackBefore, memoryBefore, accounts);
  }

  public Wei getValue() {
    return value;
  }

  public Address getSender() {
    return sender;
  }

  public Bytes getCode() {
    return code;
  }

  public Address getCoinbase() {
    return coinbase;
  }

  public long getGasAvailable() {
    return gasAvailable;
  }

  public Map<Address, Wei> getRefunds() {
    return refunds;
  }

  public long getAllGasUsed() {
    return allGasUsed;
  }

  public UInt256 getChainId() {
    return chainId;
  }

  public Bytes getDifficultyBytes() {
    return blockData.getDifficultyBytes();
  }

  public Bytes32 getMixHashOrPrevRandao() {
    return blockData.getMixHashOrPrevRandao();
  }

  public Optional<Wei> getBaseFee() {
    return blockData.getBaseFee();
  }

  public long getNumber() {
    return blockData.getNumber();
  }

  public long getTimestamp() {
    return blockData.getTimestamp();
  }

  public long getGasLimit() {
    return blockData.getGasLimit();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    OpcodeTestModel that = (OpcodeTestModel) o;
    return index == that.index
        && Objects.equals(hardFork, that.hardFork)
        && Objects.equals(accounts, that.accounts)
        && Objects.equals(name, that.name)
        && Objects.equals(gasUsed, that.gasUsed)
        && Objects.equals(haltReason, that.haltReason)
        && Objects.equals(post, that.post)
        && Objects.equals(inputData, that.inputData)
        && Objects.equals(gasPrice, that.gasPrice)
        && Objects.equals(logs, that.logs)
        && Objects.equals(stackBefore, that.stackBefore)
        && Objects.equals(memoryBefore, that.memoryBefore)
        && Objects.equals(stackAfter, that.stackAfter)
        && Objects.equals(memoryAfter, that.memoryAfter)
        && Objects.equals(blockData, that.blockData)
        && Objects.equals(coinbase, that.coinbase)
        && Objects.equals(gasAvailable, that.gasAvailable)
        && Objects.equals(allGasUsed, that.allGasUsed)
        && Objects.equals(refunds, that.refunds)
        && Objects.equals(receiver, that.receiver)
        && Objects.equals(chainId, that.chainId)
        && Objects.equals(sender, that.sender)
        && Objects.equals(value, that.value)
        && Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(
            hardFork,
            accounts,
            name,
            gasUsed,
            haltReason,
            post,
            inputData,
            gasPrice,
            logs,
            stackBefore,
            memoryBefore,
            stackAfter,
            memoryAfter,
            blockData,
            coinbase,
            gasAvailable,
            allGasUsed,
            refunds,
            receiver,
            chainId,
            index,
            sender,
            value,
            code);
  }

  @Override
  public String toString() {
    return "OpcodeTestModel{"
        + "hardFork='"
        + hardFork
        + '\''
        + ", accounts="
        + accounts
        + ", name="
        + name
        + ", gasUsed="
        + gasUsed
        + ", haltReason="
        + haltReason
        + ", post="
        + post
        + ", inputData="
        + inputData
        + ", gasPrice="
        + gasPrice
        + ", logs="
        + logs
        + ", stackBefore="
        + stackBefore
        + ", memoryBefore="
        + memoryBefore
        + ", stackAfter="
        + stackAfter
        + ", memoryAfter="
        + memoryAfter
        + ", blockData="
        + blockData
        + ", coinbase="
        + coinbase
        + ", gasAvailable="
        + gasAvailable
        + ", allGasUsed="
        + allGasUsed
        + ", refunds="
        + refunds
        + ", receiver="
        + receiver
        + ", chainId="
        + chainId
        + ", index="
        + index
        + ", sender="
        + sender
        + ", value="
        + value
        + ", code="
        + code
        + '}';
  }
}
