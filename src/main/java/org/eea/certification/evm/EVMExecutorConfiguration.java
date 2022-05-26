package org.eea.certification.evm;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.operation.OperationRegistry;
import org.hyperledger.besu.evm.precompile.PrecompileContractRegistry;

public class EVMExecutorConfiguration {

  final EVM evm;
  private final PrecompileContractRegistry precompileContractRegistry;
  private final OperationRegistry operationsRegistry;
  private final String hardFork;

  EVMExecutorConfiguration(
      String hardFork,
      EVM evm,
      PrecompileContractRegistry precompileContractRegistry,
      OperationRegistry operationRegistry) {
    this.evm = evm;
    this.precompileContractRegistry = precompileContractRegistry;
    this.operationsRegistry = operationRegistry;
    this.hardFork = hardFork;
  }

  public OperationRegistry getOperationsRegistry() {
    return operationsRegistry;
  }

  public PrecompileContractRegistry getPrecompileContractRegistry() {
    return precompileContractRegistry;
  }

  public EVM getEvm() {
    return evm;
  }

  public String getHardFork() {
    return hardFork;
  }
}
