// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.google.cloud.crypto.tink;

import static org.junit.Assert.assertEquals;

import com.google.cloud.crypto.tink.TinkProto.Keyset.Key;
import com.google.cloud.crypto.tink.TinkProto.Keyset.KeyStatus;
import java.security.GeneralSecurityException;
import org.junit.Test;

/**
 * Tests for PrimitiveSet.
 */
public class PrimitiveSetTest {
  private class DummyMac1 implements Mac {
    public DummyMac1() {}
    @Override
    public byte[] computeMac(byte[] data) throws GeneralSecurityException {
      return this.getClass().getSimpleName().getBytes();
    }
    @Override
    public boolean verifyMac(byte[] mac, byte[] data) throws GeneralSecurityException {
      return true;
    }
  }

  private class DummyMac2 implements Mac {
    public DummyMac2() {}
    @Override
    public byte[] computeMac(byte[] data) throws GeneralSecurityException {
      return this.getClass().getSimpleName().getBytes();
    }
    @Override
    public boolean verifyMac(byte[] mac, byte[] data) throws GeneralSecurityException {
      return true;
    }
  }

  @Test
  public void testBasicFunctionality() throws Exception {
    PrimitiveSet<Mac> pset = PrimitiveSet.newPrimitiveSet();
    pset.addPrimitive(new DummyMac1(),
        Key.newBuilder().setKeyId(1).setStatus(KeyStatus.ENABLED).build());
    pset.addPrimitive(new DummyMac2(),
        Key.newBuilder().setKeyId(2).setStatus(KeyStatus.ENABLED).build());
    pset.addPrimitive(new DummyMac1(),
        Key.newBuilder().setKeyId(3).setStatus(KeyStatus.DISABLED).build());
    pset.setPrimary(pset.getPrimitiveForId(2));
    PrimitiveSet<Mac>.Entry<Mac> entry = pset.getPrimitiveForId(1);
    assertEquals(DummyMac1.class.getSimpleName(),
        new String(entry.getPrimitive().computeMac(null)));
    assertEquals(KeyStatus.ENABLED, entry.getStatus());
    assertEquals(1, entry.getIdentifier());

    entry = pset.getPrimitiveForId(2);
    assertEquals(DummyMac2.class.getSimpleName(),
        new String(entry.getPrimitive().computeMac(null)));
    assertEquals(KeyStatus.ENABLED, entry.getStatus());
    assertEquals(2, entry.getIdentifier());

    entry = pset.getPrimitiveForId(3);
    assertEquals(DummyMac1.class.getSimpleName(),
        new String(entry.getPrimitive().computeMac(null)));
    assertEquals(KeyStatus.DISABLED, entry.getStatus());
    assertEquals(3, entry.getIdentifier());

    entry = pset.getPrimary();
    assertEquals(DummyMac2.class.getSimpleName(),
        new String(entry.getPrimitive().computeMac(null)));
    assertEquals(KeyStatus.ENABLED, entry.getStatus());
    assertEquals(2, entry.getIdentifier());
  }
}
