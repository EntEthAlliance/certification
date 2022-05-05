package org.eea.certification.evm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.Gas;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.frame.BlockValues;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.log.Log;
import org.hyperledger.besu.evm.operation.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Model representing a test of an EVM opcode.
 * <p>
 * This model can be serialized into a YAML document, to be consumed by implementers.
 */
@JsonPropertyOrder(alphabetic = true, value = {"operation", "gas", "blockData", "gasPrice", "inputData", "haltReason", "before", "after"})
public class OpcodeTestModel {

  private final String hardFork;
  private final List<Account> accounts;
  private final Operation operation;
  private final Gas gasUsed;
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
  private final Gas gasAvailable;
  private final Gas allGasUsed;
  private final Map<Address, Wei> refunds;
  private final Address receiver;
  private final UInt256 chainId;
  private int index;
  private final Address sender;
  private final Wei value;
  private final Bytes code;

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
    public Before(@JsonProperty("stack") List<Bytes> stackBefore,@JsonProperty("memory") List<Bytes32> memoryBefore, @JsonProperty("accounts") List<Account> accounts) {
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
    public After(@JsonProperty("stack") List<Bytes> stackAfter, @JsonProperty("memory") List<Bytes32> memoryAfter, @JsonProperty("accounts") List<Account> accounts, @JsonProperty("logs") List<Log> logs) {
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
  public OpcodeTestModel(@JsonProperty("hardFork") String hardFork,
                         @JsonProperty("operation") JsonModule.OperationModel operation,
                         @JsonProperty("after") After after,
                         @JsonProperty("before") Before before,
                         @JsonProperty("inputData") Bytes inputData,
                         @JsonProperty("gasPrice") Wei gasPrice,
                         @JsonProperty("gasAvailable") String gasAvailable,
                         @JsonProperty("gasUsed") String gasUsed,
                         @JsonProperty("allGasUsed") String allGasUsed,
                         @JsonProperty("refunds")  Map<Address, Wei> refunds,
                         @JsonProperty("haltReason") ExceptionalHaltReason exceptionalHaltReason,
                         @JsonProperty("difficultyBytes")  Bytes difficultyBytes,
                         @JsonProperty("mixHashOrPrevRandao")  Bytes32 mixHashOrPrevRandao,
                         @JsonProperty("gasLimit")  long gasLimit,
                         @JsonProperty("timestamp")  long timestamp,
                         @JsonProperty("baseFee") Wei baseFee,
                         @JsonProperty("number") long number,
                         @JsonProperty("sender") Address sender,
                         @JsonProperty("receiver") Address receiver,
                         @JsonProperty("value") Wei value,
                         @JsonProperty("code") Bytes code,
                         @JsonProperty("coinbase") Address coinbase,
                         @JsonProperty("chainId") UInt256 chainId
                         ) {
    this.hardFork = hardFork;
    this.accounts = before.getAccounts();
    this.inputData = inputData;
    this.gasPrice = gasPrice;
    this.gasAvailable = Gas.fromHexString(gasAvailable);
    this.gasUsed = Gas.fromHexString(gasUsed);
    this.allGasUsed = Gas.fromHexString(allGasUsed);
    this.refunds= refunds;
    this.haltReason = exceptionalHaltReason;
    this.blockData = new SettableBlockValues(difficultyBytes, mixHashOrPrevRandao, gasLimit, number, timestamp, Optional.ofNullable(baseFee));
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
    EVMExecutor executor = EVMExecutors.registry.get(hardFork).get();

    this.operation = executor.getOperationsRegistry().get(Bytes.fromHexString(operation.opcode).get(0));
  }


  public OpcodeTestModel(String hardFork,
                         List<Account> accounts,
                         Operation operation,
                         List<Bytes> stackAfter,
                         List<Bytes32> memoryAfter,
                         List<Bytes> stackBefore,
                         List<Bytes32> memoryBefore,
                         Bytes inputData,
                         Wei gasPrice,
                         List<Log> logs,
                         Gas gasAvailable,
                         Optional<Gas> gasUsed,
                         Gas allGasUsed,
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
    this.operation = operation;
    this.gasAvailable = gasAvailable;
    this.gasUsed = gasUsed.orElse(Gas.ZERO);
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

  public Operation getOperation() {
    return operation;
  }

  public Gas getGasUsed() {
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

  public Gas getGasAvailable() {
    return gasAvailable;
  }

  public Map<Address, Wei> getRefunds() {
    return refunds;
  }

  public Gas getAllGasUsed() {
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpcodeTestModel that = (OpcodeTestModel) o;
    return index == that.index && Objects.equals(hardFork, that.hardFork) && Objects.equals(accounts, that.accounts) && Objects.equals(operation, that.operation) && Objects.equals(gasUsed, that.gasUsed) && Objects.equals(haltReason, that.haltReason) && Objects.equals(post, that.post) && Objects.equals(inputData, that.inputData) && Objects.equals(gasPrice, that.gasPrice) && Objects.equals(logs, that.logs) && Objects.equals(stackBefore, that.stackBefore) && Objects.equals(memoryBefore, that.memoryBefore) && Objects.equals(stackAfter, that.stackAfter) && Objects.equals(memoryAfter, that.memoryAfter) && Objects.equals(blockData, that.blockData) && Objects.equals(coinbase, that.coinbase) && Objects.equals(gasAvailable, that.gasAvailable) && Objects.equals(allGasUsed, that.allGasUsed) && Objects.equals(refunds, that.refunds) && Objects.equals(receiver, that.receiver) && Objects.equals(chainId, that.chainId) && Objects.equals(sender, that.sender) && Objects.equals(value, that.value) && Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hardFork, accounts, operation, gasUsed, haltReason, post, inputData, gasPrice, logs, stackBefore, memoryBefore, stackAfter, memoryAfter, blockData, coinbase, gasAvailable, allGasUsed, refunds, receiver, chainId, index, sender, value, code);
  }

  @Override
  public String toString() {
    return "OpcodeTestModel{" +
        "hardFork='" + hardFork + '\'' +
        ", accounts=" + accounts +
        ", operation=" + operation +
        ", gasUsed=" + gasUsed +
        ", haltReason=" + haltReason +
        ", post=" + post +
        ", inputData=" + inputData +
        ", gasPrice=" + gasPrice +
        ", logs=" + logs +
        ", stackBefore=" + stackBefore +
        ", memoryBefore=" + memoryBefore +
        ", stackAfter=" + stackAfter +
        ", memoryAfter=" + memoryAfter +
        ", blockData=" + blockData +
        ", coinbase=" + coinbase +
        ", gasAvailable=" + gasAvailable +
        ", allGasUsed=" + allGasUsed +
        ", refunds=" + refunds +
        ", receiver=" + receiver +
        ", chainId=" + chainId +
        ", index=" + index +
        ", sender=" + sender +
        ", value=" + value +
        ", code=" + code +
        '}';
  }
}
