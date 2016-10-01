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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;
import ubicrypt.core.FileSynchronizer;
import ubicrypt.core.InitFileSyncronizer;
import ubicrypt.core.InitLocalFiles;
import ubicrypt.core.ProgressFile;
import ubicrypt.core.dto.UbiFile;
import ubicrypt.core.util.QueueLiner;

@Configuration
public class RemoteCtxConf {

    @Bean
    @DependsOn("localFileInitSynchronizer")
    public ProviderLifeCycle providerLifeCycle() {
        return new ProviderLifeCycle();
    }

    @Bean
    public InitFileSyncronizer initFileSyncronizer() {
        return new InitFileSyncronizer();
    }

    @Bean
    public Observable.OnSubscribe<Boolean> fileSynchronizer() {
        return new FileSynchronizer();
    }

    @Bean
    public InitLocalFiles localFileInitSynchronizer() {
        return new InitLocalFiles();
    }

    @Bean
    public Subject<ProviderEvent, ProviderEvent> providerEvent() {
        return ReplaySubject.create();
    }

    @Bean
    public PublishSubject<ProgressFile> progressEvents() {
        return PublishSubject.create();
    }

    @Bean
    public Subject<FileEvent, FileEvent> fileEvents() {
        return PublishSubject.create();
    }

    @Bean
    public Subject<UbiFile, UbiFile> conflictEvents() {
        return PublishSubject.create();
    }

    @Bean
    public PublishSubject<ProviderHook> providerStatus() {
        return PublishSubject.create();
    }

    @Bean
    public QueueLiner<Boolean> queueLiner(@Value("${saveConfIntervalMs:30000}") final Long saveConfIntervalMs) {
        return new QueueLiner<>(saveConfIntervalMs);
    }


}
