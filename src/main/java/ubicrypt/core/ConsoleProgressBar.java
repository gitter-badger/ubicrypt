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

class ConsoleProgressBar {
    public static void main(final String[] args) throws InterruptedException, IOException {
        final char[] animationChars = new char[]{'|', '/', '-', '\\'};

        final String anim = "|/-\\";
        for (int x = 0; x < 100; x++) {
            final String data = "file:\r" + anim.charAt(x % anim.length()) + " " + x;
            System.out.write(data.getBytes());
            Thread.sleep(100);
        }

        System.out.println("Processing: Done!          ");
    }
}
