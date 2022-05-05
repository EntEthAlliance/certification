package org.eea.certification.evm;

import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.frame.BlockValues;

import java.util.Optional;

public class SettableBlockValues implements BlockValues {

  private final Bytes difficulty;
  private final Bytes32 mixHash;
  private final long gasLimit;
  private final long number;
  private final long timestamp;
  private final Optional<Wei> baseFee;

  public SettableBlockValues(Bytes difficulty, Bytes32 mixHash, long gasLimit, long number, long timestamp, Optional<Wei> baseFee) {
    this.difficulty = difficulty;
    this.mixHash = mixHash;
    this.gasLimit = gasLimit;
    this.number = number;
    this.timestamp = timestamp;
    this.baseFee = baseFee;
  }

  @Override
  public Bytes getDifficultyBytes() {
    return difficulty;
  }

  @Override
  public Bytes32 getMixHashOrPrevRandao() {
    return mixHash;
  }

  @Override
  public long getGasLimit() {
    return gasLimit;
  }

  @Override
  public long getNumber() {
    return number;
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public Optional<Wei> getBaseFee() {
    return baseFee;
  }
}
