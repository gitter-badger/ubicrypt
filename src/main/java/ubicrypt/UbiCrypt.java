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
package ubicrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import ubicrypt.core.FixPassPhraseInitializer;
import ubicrypt.core.UbiConf;
import ubicrypt.core.Utils;

import static ubicrypt.core.Utils.securityFile;
import static ubicrypt.core.Utils.ubiqFolder;
import static ubicrypt.ui.Anchor.anchor;

@Lazy
public class UbiCrypt extends Application {
    private static final Logger log = LoggerFactory.getLogger(UbiCrypt.class);
    public static String[] arguments = {};
    private ConfigurableApplicationContext ctx;


    public static void main(final String[] args) throws IOException, InterruptedException {
        arguments = args;
        ubiqFolder().toFile().mkdirs();
        if (Utils.isAppInUse(ubiqFolder())) {
            log.error("UbiCrypt already running. Quit");
            return;
        }
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (ctx != null) {
            ctx.close();
        }
    }

    @Override
    public void start(final Stage stage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);
        stage.setTitle("UbiCrypt");
        anchor().setStage(stage);
        final File file = securityFile().toFile();
        stage.setScene(anchor().showScene(file.exists() ? "login" : "createKey"));
        stage.setWidth(350);
        stage.setHeight(700);
        stage.show();
        final UbiCrypt ubiCrypt = this;
        anchor().getPasswordStream().subscribe(pwd -> {
            final SpringApplication app = new SpringApplication(UbiConf.class);
            app.addInitializers(new FixPassPhraseInitializer(pwd));
            app.setLogStartupInfo(true);
            ctx = app.run(arguments);
            ctx.getAutowireCapableBeanFactory().autowireBean(ubiCrypt);
            ctx.getBeanFactory().registerSingleton("stage", stage);
            ctx.getBeanFactory().registerSingleton("ctx", anchor());
            anchor().showScene("home");
            anchor().getControllerStream().subscribe(initializable -> {
                log.debug("register in spring:{}", initializable);
                Utils.springIt(ctx, initializable);
//                ctx.getBeanFactory().registerSingleton(initializable.getClass().getSimpleName(), initializable);
            });
        });


    }
}
