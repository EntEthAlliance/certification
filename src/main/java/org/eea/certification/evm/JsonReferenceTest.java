package org.eea.certification.evm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.units.bigints.UInt256;
import org.hyperledger.besu.datatypes.Address;
import org.hyperledger.besu.datatypes.Wei;
import org.hyperledger.besu.evm.Gas;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonReferenceTest {

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Env {
    private Address currentCoinbase;
    private UInt256 currentDifficulty;
    private UInt256 currentGasLimit;
    private UInt256 currentNumber;
    private UInt256 currentTimestamp;

    public Address getCurrentCoinbase() {
      return currentCoinbase;
    }

    public void setCurrentCoinbase(Address currentCoinbase) {
      this.currentCoinbase = currentCoinbase;
    }

    public UInt256 getCurrentDifficulty() {
      return currentDifficulty;
    }

    public void setCurrentDifficulty(UInt256 currentDifficulty) {
      this.currentDifficulty = currentDifficulty;
    }

    public UInt256 getCurrentGasLimit() {
      return currentGasLimit;
    }

    public void setCurrentGasLimit(UInt256 currentGasLimit) {
      this.currentGasLimit = currentGasLimit;
    }

    public UInt256 getCurrentNumber() {
      return currentNumber;
    }

    public void setCurrentNumber(UInt256 currentNumber) {
      this.currentNumber = currentNumber;
    }

    public UInt256 getCurrentTimestamp() {
      return currentTimestamp;
    }

    public void setCurrentTimestamp(UInt256 currentTimestamp) {
      this.currentTimestamp = currentTimestamp;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Exec {
    private Address address;
    private Address caller;
    private Bytes code;
    private Bytes data;
    private Wei gasPrice;
    private Address origin;
    private Bytes value;
    private Gas gas;

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }

    public Address getCaller() {
      return caller;
    }

    public void setCaller(Address caller) {
      this.caller = caller;
    }

    public Bytes getCode() {
      return code;
    }

    public void setCode(Bytes code) {
      this.code = code;
    }

    public Bytes getData() {
      return data;
    }

    public void setData(Bytes data) {
      this.data = data;
    }

    public Wei getGasPrice() {
      return gasPrice;
    }

    public void setGasPrice(Wei gasPrice) {
      this.gasPrice = gasPrice;
    }

    public Address getOrigin() {
      return origin;
    }

    public void setOrigin(Address origin) {
      this.origin = origin;
    }

    public Bytes getValue() {
      return value;
    }

    public void setValue(Bytes value) {
      this.value = value;
    }

    public Gas getGas() {
      return gas;
    }

    public void setGas(String gas) {
      this.gas = Gas.fromHexString(gas);
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class JsonAccountState {
    private Wei balance;
    private Bytes code;
    private UInt256 nonce;
    private Map<UInt256, UInt256> storage;

    public Wei getBalance() {
      return balance;
    }

    public void setBalance(Wei balance) {
      this.balance = balance;
    }

    public Bytes getCode() {
      return code;
    }

    public void setCode(Bytes code) {
      this.code = code;
    }

    public UInt256 getNonce() {
      return nonce;
    }

    public void setNonce(UInt256 nonce) {
      this.nonce = nonce;
    }

    public Map<UInt256, UInt256> getStorage() {
      return storage;
    }

    public void setStorage(Map<UInt256, UInt256> storage) {
      this.storage = storage;
    }
  }

  private Env env;
  private Exec exec;
  private Gas gas;
  private Bytes logs;
  private Bytes out;
  private Map<Address, JsonAccountState> post;
  private Map<Address, JsonAccountState> pre;

  public Env getEnv() {
    return env;
  }

  public void setEnv(Env env) {
    this.env = env;
  }

  public Exec getExec() {
    return exec;
  }

  public void setExec(Exec exec) {
    this.exec = exec;
  }

  public Gas getGas() {
    return gas;
  }

  public void setGas(String gas) {
    this.gas = Gas.fromHexString(gas);
  }

  public Bytes getLogs() {
    return logs;
  }

  public void setLogs(Bytes logs) {
    this.logs = logs;
  }

  public Bytes getOut() {
    return out;
  }

  public void setOut(Bytes out) {
    this.out = out;
  }

  public Map<Address, JsonAccountState> getPost() {
    return post;
  }

  public void setPost(Map<Address, JsonAccountState> post) {
    this.post = post;
  }

  public Map<Address, JsonAccountState> getPre() {
    return pre;
  }

  public void setPre(Map<Address, JsonAccountState> pre) {
    this.pre = pre;
  }
}
