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

import com.google.common.base.Throwables;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.controlsfx.control.NotificationPane;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import ubicrypt.core.crypto.PGPEC;
import ubicrypt.ui.Anchor;

import static org.slf4j.LoggerFactory.getLogger;

public class SettingsController implements Initializable {
    private static final Logger log = getLogger(SettingsController.class);

    @Autowired
    @Qualifier("keyPair")
    PGPKeyPair keyPair;
    @Inject
    PGPPrivateKey signKey;
    @FXML
    Button copyPKClipboard;
    @FXML
    Button addNewPK;
    @FXML
    Button importConfig;
    @FXML
    Button exportConfig;
    @FXML
    VBox topbox;
    @FXML
    AnchorPane anchor;
    @FXML
    private
    BorderPane mainPane;

    @PostConstruct
    public void init() {
        copyPKClipboard.setOnMouseClicked(event -> {
            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ArmoredOutputStream armor = new ArmoredOutputStream(out);
                armor.write(PGPEC.signPK(keyPair.getPublicKey(), signKey).getEncoded());
                armor.close();
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(new String(out.toByteArray()));
                clipboard.setContent(content);
//                anchor.getChildren().add(notification);
                final NotificationPane notification = new NotificationPane(mainPane);
                notification.setText("Public Key copied in clipboard");
                notification.show();
                log.info("public key copied into clipboard");
            } catch (final IOException e) {
                Throwables.propagate(e);
            }
        });

        addNewPK.setOnMouseClicked(event -> Anchor.anchor().browse("addNewPK"));
        importConfig.setOnMouseClicked(event -> Anchor.anchor().browse("importConfig"));
        exportConfig.setOnMouseClicked(event -> Anchor.anchor().browse("exportConfig"));

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Anchor.anchor().getControllerPublisher().onNext(this);
    }
}
