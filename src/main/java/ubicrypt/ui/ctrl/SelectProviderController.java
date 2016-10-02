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

import org.slf4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ubicrypt.core.provider.ProviderDescriptor;
import ubicrypt.ui.Anchor;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

public class SelectProviderController implements Initializable {
    private static final Logger log = getLogger(SelectProviderController.class);
    @Resource
    List<ProviderDescriptor> providerDescriptors;
    @Inject
    Anchor ctx;
    @FXML
    ListView<ProviderDescriptor> providers;

    @PostConstruct
    public void init() {
        providers.getItems().clear();
        providerDescriptors.stream().forEach(pd -> providers.getItems().add(pd));
    }


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        providers.setCellFactory(listView -> new ListCell<ProviderDescriptor>() {

            @Override
            protected void updateItem(ProviderDescriptor provider, boolean empty) {
                super.updateItem(provider, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                setText(provider.getDescription());
                setGraphic(provider.getLogo());
            }
        });
        providers.setOnMouseClicked(mouseEvent -> {
            ctx.browse(format("provider/%s", providers.getSelectionModel().getSelectedItem().getCode()));
        });
        Anchor.anchor().getControllerPublisher().onNext(this);

    }


}
