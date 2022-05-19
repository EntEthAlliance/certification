package org.eea.certification.evm;

import com.google.common.collect.ImmutableSetMultimap;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.units.bigints.UInt256;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Hash;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.Code;
import org.hyperledger.besu.evm.EVM;
import org.hyperledger.besu.evm.Gas;
import org.hyperledger.besu.evm.MainnetEVMs;
import org.hyperledger.besu.evm.account.Account;
import org.hyperledger.besu.evm.fluent.SimpleWorld;
import org.hyperledger.besu.evm.frame.ExceptionalHaltReason;
import org.hyperledger.besu.evm.frame.MessageFrame;
import org.hyperledger.besu.evm.operation.Operation;
import org.hyperledger.besu.evm.operation.OperationRegistry;
import org.hyperledger.besu.evm.precompile.PrecompileContractRegistry;
import org.hyperledger.besu.evm.processor.MessageCallProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Generates tests for each EVM opcode execution across every supported hard fork.
 */
public class EVMOpcodeTestGenerator {

  private static final Logger logger = LoggerFactory.getLogger(EVMOpcodeTestGenerator.class);

  private final SecureRandom random = new SecureRandom();

  /**
   * Generate a valid argument for an operation.
   *
   * @return the argument
   */
  private Bytes generateArgument() {
    int size = random.nextInt(32) + 1;
    return Bytes.random(size);
  }

  private Bytes generateInputData() {
    int size = random.nextInt(64);
    return Bytes.random(size);
  }

  private Wei generateWei() {
    if (random.nextBoolean()) {
      long value = random.nextInt(32000);
      return Wei.of(value);
    } else {
      return Wei.ZERO;
    }
  }

  private Address randomAddress() {
    return Address.wrap(Bytes.random(20));
  }

  private Gas initialGas() {
    return Gas.of(100000000 + random.nextInt(20000) * 100000000L);
  }

  public List<OpcodeTestModel> generateForAllHardForks(int numTestPerOpcode) {
    List<OpcodeTestModel> result = new ArrayList<>();
    result.addAll(generateForFrontier(numTestPerOpcode));
    result.addAll(generateForHomestead(numTestPerOpcode));
    result.addAll(generateSpuriousDragon(numTestPerOpcode));
    result.addAll(generateTangerineWhistle(numTestPerOpcode));
    result.addAll(generateByzantium(numTestPerOpcode));
    result.addAll(generateConstantinople(numTestPerOpcode));
    result.addAll(generatePetersburg(numTestPerOpcode));
    result.addAll(generateIstanbul(numTestPerOpcode));
    result.addAll(generateBerlin(numTestPerOpcode));
    result.addAll(generateLondon(numTestPerOpcode));
    result.addAll(generatePreMergeFork(numTestPerOpcode));
    return result;
  }

  public List<OpcodeTestModel> generateForFrontier(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.frontier, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateForHomestead(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.homestead, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateSpuriousDragon(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.spuriousDragon, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateTangerineWhistle(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.tangerineWhistle, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateByzantium(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.byzantium, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateConstantinople(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.constantinople, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generatePetersburg(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.petersburg, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateIstanbul(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.istanbul, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateBerlin(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.berlin, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateLondon(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.london, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generatePreMergeFork(int numTestsPerOpcode) {
    return generateForHardFork(EVMExecutors.premergeFork, numTestsPerOpcode);
  }

  public List<OpcodeTestModel> generateForHardFork(Supplier<EVMExecutor> evmExecutor, int numTestsPerOpcode) {
    logger.info("Generating for hard fork {}", evmExecutor.get().getHardFork());
    OperationRegistry registry = evmExecutor.get().getOperationsRegistry();

    List<OpcodeTestModel> allTests = new ArrayList<>();
    for (int i = 0; i < 256; i++) {
      Operation operation = registry.get(i);
      if (operation != null) {
        if ("CALL".equals(operation.getName()) || "CALLCODE".equals(operation.getName()) || "DELEGATECALL".equals(operation.getName()) || "STATICCALL".equals(operation.getName())) {
          // for now skip call operations
          continue;
        }
        List<OpcodeTestModel> opcodeTests = new ArrayList<>();
        while (opcodeTests.size() < numTestsPerOpcode) {
          OpcodeTestModel test = generate(evmExecutor, operation);
          if (test != null) {
            test.setIndex(opcodeTests.size());
            opcodeTests.add(test);
          }
        }
        logger.info("Added opcode tests for {}", operation.getName());
        allTests.addAll(opcodeTests);
      }
    }
    logger.info("Done generating for hard fork {}", evmExecutor.get().getHardFork());
    return allTests;
  }

  OpcodeTestModel generate(Supplier<EVMExecutor> evmExecutor, Operation operation) {
    EVMExecutor executor = evmExecutor.get();
    EVM evm = executor.evm;
    PrecompileContractRegistry precompileContractRegistry = executor.getPrecompileContractRegistry();
    Bytes codeBytes = Bytes.EMPTY;
    for (int i = 0; i < operation.getStackItemsConsumed(); i++) {
      Bytes argument = generateArgument();
      byte pushOp = (byte) (0x5f + argument.size());
      codeBytes = Bytes.wrap(codeBytes, Bytes.of(pushOp), argument);
    }
    codeBytes = Bytes.wrap(codeBytes, Bytes.of((byte) operation.getOpcode()));

    Code code = evm.getCode(Hash.hash(codeBytes), codeBytes);
    MessageCallProcessor mcp = new MessageCallProcessor(evm, precompileContractRegistry);

    Address sender = randomAddress();
    Address receiver = randomAddress();
    Address coinbase = randomAddress();
    Deque<MessageFrame> messageFrameStack = new ArrayDeque<>();
    SimpleWorld worldUpdater = new SimpleWorld();

    Account senderAccount = worldUpdater.createAccount(sender, random.nextInt(42) + 1, generateWei());
    Account receiverAccount = worldUpdater.createAccount(receiver, random.nextInt(24), generateWei());
    Account coinbaseAccount = worldUpdater.createAccount(coinbase, random.nextInt(100), generateWei());
    worldUpdater.createAccount(sender, senderAccount.getNonce(), senderAccount.getBalance());
    worldUpdater.createAccount(receiver, receiverAccount.getNonce(), receiverAccount.getBalance());
    worldUpdater.createAccount(coinbase, coinbaseAccount.getNonce(), coinbaseAccount.getBalance());

    SettableBlockValues blockValues = new SettableBlockValues(UInt256.fromBytes(Bytes32.random(random)), Bytes32.random(), initialGas().toLong() * 5, Math.abs(random.nextLong()), Math.abs(random.nextLong()), Optional.empty());

    Wei value = generateWei();
    Gas gasAvailable = initialGas();
    MessageFrame initialMessageFrame = MessageFrame.builder().type(MessageFrame.Type.MESSAGE_CALL).messageFrameStack(messageFrameStack).worldUpdater(worldUpdater.updater()).initialGas(gasAvailable).contract(Address.ZERO).address(receiver).originator(sender).sender(sender).gasPrice(generateWei()).inputData(generateInputData()).value(value).apparentValue(value).code(code).blockValues(blockValues).depth(0).completer(c -> {
    }).miningBeneficiary(coinbase).blockHashLookup(h -> null).accessListWarmAddresses(Collections.emptySet()).accessListWarmStorage(ImmutableSetMultimap.of()).build();
    AtomicReference<Optional<Gas>> gasCost = new AtomicReference<>();
    AtomicReference<ExceptionalHaltReason> haltReason = new AtomicReference<>();
    AtomicReference<Operation> currentOperation = new AtomicReference<>();

    messageFrameStack.add(initialMessageFrame);
    List<Bytes> stackBefore = new ArrayList<>();
    List<Bytes> stackAfter = new ArrayList<>();
    List<Bytes32> memoryBefore = new ArrayList<>();
    List<Bytes32> memoryAfter = new ArrayList<>();
    AtomicBoolean executedOpcode = new AtomicBoolean(false);
    try {
      mcp.process(initialMessageFrame, (frame, executeOperation) -> {
        if (!executedOpcode.get()) {
          stackBefore.clear();
          for (int i = 0; i < frame.stackSize(); i++) {
            stackBefore.add(frame.getStackItem(i));
          }
          memoryBefore.clear();
          for (int i = 0; i < frame.memoryWordSize(); i++) {
            memoryBefore.add((Bytes32) frame.readMemory(i * 32L, 32L));
          }
        }

        currentOperation.set(frame.getCurrentOperation());
        Operation.OperationResult result = executeOperation.execute();
        if (executedOpcode.compareAndSet(false, frame.getCurrentOperation().getOpcode() == operation.getOpcode())) {
          gasCost.set(result.getGasCost());
          stackAfter.clear();
          for (int i = 0; i < frame.stackSize(); i++) {
            stackAfter.add(frame.getStackItem(i));
          }
          for (int i = 0; i < frame.memoryWordSize(); i++) {
            memoryAfter.add((Bytes32) frame.readMemory(i * 32L, 32L));
          }
        }
        haltReason.set(result.getHaltReason().orElse(ExceptionalHaltReason.NONE));

      });
    } catch (IllegalStateException e) {
      // do nothing.
    }
    // the memory is too large, not a suitable outcome. return null
    if (memoryAfter.size() > 128) {
      return null;
    }
    // try to only get opcode execution that doesn't result in an out of gas error
    if (haltReason.get() == ExceptionalHaltReason.INSUFFICIENT_GAS) {
      return null;
    }
    Operation current = currentOperation.get();
    if (current != null && (current.getOpcode() == 0x00 || current.getOpcode() == operation.getOpcode())) {
      List<Account> pre = new ArrayList<>();
      pre.add(senderAccount);
      pre.add(coinbaseAccount);
      pre.add(receiverAccount);
      Gas allGasCost = gasAvailable.minus(initialMessageFrame.getRemainingGas());

      return new OpcodeTestModel(executor.getHardFork(), pre, operation.getName(), stackAfter, memoryAfter, stackBefore, memoryBefore, initialMessageFrame.getInputData(), initialMessageFrame.getGasPrice(), initialMessageFrame.getLogs(), gasAvailable, gasCost.get(), allGasCost, initialMessageFrame.getRefunds(), haltReason.get(), worldUpdater.getTouchedAccounts(), blockValues, sender, receiver, initialMessageFrame.getValue(), codeBytes, coinbase, UInt256.valueOf(MainnetEVMs.DEV_NET_CHAIN_ID));
    }

    // the execution didn't go as far as running the opcode.
    return null;
  }

  /**
   * Runs a given test model, with a hard fork of our choosing
   *
   * @param model    the model to run
   * @param hardFork the hard fork to associate with the execution
   * @return a new model execution with the hard fork.
   */
  public static OpcodeTestModel run(OpcodeTestModel model, String hardFork) {
    EVMExecutor executor = EVMExecutors.registry.get(hardFork).get();
    EVM evm = executor.evm;
    PrecompileContractRegistry precompileContractRegistry = executor.getPrecompileContractRegistry();

    Code code = evm.getCode(Hash.hash(model.getCode()), model.getCode());
    MessageCallProcessor mcp = new MessageCallProcessor(evm, precompileContractRegistry);

    Address sender = model.getSender();
    Address receiver = model.getReceiver();
    Address coinbase = model.getCoinbase();
    Deque<MessageFrame> messageFrameStack = new ArrayDeque<>();
    SimpleWorld worldUpdater = new SimpleWorld();

    List<Account> pre = new ArrayList<>();
    for (Account acct : model.getBefore().getAccounts()) {
      pre.add(acct);
      worldUpdater.createAccount(acct.getAddress(), acct.getNonce(), acct.getBalance());
    }

    SettableBlockValues blockValues = new SettableBlockValues(model.getDifficultyBytes(), model.getMixHashOrPrevRandao(), model.getGasLimit(), model.getNumber(), model.getTimestamp(), Optional.empty());

    Wei value = model.getValue();
    Gas gasAvailable = model.getGasAvailable();
    MessageFrame initialMessageFrame = MessageFrame.builder().type(MessageFrame.Type.MESSAGE_CALL).messageFrameStack(messageFrameStack).worldUpdater(worldUpdater.updater()).initialGas(gasAvailable).contract(Address.ZERO).address(receiver).originator(sender).sender(sender).gasPrice(model.getGasPrice()).inputData(model.getInputData()).value(value).apparentValue(value).code(code).blockValues(blockValues).depth(0).completer(c -> {
    }).miningBeneficiary(coinbase).blockHashLookup(h -> null).accessListWarmAddresses(Collections.emptySet()).accessListWarmStorage(ImmutableSetMultimap.of()).build();
    AtomicReference<Optional<Gas>> gasCost = new AtomicReference<>();

    messageFrameStack.add(initialMessageFrame);
    List<Bytes> stackAfter = new ArrayList<>();
    List<Bytes32> memoryAfter = new ArrayList<>();
    try {
      mcp.process(initialMessageFrame, (frame, executeOperation) -> {
        Operation.OperationResult result = executeOperation.execute();
        gasCost.set(result.getGasCost());
        stackAfter.clear();
        for (int i = 0; i < frame.stackSize(); i++) {
          stackAfter.add(frame.getStackItem(i));
        }
        for (int i = 0; i < frame.memoryWordSize(); i++) {
          memoryAfter.add((Bytes32) frame.readMemory(i * 32L, 32L));
        }
      });
    } catch (IllegalStateException e) {
      // do nothing.
    }
    ExceptionalHaltReason haltReason = initialMessageFrame.getExceptionalHaltReason().orElse(ExceptionalHaltReason.NONE);

    // the memory is too large, not a suitable outcome. return null
    if (memoryAfter.size() > 128) {
      return null;
    }

    Gas allGasCost = gasAvailable.minus(initialMessageFrame.getRemainingGas());

    OpcodeTestModel result = new OpcodeTestModel(executor.getHardFork(), pre, model.getName(), stackAfter, memoryAfter, new ArrayList<>(), new ArrayList<>(), initialMessageFrame.getInputData(), initialMessageFrame.getGasPrice(), initialMessageFrame.getLogs(), gasAvailable, Optional.empty(), allGasCost, initialMessageFrame.getRefunds(), haltReason, worldUpdater.getTouchedAccounts(), blockValues, sender, receiver, value, model.getCode(), coinbase, UInt256.valueOf(MainnetEVMs.DEV_NET_CHAIN_ID));
    result.setIndex(model.getIndex());
    return result;
  }

}
