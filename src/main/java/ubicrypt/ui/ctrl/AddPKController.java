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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import ubicrypt.core.crypto.PGPEC;
import ubicrypt.ui.OnShow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.slf4j.LoggerFactory.getLogger;
import static ubicrypt.ui.Anchor.anchor;

public class AddPKController implements Initializable, OnShow {
    private static final Logger log = getLogger(AddPKController.class);
    @FXML
    Button add;
    @FXML
    Button cancel;
    @FXML
    TextArea text;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        anchor().getControllerPublisher().onNext(this);
    }

    @Override
    public void onShow() {
        add.setDisable(true);
        cancel.setOnMouseClicked(event -> anchor().popScene());
        text.setOnKeyTyped(event -> {
            if (StringUtils.isEmpty(text.getText())) {
                add.setDisable(true);
            } else {
                add.setDisable(false);
            }
        });
        add.setOnMouseClicked(event -> {
            try {
                final ArmoredInputStream is = new ArmoredInputStream(new ByteArrayInputStream(text.getText().getBytes()));
                final PGPPublicKey newPK = PGPEC.decodePK(is);
                anchor().browse("confirmNewPK", newPK);
            } catch (final IOException e) {
                Throwables.propagate(e);
            }
        });

    }
}
