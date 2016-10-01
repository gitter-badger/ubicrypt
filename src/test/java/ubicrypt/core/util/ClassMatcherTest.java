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

import org.junit.Test;
import rx.functions.Action1;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassMatcherTest {

    @Test
    public void action1() throws Exception {
        AtomicInteger ai = new AtomicInteger(0);
        ClassMatcher.newMatcher()
                .on(String.class, (Action1<String>) str -> ai.set(2))
                .on(Integer.class, ai::set)
                .call(1);
        assertThat(ai.get()).isEqualTo(1);

    }
}
