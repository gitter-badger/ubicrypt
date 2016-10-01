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

import org.junit.Test;
import ubicrypt.core.dto.RemoteFile;
import ubicrypt.core.provider.ProviderHook;

import static org.mockito.Mockito.mock;

public class FileSynchronizedTest {

    @Test
    public void sameElement() throws Exception {
        final ProviderHook hook = mock(ProviderHook.class);
        final RemoteFile file = new RemoteFile() {{
            getVclock().increment(1);
        }};
/*
        doReturn(Observable.just(new RemoteConfig() {{
            setRemoteFiles(ImmutableSet.of(file));
        }})).when(hook).acquire();
*/

/*
        final Boolean result = Observable.create(new FileSynchronized(file, Collections.singletonList(hook))).toBlocking().first();
        assertThat(result).isTrue();
*/
    }

    @Test
    public void noProviders() throws Exception {
/*
        final Boolean result = Observable.create(new FileSynchronized(new LocalFile(), Collections.emptyList())).toBlocking().first();
        assertThat(result).isFalse();
*/
    }


    @Test
    public void twoProviders() throws Exception {
        final ProviderHook hook = mock(ProviderHook.class);
        final ProviderHook hook2 = mock(ProviderHook.class);
        final RemoteFile file = new RemoteFile() {{
            getVclock().increment(1);
        }};
/*
        doReturn(Observable.just(new RemoteConfig() {{
            setRemoteFiles(ImmutableSet.of(file));
        }})).when(hook).acquire();

        doReturn(Observable.just(new RemoteConfig() {{
            setRemoteFiles(ImmutableSet.of(new RemoteFile() {{
                setId(file.getId());
                getVclock().increment(2);
            }}));
        }})).when(hook2).acquire();

        final Boolean result = Observable.create(new FileSynchronized(file, Arrays.asList(hook, hook2))).toBlocking().first();
        assertThat(result).isFalse();
*/
    }

    @Test
    public void notPresent() throws Exception {
        final ProviderHook hook = mock(ProviderHook.class);
        final RemoteFile file = new RemoteFile() {{
            getVclock().increment(1);
        }};
/*
        doReturn(Observable.just(new RemoteConfig() {{
            setRemoteFiles(ImmutableSet.of(new RemoteFile()));
        }})).when(hook).acquire();

        final Boolean result = Observable.create(new FileSynchronized(file, Collections.singletonList(hook))).toBlocking().first();
        assertThat(result).isFalse();
*/
    }

    @Test
    public void differentVersion() throws Exception {
        final ProviderHook hook = mock(ProviderHook.class);
        final RemoteFile file = new RemoteFile() {{
            getVclock().increment(1);
        }};
/*
        doReturn(Observable.just(new RemoteConfig() {{
            setRemoteFiles(ImmutableSet.of(new RemoteFile() {{
                setId(file.getId());
                getVclock().increment(1);
                getVclock().increment(1);
            }}));
        }})).when(hook).acquire();

        final Boolean result = Observable.create(new FileSynchronized(file, Collections.singletonList(hook))).toBlocking().first();
        assertThat(result).isFalse();
*/
    }
}
