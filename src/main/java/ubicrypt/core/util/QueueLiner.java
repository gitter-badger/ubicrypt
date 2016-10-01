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
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;
import static rx.functions.Actions.empty;

/**
 * Runs sequentially any Observable and terminate any job with the given conclusive epiloguer.
 *
 * @param <T>
 */
public class QueueLiner<T> {
    private final Logger log = getLogger(QueueLiner.class);
    private final AtomicBoolean inProcess = new AtomicBoolean(false);
    private final long delayMs;
    private final CopyOnWriteArrayList<QueueEpilogued> enqueuers = new CopyOnWriteArrayList<>();

    /**
     * @param delayMs specify the minimun delays between epiloguers execution.
     */
    public QueueLiner(final long delayMs) {
        log.info("epilogue delay:{} ms", delayMs);
        this.delayMs = delayMs;
    }

    /**
     * Creates an Enqueuer for a specific epilogue.
     *
     * @param epilogue
     * @return
     */
    public QueueEpilogued createEpiloguer(final Supplier<Observable<T>> epilogue) {
        final QueueEpilogued qe = new QueueEpilogued(epilogue);
        enqueuers.add(qe);
        return qe;
    }

    /**
     * Fetch all queues and executes them one after another.
     */
    private void fetch() {
        final Optional<QueueEpilogued> opt = enqueuers.stream().filter(enqueuer ->
                !enqueuer.queue.isEmpty()).findFirst();
        if (opt.isPresent()) {
            log.trace("epiloged in queue:{}", opt.get());
            spool(opt.get(), null);
        } else {
            log.trace("no epiloged in queue");
            inProcess.set(false);
        }
    }

    /**
     * Spools the pending queue for a specific QueueEpilogued.
     *
     * @param instance
     * @param lastSubscriber
     */
    private void spool(final QueueEpilogued instance, final Subscriber<? super T> lastSubscriber) {
        final Tuple2<Observable<T>, Subscriber<? super T>> enqued = instance.queue.poll();
        log.trace("{}, spool item exists:{}", instance.hashCode(), enqued != null);
        if (enqued == null) {
            if (instance.skippedEpilogue.get()) {
                log.trace("run final epiloguer:{}", instance.epilogue);
                instance.epilogue.get().doOnCompleted(this::fetch).subscribe(empty(), lastSubscriber::onError, lastSubscriber::onCompleted);
            } else {
                lastSubscriber.onCompleted();
                fetch();
            }
            return;
        } else if (lastSubscriber != null) {
            lastSubscriber.onCompleted();
        }
        enqued.getT1().doOnCompleted(() -> {
            if (instance.lastEpilogue.get() + delayMs < System.currentTimeMillis()) {
                instance.lastEpilogue.set(System.currentTimeMillis());
                log.trace("run epiloguer:{}", instance.epilogue);
                instance.epilogue.get().subscribe(empty(), enqued.getT2()::onError, () -> {
                    instance.skippedEpilogue.set(false);
                    spool(instance, enqued.getT2());
                });
                return;
            }
            log.trace("skip epiloguer");
            instance.skippedEpilogue.set(true);
            spool(instance, enqued.getT2());
        }).subscribe(enqued.getT2()::onNext, enqued.getT2()::onError);
    }

    public class QueueEpilogued implements Func1<Observable<T>, Observable<T>> {
        private final Supplier<Observable<T>> epilogue;
        private final AtomicBoolean skippedEpilogue = new AtomicBoolean(false);
        private final AtomicLong lastEpilogue = new AtomicLong(System.currentTimeMillis());
        private final ConcurrentLinkedQueue<Tuple2<Observable<T>, Subscriber<? super T>>> queue = new ConcurrentLinkedQueue<>();

        private QueueEpilogued(final Supplier<Observable<T>> epilogue) {
            this.epilogue = epilogue;
        }

        /**
         * Enqueue the Observable and runs it sequentially (not in parallel)
         *
         * @param enqueable
         * @return
         */
        @Override
        public Observable<T> call(final Observable<T> enqueable) {
            return Observable.create(subscriber -> {
                log.trace("enqueued epilogue:{}", epilogue.hashCode());
                queue.offer(Tuple.of(enqueable, subscriber));
                if (inProcess.compareAndSet(false, true)) {
                    log.trace("fetch queue");
                    fetch();
                }
            });
        }

    }
}
