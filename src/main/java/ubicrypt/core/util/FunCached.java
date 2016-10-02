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
package ubicrypt.core.util;

import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.functions.Func1;

import static org.slf4j.LoggerFactory.getLogger;

class FunCached<T, R> implements Func1<T, Observable<R>> {
    private static final Logger log = getLogger(FunCached.class);
    private final long delayMs;
    private final R defaultValue;
    private final Func1<T, Observable<R>> actual;
    private final AtomicLong lastExecution = new AtomicLong(System.currentTimeMillis());
    private final AtomicBoolean cachedOnce = new AtomicBoolean(false);

    private FunCached(final long delayMs, final R defaultValue, final Func1<T, Observable<R>> actual) {
        this.delayMs = delayMs;
        this.defaultValue = defaultValue;
        this.actual = actual;
    }

    @Override
    public Observable<R> call(final T t) {
        if (lastExecution.get() < System.currentTimeMillis() - delayMs) {
            cachedOnce.set(true);
            return actual.call(t);
        }
        cachedOnce.set(false);
        return Observable.just(defaultValue);
    }

    public Func1<T, Observable<R>> getActualOrDefault() {
        if (cachedOnce.get()) {
            return actual;
        }
        return r -> Observable.just(defaultValue);
    }

    public R getDefaultValue() {
        return defaultValue;
    }
}
