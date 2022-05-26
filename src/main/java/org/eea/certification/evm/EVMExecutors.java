package org.eea.certification.evm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.MainnetEVMs;
import org.hyperledger.besu.evm.internal.EvmConfiguration;
import org.hyperledger.besu.evm.precompile.MainnetPrecompiledContracts;
import org.hyperledger.besu.evm.precompile.PrecompileContractRegistry;

public class EVMExecutors {

  public static final Supplier<EVMExecutorConfiguration> frontier = () -> {
    EVM evm = MainnetEVMs.frontier(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.frontier(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "frontier",
        evm,
        precompileContractRegistry,
        MainnetEVMs.frontierOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> homestead = () -> {
    EVM evm = MainnetEVMs.homestead(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.homestead(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "homestead",
        evm,
        precompileContractRegistry,
        MainnetEVMs.homesteadOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> spuriousDragon = () -> {
    EVM evm = MainnetEVMs.spuriousDragon(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.homestead(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "spuriousDragon",
        evm,
        precompileContractRegistry,
        MainnetEVMs.homesteadOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> tangerineWhistle = () -> {
    EVM evm = MainnetEVMs.tangerineWhistle(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.homestead(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "tangerineWhistle",
        evm,
        precompileContractRegistry,
        MainnetEVMs.homesteadOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> byzantium = () -> {
    EVM evm = MainnetEVMs.byzantium(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.byzantium(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "byzantium",
        evm,
        precompileContractRegistry,
        MainnetEVMs.byzantiumOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> constantinople = () -> {
    EVM evm = MainnetEVMs.constantinople(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.byzantium(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "constantinople",
        evm,
        precompileContractRegistry,
        MainnetEVMs.constantinopleOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> petersburg = () -> {
    EVM evm = MainnetEVMs.petersburg(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.byzantium(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "petersburg",
        evm,
        precompileContractRegistry,
        MainnetEVMs.constantinopleOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutorConfiguration> istanbul = () -> {
    EVM evm = MainnetEVMs.istanbul(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "istanbul",
        evm,
        precompileContractRegistry,
        MainnetEVMs.istanbulOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Supplier<EVMExecutorConfiguration> berlin = () -> {
    EVM evm = MainnetEVMs.berlin(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "berlin",
        evm,
        precompileContractRegistry,
        MainnetEVMs.istanbulOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Supplier<EVMExecutorConfiguration> london = () -> {
    EVM evm = MainnetEVMs.london(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "london",
        evm,
        precompileContractRegistry,
        MainnetEVMs.londonOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Supplier<EVMExecutorConfiguration> paris = () -> {
    EVM evm = MainnetEVMs.paris(MainnetEVMs.DEV_NET_CHAIN_ID, EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry =
        MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutorConfiguration(
        "premerge",
        evm,
        precompileContractRegistry,
        MainnetEVMs.parisOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Map<String, Supplier<EVMExecutorConfiguration>> registry = new HashMap<>();

  static {
    registry.put("frontier", frontier);
    registry.put("homestead", homestead);
    registry.put("spuriousDragon", spuriousDragon);
    registry.put("tangerineWhistle", tangerineWhistle);
    registry.put("byzantium", byzantium);
    registry.put("constantinople", constantinople);
    registry.put("petersburg", petersburg);
    registry.put("istanbul", istanbul);
    registry.put("berlin", berlin);
    registry.put("london", london);
    registry.put("paris", paris);
  }
}
