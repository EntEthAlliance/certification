package org.eea.certification.evm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.tuweni.eth.EthJsonModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EVMOpcodeTestGeneratorTest {

  @Test
  void testGenerate() throws JsonProcessingException {
    EVMOpcodeTestGenerator generator = new EVMOpcodeTestGenerator();
    List<OpcodeTestModel> tests = generator.generateForAllHardForks(1);
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new JsonModule());
    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tests.get(0x30));
  }

  @Test
  void testTryRun() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new JsonModule());
    mapper.registerModule(new EthJsonModule());
    OpcodeTestModel model = mapper.readValue(getClass().getResourceAsStream("/CREATE2-2.yaml"), OpcodeTestModel.class);
    assertNotNull(model.getBefore().getAccounts());
    EVMOpcodeTestGenerator generator = new EVMOpcodeTestGenerator();
    OpcodeTestModel result = generator.run(model, model.getHardFork());
    assertEquals(model.getAfter().getStack(), result.getAfter().getStack());
  }
}
