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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import ubicrypt.core.Utils;
import ubicrypt.core.dto.LocalConfig;
import ubicrypt.ui.Anchor;
import ubicrypt.ui.OnShow;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static org.slf4j.LoggerFactory.getLogger;

public class ShowConfigCtrl implements Initializable, OnShow {
    private static final Logger log = getLogger(ShowConfigCtrl.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Utils.configureMapper(mapper);
    }

    @Inject
    LocalConfig localConfig;
    @FXML
    private TextArea text;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Anchor.anchor().registerController(this);
    }

    @Override
    public void onShow() {
        try {
            text.setText(mapper.writeValueAsString(localConfig));
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
