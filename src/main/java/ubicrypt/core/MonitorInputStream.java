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
package ubicrypt.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.internal.operators.BufferUntilSubscriber;

public class MonitorInputStream extends InputStream {
    private final InputStream inputStream;
    private final AtomicLong counter = new AtomicLong(0);
    private final BufferUntilSubscriber<Long> subscriber = BufferUntilSubscriber.create();
    private int chunkLength = 1 << 16;

    public MonitorInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public MonitorInputStream(final InputStream inputStream, final int chunkLength) {
        this.inputStream = inputStream;
        this.chunkLength = chunkLength;
    }

    @Override
    public int read() throws IOException {
        try {
            final int ret = inputStream.read();
            if (ret == -1) {
                subscriber.onNext(counter.get());
                subscriber.onCompleted();
            } else {
                if ((counter.incrementAndGet() % chunkLength) == 0) {
                    subscriber.onNext(counter.get());
                }
            }
            return ret;
        } catch (final IOException e) {
            subscriber.onError(e);
            throw e;
        }
    }

    public Observable<Long> monitor() {
        return subscriber;
    }

}
