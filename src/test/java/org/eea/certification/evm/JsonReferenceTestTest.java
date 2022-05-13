package org.eea.certification.evm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.tuweni.eth.Address;
import org.apache.tuweni.eth.EthJsonModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class JsonReferenceTestTest {

  @Test
  void testLoadJson() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JsonModule());
    mapper.registerModule(new EthJsonModule());
    TypeReference<HashMap<String, JsonReferenceTest>> ref = new TypeReference<>() {
    };
    Map<String, JsonReferenceTest> test = mapper.readValue(getClass().getResourceAsStream("/add3.json"), ref);
    assertEquals(Address.fromHexString("0x0f572e5295c57f15886f9b263e2f6d2d6c7b5ec6"), test.get("add3").getExec().getAddress());
  }

  @Test
  void testLoadJsonAndRun() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new JsonModule());
    mapper.registerModule(new EthJsonModule());
    TypeReference<HashMap<String, JsonReferenceTest>> ref = new TypeReference<>() {
    };
    Map<String, JsonReferenceTest> tests = mapper.readValue(getClass().getResourceAsStream("/add3.json"), ref);
    JsonReferenceTest test = tests.get("add3");
    OpcodeTestModel model = OpcodeTestModel.fromJsonReferenceTest("frontier", "add3", test);
    assertEquals(2, model.getBefore().getAccounts().size());
    OpcodeTestModel result = EVMOpcodeTestGenerator.run(model, "frontier");
    System.out.println(mapper.writeValueAsString(result));
  }
}
