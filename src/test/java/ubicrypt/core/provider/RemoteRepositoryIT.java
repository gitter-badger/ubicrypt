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
package ubicrypt.core.provider;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import rx.Observable;
import rx.internal.operators.BufferUntilSubscriber;
import rx.subjects.PublishSubject;
import ubicrypt.core.FileProvenience;
import ubicrypt.core.ProgressFile;
import ubicrypt.core.TestUtils;
import ubicrypt.core.Utils;
import ubicrypt.core.crypto.PGPEC;
import ubicrypt.core.crypto.PGPService;
import ubicrypt.core.dto.*;
import ubicrypt.core.exp.NotFoundException;
import ubicrypt.core.provider.file.FileProvider;
import ubicrypt.core.provider.lock.AcquirerReleaser;
import ubicrypt.core.provider.lock.ObjectIO;
import ubicrypt.core.util.ObjectSerializer;
import ubicrypt.core.util.QueueLiner;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;
import static rx.functions.Actions.empty;

public class RemoteRepositoryIT {

    private static final Logger log = getLogger(RemoteRepositoryIT.class);
    private static final int deviceId = Utils.deviceId();

    @Before
    public void setUp() throws Exception {
        TestUtils.deleteDirs();
        TestUtils.createDirs();
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.deleteR(TestUtils.tmp);
        TestUtils.deleteR(TestUtils.tmp2);

    }

    @Test
    public void save() throws Exception {
        final PGPService pgp = new PGPService(PGPEC.encryptionKey(), new LocalConfig());

        final RemoteConfig remoteConfig = new RemoteConfig();
        Observable.OnSubscribe<AcquirerReleaser> acquirer = subscriber -> {
            subscriber.onNext(new AcquirerReleaser(remoteConfig, empty()));
            subscriber.onCompleted();
        };
        final FileProvider provider = TestUtils.fileProvider(TestUtils.tmp);
        final ObjectSerializer ser = new ObjectSerializer(provider) {{
            setPgpService(pgp);
        }};
        provider.getConfFile().setKey(new Key() {{
            setType(UbiFile.KeyType.pgp);
        }});
        ser.putObject(remoteConfig, provider.getConfFile()).toBlocking().last();

        final LocalRepository localRepository = new LocalRepository(TestUtils.tmp2);
        Utils.write(TestUtils.tmp2.resolve("origin"), "ciao".getBytes()).toBlocking().last();
        final PublishSubject<ProgressFile> progress = PublishSubject.create();
        BufferUntilSubscriber<FileEvent> fileEvents = BufferUntilSubscriber.create();

        final BufferUntilSubscriber<FileEvent> finalFileEvents = fileEvents;
        final RemoteRepository repo = new RemoteRepository(acquirer, provider, new ObjectIO<>(ser, provider.getConfFile(), RemoteConfig.class)) {{
            setProgressEvents(progress);
            setFileEvents(finalFileEvents);
            setQueueLiner(new QueueLiner<>(1000));
        }};
        repo.init();

        final LocalFile localFile = new LocalFile() {{
            setPath(Paths.get("origin"));
        }};
        localRepository.localConfig.getLocalFiles().add(localFile);
        final ArrayList<ProgressFile> progresses = new ArrayList<>();
        progress.subscribe(progresses::add);

        //create
        assertThat(repo.save(new FileProvenience(localFile, localRepository)).toBlocking().last()).isTrue();
        assertThat(remoteConfig.getRemoteFiles()).hasSize(1);
        assertThat(IOUtils.readLines(repo.get(remoteConfig.getRemoteFiles().iterator().next()).toBlocking().first())).contains("ciao");
        assertThat(progresses).hasSize(4);//2 put, 2 get
        Iterator<ProgressFile> it = progresses.iterator();
        ProgressFile next = it.next();
        assertThat(next.isCompleted()).isFalse();
        assertThat(next.getChunk()).isGreaterThan(0);
        assertThat(it.next().isCompleted()).isTrue();
        progresses.clear();
        FileEvent fileEvent = fileEvents.toBlocking().first();
        assertThat(fileEvent.getType()).isEqualTo(FileEvent.Type.created);
        assertThat(fileEvent.getFile()).isEqualTo(localFile);

        //update
        fileEvents = BufferUntilSubscriber.create();
        repo.setFileEvents(fileEvents);
        Utils.write(TestUtils.tmp2.resolve("origin"), "ciao2".getBytes()).toBlocking().last();
        localFile.getVclock().increment(deviceId);
        assertThat(repo.save(new FileProvenience(localFile, localRepository)).toBlocking().last()).isTrue();
        assertThat(localFile.compare(remoteConfig.getRemoteFiles().iterator().next())).isEqualTo(VClock.Comparison.equal);
        assertThat(IOUtils.readLines(repo.get(remoteConfig.getRemoteFiles().iterator().next()).toBlocking().first())).contains("ciao2");
        it = progresses.iterator();
        next = it.next();
        assertThat(next.isCompleted()).isFalse();
        assertThat(next.getChunk()).isGreaterThan(0);
        assertThat(it.next().isCompleted()).isTrue();
        progresses.clear();
        fileEvent = fileEvents.toBlocking().first();
        assertThat(fileEvent.getType()).isEqualTo(FileEvent.Type.updated);
        assertThat(fileEvent.getFile()).isEqualTo(localFile);

        //not update
        fileEvents = BufferUntilSubscriber.create();
        repo.setFileEvents(fileEvents);
        final VClock vClock = (VClock) localFile.getVclock().clone();
        assertThat(repo.save(new FileProvenience(localFile, localRepository)).toBlocking().last()).isFalse();
        assertThat(remoteConfig.getRemoteFiles().iterator().next().getVclock().compare(vClock)).isEqualTo(VClock.Comparison.equal);
        assertThat(progresses).isEmpty();
        final CountDownLatch cd = new CountDownLatch(1);
        fileEvents.subscribe(event -> cd.countDown());
        if (cd.await(1, TimeUnit.SECONDS)) {
            Assertions.fail("update event not expected");
        }

        //delete
        fileEvents = BufferUntilSubscriber.create();
        repo.setFileEvents(fileEvents);
        localFile.setDeleted(true);
        localFile.getVclock().increment(deviceId);
        assertThat(repo.save(new FileProvenience(localFile, localRepository)).toBlocking().last()).isTrue();
        assertThat(remoteConfig.getRemoteFiles().iterator().next().isDeleted()).isTrue();
        try {
            assertThat(IOUtils.readLines(repo.get(remoteConfig.getRemoteFiles().iterator().next()).toBlocking().first())).contains("ciao2");
            Assertions.fail("file still exists");
        } catch (final NotFoundException e) {

        }
        fileEvent = fileEvents.toBlocking().first();
        assertThat(fileEvent.getType()).isEqualTo(FileEvent.Type.deleted);
        assertThat(fileEvent.getFile()).isEqualTo(localFile);


    }
}
