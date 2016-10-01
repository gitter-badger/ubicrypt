/**
 * Copyright (C) 2016 Giancarlo Frison <giancarlo@gfrison.com>
 * <p>
 * Licensed under the UbiCrypt License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://github.com/gfrison/ubicrypt/LICENSE.md
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ubicrypt.core.crypto;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import ubicrypt.core.TestUtils;

import static org.slf4j.LoggerFactory.getLogger;

class PGPServiceTest {
    private static final Logger log = getLogger(PGPServiceTest.class);

    @Before
    public void setUp() throws Exception {
        TestUtils.deleteDirs();
        TestUtils.createDirs();

    }

    @After
    public void tearDown() throws Exception {
        TestUtils.deleteDirs();

    }

/*
    @Test
    public void pkRing() throws Exception {
        PGPKeyPair sign = PGPEC.masterKey();
        PGPKeyPair enc = PGPEC.encryptionKey();

        final char[] passPhrase = "g".toCharArray();
        PGPKeyPair newKeyPair = PGPEC.extractEncryptKeyPair(PGPEC.createSecretKeyRing(passPhrase), passPhrase);
        PGPKeyPair newKeyPair2 = PGPEC.extractEncryptKeyPair(PGPEC.createSecretKeyRing(passPhrase), passPhrase);

        Path path = TestUtils.tmp.resolve(RandomStringUtils.randomAlphabetic(3));

        //create new pk ring
        CountDownLatch cd = new CountDownLatch(1);
        PGPService.addPK(path, enc, newKeyPair.getPublicKey(), sign).doOnCompleted(() -> cd.countDown()).subscribe();
        assertThat(cd.await(2, TimeUnit.SECONDS)).isTrue();

        //load pk ring
        List<PGPKValue> pks = PGPService.loadPKs(path, enc.getPrivateKey(), sign.getPrivateKey());
        assertThat(pks).hasSize(1);
        assertThat(Utils.toStream(pks.get(0).getValue().getKeySignatures())
                .filter(sig -> ((PGPSignature) sig).getKeyID() == sign.getPrivateKey().getKeyID())
                .findFirst()).isPresent();

        //add another pk
        CountDownLatch cd2 = new CountDownLatch(1);
        PGPService.addPK(path, enc, newKeyPair2.getPublicKey(), sign).doOnCompleted(() -> cd2.countDown()).doOnError(err -> err.printStackTrace()).
                subscribe();
        assertThat(cd.await(3, TimeUnit.SECONDS)).isTrue();
        Thread.sleep(2000);

        log.debug("second key, sign key:" + sign.getPrivateKey().getKeyID());
        //check 2 pks
        pks = PGPService.loadPKs(path, enc.getPrivateKey(), sign.getPrivateKey());
        assertThat(pks).hasSize(2);

    }
*/
}
