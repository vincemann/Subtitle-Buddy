package io.github.vincemann.subtitleBuddy.guice.modules.mockModules;

import io.github.vincemann.subtitleBuddy.config.propertiesFile.PropertiesFile;
import io.github.vincemann.subtitleBuddy.config.uiStringsFile.UIStringsFile;
import io.github.vincemann.subtitleBuddy.filechooser.FileChooser;
import io.github.vincemann.subtitleBuddy.gui.dialog.alertDialog.AlertDialog;
import io.github.vincemann.subtitleBuddy.gui.dialog.continueDialog.ContinueDialog;
import io.github.vincemann.subtitleBuddy.guice.mockedProviders.MockedFileChooserProvider;
import io.github.vincemann.subtitleBuddy.module.FileChooserModule;


public class MockFileChooserModule extends FileChooserModule {


    public MockFileChooserModule(UIStringsFile stringConfiguration, PropertiesFile propertiesConfiguration) {
        super(stringConfiguration, propertiesConfiguration);
    }

    @Override
    protected void initClassBindings() {
        bind(FileChooser.class).toProvider(new MockedFileChooserProvider());
        bind(ContinueDialog.class).toInstance(new ContinueDialog() {
            @Override
            public boolean askUserToContinue(String message) {
                return true;
            }
        });
        bind(AlertDialog.class).toInstance(new AlertDialog() {
            @Override
            public void tellUser(String message) {
                //do nothing
            }
        });
    }
}
