package com.hashicorp.vault.vault;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;

public class SignatureDataCreator implements InstanceCreator<Signature> {
  @Override
  public Signature createInstance(Type type) {
    return new Signature();
  }
}
