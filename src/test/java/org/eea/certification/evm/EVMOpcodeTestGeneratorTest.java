package org.eea.certification.evm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.tuweni.eth.EthJsonModule;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

public class EVMOpcodeTestGeneratorTest {

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
