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
package ubicrypt.ui.ctrl;

import java.util.EmptyStackException;
import java.util.Stack;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class GeneralProgress {
    private final ProgressIndicator inProgress;
    private final Label inProgressMessage;
    private final Stack<String> messages = new Stack();

    public GeneralProgress(ProgressIndicator inProgress, Label inProgressMessage) {
        this.inProgress = inProgress;
        this.inProgressMessage = inProgressMessage;
    }

    public void startProgress(String message) {
        messages.push(message);
        Platform.runLater(() -> {
            inProgress.setVisible(true);
            inProgressMessage.setText(message + "...");
        });
    }

    public void stopProgress() {
        Platform.runLater(() -> {
            try {
                inProgressMessage.setText(messages.pop() + "...");
            } catch (EmptyStackException e) {
                inProgressMessage.setText("");
            }
            if (messages.isEmpty()) {
                inProgress.setVisible(false);
                inProgressMessage.setText("");
            }
        });
    }


}
