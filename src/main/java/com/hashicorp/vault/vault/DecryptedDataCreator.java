package com.hashicorp.vault.vault;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;

public class DecryptedDataCreator implements InstanceCreator<Decrypted> {
  @Override
  public Decrypted createInstance(Type type) {
    return new Decrypted();
  }
}
