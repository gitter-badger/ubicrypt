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

import com.google.common.base.Throwables;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.inject.Inject;

import rx.Observable;
import rx.subjects.Subject;
import ubicrypt.core.FileProvenience;
import ubicrypt.core.Utils;
import ubicrypt.core.dto.LocalConfig;
import ubicrypt.core.dto.LocalFile;
import ubicrypt.core.dto.UbiFile;
import ubicrypt.core.dto.VClock;
import ubicrypt.core.exp.NotFoundException;
import ubicrypt.core.util.CopyFile;
import ubicrypt.core.util.StoreTempFile;
import ubicrypt.core.util.SupplierExp;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocalRepository implements IRepository {

    private static final Logger log = LoggerFactory.getLogger(LocalRepository.class);
    @Inject
    final LocalConfig localConfig = new LocalConfig();
    private final Path basePath;
    @Resource
    @Qualifier("fileEvents")
    private Subject<FileEvent, FileEvent> fileEvents;
    @Resource
    @Qualifier("conflictEvents")
    private Subject<UbiFile, UbiFile> conflictEvents;


    public LocalRepository(final Path basePath) {
        this.basePath = basePath;
    }

    private static void check(final FileProvenience fp) {
        checkNotNull(fp, "FileProvenience must not be null");
        checkNotNull(fp.getFile(), "file must not be null");
        checkNotNull(fp.getOrigin(), "file must not be null");
    }

    @Override
    public Observable<Boolean> save(final FileProvenience fp) {
        try {
            check(fp);
            final UbiFile rfile = fp.getFile();
            final Optional<LocalFile> lfile = localConfig.getLocalFiles().stream().filter(lf -> lf.equals(rfile)).findFirst();
            //if path not set, take the getName()
            final Path path = rfile.getPath() != null ? rfile.getPath() : Paths.get(rfile.getName());
            if (!lfile.isPresent()) {
                if (!rfile.isDeleted() && !rfile.isRemoved()) {
                    log.info("copy to:{} from repo:{}, file:{} ", basePath.resolve(path), fp.getOrigin(), rfile.getPath());
                    final LocalFile lc = new LocalFile();
                    lc.copyFrom(rfile);
                    localConfig.getLocalFiles().add(lc);
                    if (Files.exists(basePath.resolve(path))) {
                        final BasicFileAttributes attrs = SupplierExp.silent(() -> Files.readAttributes(basePath.resolve(path), BasicFileAttributes.class));
                        if (attrs.isDirectory()) {
                            log.info("can't import file, a folder already exists with the same name:{}", path);
                            return Observable.just(false);
                        }
                        if (attrs.size() == rfile.getSize()) {
                            log.info("identical file already present locally:{}", path);
                            localConfig.getLocalFiles().add(LocalFile.copy(rfile));
                            return Observable.just(true).doOnCompleted(() -> fileEvents.onNext(new FileEvent(rfile, FileEvent.Type.created, FileEvent.Location.local)));
                        }
                        log.info("conflicting file already present locally:{}", path);
                        conflictEvents.onNext(rfile);
                        return Observable.just(false);
                    }
                    AtomicReference<Path> tempFile = new AtomicReference<>();
                    return fp.getOrigin().get(rfile)
                            .flatMap(new StoreTempFile())
                            .doOnNext(tempFile::set)
                            .map(new CopyFile(rfile.getSize(), basePath.resolve(path), true, fp.getFile().getLastModified()))
                            .doOnCompleted(() -> {
                                if (tempFile.get() != null) {
                                    try {
                                        Files.delete(tempFile.get());
                                    } catch (IOException e) {
                                    }
                                }
                            })
                            .doOnCompleted(() -> localConfig.getLocalFiles().add(LocalFile.copy(rfile)))
                            .doOnCompleted(() -> fileEvents.onNext(new FileEvent(rfile, FileEvent.Type.created, FileEvent.Location.local)));
                }
                //if present
            } else {
                if (rfile.getVclock().compare(lfile.get().getVclock()) == VClock.Comparison.newer) {
                    lfile.get().copyFrom(rfile);
                    if (!rfile.isDeleted() && !rfile.isRemoved()) {
                        log.info("update file:{} locally from repo:{}", rfile.getPath(), fp.getOrigin());
                        AtomicReference<Path> tempFile = new AtomicReference<>();
                        return fp.getOrigin().get(fp.getFile())
                                .flatMap(new StoreTempFile())
                                .map(new CopyFile(rfile.getSize(), basePath.resolve(path), false, fp.getFile().getLastModified()))
                                .doOnCompleted(() -> {
                                    if (tempFile.get() != null) {
                                        try {
                                            Files.delete(tempFile.get());
                                        } catch (IOException e) {
                                        }
                                    }
                                })
                                .doOnCompleted(() -> fileEvents.onNext(new FileEvent(fp.getFile(), FileEvent.Type.updated, FileEvent.Location.local)));
                    }
                    //removed or deleted
                    fileEvents.onNext(new FileEvent(fp.getFile(), rfile.isDeleted() ? FileEvent.Type.deleted : FileEvent.Type.removed, FileEvent.Location.local));

                }
            }
            return Observable.just(false);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    private Stream<LocalFile> getPathStream(final UbiFile file) {
        return localConfig.getLocalFiles().stream().filter(file1 -> file1.equals(file));
    }

    @Override
    public Observable<InputStream> get(final UbiFile file) {
        checkNotNull(file, "file must be not null");
        return Observable.create(subscriber -> {
            try {
                subscriber.onNext(getPathStream(file)
                        .map(LocalFile::getPath)
                        .map(basePath::resolve)
                        .map(Utils::readIs).findFirst().orElseThrow(() -> new NotFoundException(basePath.resolve(file.getPath()))));
                subscriber.onCompleted();
            } catch (final Exception e) {
                subscriber.onError(e);
            }
        });

    }

    @Override
    public boolean isLocal() {
        return true;
    }


    public BasicFileAttributes attributes(final Path path) {
        try {
            return Files.readAttributes(basePath.resolve(path), BasicFileAttributes.class);
        } catch (final IOException e) {
            Throwables.propagate(e);
        }
        return null;
    }

    public Path getBasePath() {
        return basePath;
    }

    public void setFileEvents(final Subject<FileEvent, FileEvent> fileEvents) {
        this.fileEvents = fileEvents;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                .append("basePath", basePath)
                .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final LocalRepository that = (LocalRepository) o;

        return new EqualsBuilder()
                .append(basePath, that.basePath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(basePath)
                .toHashCode();
    }
}
