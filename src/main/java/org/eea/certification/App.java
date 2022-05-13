package org.eea.certification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.tuweni.eth.EthJsonModule;
import org.eea.certification.evm.EVMExecutor;
import org.eea.certification.evm.EVMExecutors;
import org.eea.certification.evm.EVMOpcodeTestGenerator;
import org.eea.certification.evm.JsonModule;
import org.eea.certification.evm.OpcodeTestModel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

/**
 * Entry point of the application.
 */
public class App {

  private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  static {
    mapper.registerModule(new JsonModule());
    mapper.registerModule(new EthJsonModule());
  }

  /**
   * @param args A set of command line arguments used to run the application
   *             Arguments are expected to be:
   *             generate, followed by an optional argument of a path to generate tests. If no path is provided,
   *             the working directory is used.
   *             recreate, followed by the path of a yaml file containing a valid test model, and an optional argument
   *             of a path to generate tests. If no path is provided, the working directory is used.
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      String action = args[0];
      if ("generate".equals(action)) {
        Path path = Paths.get(args.length >= 2 ? args[1] : "");
        generate(path);
      } else if ("recreate".equals(action)) {
        Path modelPath = Paths.get(args.length >= 2 ? args[1] : "");
        Path testsPath = Paths.get(args.length >= 3 ? args[2] : "");

        recreate(modelPath, testsPath);
      } else {
        System.err.println("Unrecognized command " + action);
        System.exit(1);
      }
    } else {
      System.err.println("Unrecognized command, use generate <path> or recreate <file> <path>");
      System.exit(1);
    }

  }

  private static void recreate(Path modelPath, Path testsPath) {
    if (!modelPath.toFile().exists()) {
      System.err.println("Cannot find test file: " + modelPath);
      System.exit(1);
    }
    if (!modelPath.toFile().canRead()) {
      System.err.println("Cannot read test file: " + modelPath);
      System.exit(1);
    }
    OpcodeTestModel model = null;
    try {
      model = mapper.readValue(modelPath.toFile(), OpcodeTestModel.class);
    } catch (IOException e) {
      System.err.println("Cannot interpret test file contents: " + modelPath);
      e.printStackTrace();
      System.exit(1);
    }
    testsPath.toFile().mkdirs();
    for (Supplier<EVMExecutor> executor : EVMExecutors.registry.values()) {
      String hardFork = executor.get().getHardFork();
      Path folder = testsPath.resolve(hardFork);
      folder.toFile().mkdirs();
      OpcodeTestModel result = EVMOpcodeTestGenerator.run(model, hardFork);
      Path testFile = folder.resolve(result.getName() + "-" + result.getIndex() + ".yaml");
      try {
        mapper.writeValue(testFile.toFile(), result);
      } catch (IOException e) {
        System.err.println("Cannot write test file contents: " + testFile);
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  private static void generate(Path path) {
    path.toFile().mkdirs();

    EVMOpcodeTestGenerator generator = new EVMOpcodeTestGenerator();
    List<OpcodeTestModel> tests = generator.generateForAllHardForks(5);
    for (OpcodeTestModel test : tests) {
      Path folder = path.resolve(test.getHardFork());
      folder.toFile().mkdirs();
      Path testFile = folder.resolve(test.getName() + "-" + test.getIndex() + ".yaml");
      try {
        mapper.writeValue(testFile.toFile(), test);
      } catch (IOException e) {
        System.err.println("Error writing file " + testFile + ": " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
