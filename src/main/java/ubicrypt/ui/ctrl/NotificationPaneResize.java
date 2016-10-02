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

import org.controlsfx.control.NotificationPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class NotificationPaneResize extends Application {

    public static void main(final String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        try {
            final BorderPane root = new BorderPane();

            final MenuBar menuBar = new MenuBar();
            menuBar.getMenus().addAll(new Menu("File"), new Menu("Edit"));
            root.setTop(menuBar);

            final Button button = new Button("notification");
            final NotificationPane notificationPanel = new NotificationPane(button);
            notificationPanel.setShowFromTop(true);

            button.setOnAction((event) -> {
                notificationPanel.setText("pressed!");
                notificationPanel.show();
            });

            final AnchorPane ap = new AnchorPane(notificationPanel);
            AnchorPane.setRightAnchor(notificationPanel, 10.0);
            AnchorPane.setLeftAnchor(notificationPanel, 10.0);
            AnchorPane.setTopAnchor(notificationPanel, 0.0);
            AnchorPane.setBottomAnchor(notificationPanel, 0.0);
            ap.setStyle("-fx-background-color: red;");

            root.setCenter(ap);

            final Scene scene = new Scene(root, 400, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
