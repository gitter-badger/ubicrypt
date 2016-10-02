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
package ubicrypt.ui.tree;

import org.slf4j.Logger;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import static org.slf4j.LoggerFactory.getLogger;

public class RootFilesItem implements ITreeItem {
    private static final Logger log = getLogger(RootFilesItem.class);
    private final ContextMenu menu;
    private final ImageView imageView = new ImageView(new Image("/images/web-shield.png", 20, 20, true, true)) {{
        getStyleClass().add("tree-root");
    }};

    public RootFilesItem(final EventHandler<ActionEvent> fileAdder) {
        menu = new ContextMenu();
        final MenuItem remove = new MenuItem("Add File");
        remove.setOnAction(fileAdder);
        menu.getItems().add(remove);
    }

    @Override
    public Node getGraphics() {
        return imageView;
    }

    @Override
    public String getLabel() {
        return "Tracked Files";
    }

    @Override
    public Optional<EventHandler<? super MouseEvent>> onMousePrimaryClick() {
        return Optional.empty();
    }

    @Override
    public Optional<EventHandler<? super MouseEvent>> onMouseSecondaryClick() {
        return Optional.empty();
    }

    @Override
    public ContextMenu getContextMenu() {
        return menu;
    }
}
