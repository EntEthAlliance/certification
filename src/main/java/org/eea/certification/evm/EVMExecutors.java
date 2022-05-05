package org.eea.certification.evm;

import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.MainnetEVMs;
import org.hyperledger.besu.evm.internal.EvmConfiguration;
import org.hyperledger.besu.evm.precompile.MainnetPrecompiledContracts;
import org.hyperledger.besu.evm.precompile.PrecompileContractRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EVMExecutors {

  public static final Supplier<EVMExecutor> frontier = () -> {
    EVM evm = MainnetEVMs.frontier(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.frontier(evm.getGasCalculator());
    return new EVMExecutor("frontier", evm, precompileContractRegistry, MainnetEVMs.frontierOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> homestead = () -> {
    EVM evm = MainnetEVMs.homestead(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.homestead(evm.getGasCalculator());
    return new EVMExecutor("homestead", evm, precompileContractRegistry, MainnetEVMs.homesteadOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> spuriousDragon = () -> {
    EVM evm = MainnetEVMs.spuriousDragon(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.homestead(evm.getGasCalculator());
    return new EVMExecutor("spuriousDragon", evm, precompileContractRegistry, MainnetEVMs.homesteadOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> tangerineWhistle = () -> {
    EVM evm = MainnetEVMs.tangerineWhistle(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.homestead(evm.getGasCalculator());
    return new EVMExecutor("tangerineWhistle", evm, precompileContractRegistry, MainnetEVMs.homesteadOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> byzantium = () -> {
    EVM evm = MainnetEVMs.byzantium(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.byzantium(evm.getGasCalculator());
    return new EVMExecutor("byzantium", evm, precompileContractRegistry, MainnetEVMs.byzantiumOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> constantinople = () -> {
    EVM evm = MainnetEVMs.constantinople(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.byzantium(evm.getGasCalculator());
    return new EVMExecutor("constantinople", evm, precompileContractRegistry, MainnetEVMs.constantinopleOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> petersburg = () -> {
    EVM evm = MainnetEVMs.petersburg(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.byzantium(evm.getGasCalculator());
    return new EVMExecutor("petersburg", evm, precompileContractRegistry, MainnetEVMs.constantinopleOperations(evm.getGasCalculator()));
  };

  public static final Supplier<EVMExecutor> istanbul = () -> {
    EVM evm = MainnetEVMs.istanbul(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutor("istanbul", evm, precompileContractRegistry, MainnetEVMs.istanbulOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Supplier<EVMExecutor> berlin = () -> {
    EVM evm = MainnetEVMs.berlin(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutor("berlin", evm, precompileContractRegistry, MainnetEVMs.istanbulOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Supplier<EVMExecutor> london = () -> {
    EVM evm = MainnetEVMs.london(EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutor("london", evm, precompileContractRegistry, MainnetEVMs.londonOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Supplier<EVMExecutor> premergeFork = () -> {
    EVM evm = MainnetEVMs.preMergeFork(MainnetEVMs.DEV_NET_CHAIN_ID, EvmConfiguration.DEFAULT);
    PrecompileContractRegistry precompileContractRegistry = MainnetPrecompiledContracts.istanbul(evm.getGasCalculator());
    return new EVMExecutor("premerge", evm, precompileContractRegistry, MainnetEVMs.preMergeForkOperations(evm.getGasCalculator(), MainnetEVMs.DEV_NET_CHAIN_ID));
  };

  public static final Map<String, Supplier<EVMExecutor>> registry = new HashMap<>();

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
    registry.put("premerge", premergeFork);
  }
}
