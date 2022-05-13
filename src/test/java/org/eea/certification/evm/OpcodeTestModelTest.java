package org.eea.certification.evm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.tuweni.eth.EthJsonModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpcodeTestModelTest {

  @Test
  void testYAMLRoundtrip() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new JsonModule());
    mapper.registerModule(new EthJsonModule());
    OpcodeTestModel model = mapper.readValue(getClass().getResourceAsStream("/SWAP15-4.yaml"), OpcodeTestModel.class);
    assertEquals("SWAP15", model.getName());
  }
}
