package com.youneedsoftware.subtitleBuddy.main;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.youneedsoftware.subtitleBuddy.classpathFileFinder.ClassPathFileFinder;
import com.youneedsoftware.subtitleBuddy.classpathFileFinder.SpringClassPathFileFinder;
import com.youneedsoftware.subtitleBuddy.config.propertyFile.ApachePropertiesFile;
import com.youneedsoftware.subtitleBuddy.config.propertyFile.PropertiesFile;
import com.youneedsoftware.subtitleBuddy.config.uiStringsFile.ApacheUIStringsFile;
import com.youneedsoftware.subtitleBuddy.config.uiStringsFile.UIStringsFile;
import com.youneedsoftware.subtitleBuddy.gui.stages.stageController.settingsStage.SettingsStageController;
import com.youneedsoftware.subtitleBuddy.guice.module.*;
import com.youneedsoftware.subtitleBuddy.service.EventHandlerService;
import com.youneedsoftware.subtitleBuddy.service.SrtService;
import com.youneedsoftware.subtitleBuddy.util.LoggingUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.Arrays;
import java.util.List;

@Log4j
@NoArgsConstructor
@Singleton
public class Main extends Application {

    public static final String CONFIG_FILE_PATH = "/application.properties";
    public static final String UI_STRINGS_CONFIG_FILE_PATH = "/application.string.properties";

    private SrtService srtService;
    private EventHandlerService eventHandlerService;
    private static Injector injector;

    @Override
    public void start(Stage primaryStage) throws Exception{
        LoggingUtils.disableUtilLogger();
        ClassPathFileFinder classPathFileFinder = new SpringClassPathFileFinder();
        PropertiesFile propertiesManager = new ApachePropertiesFile(classPathFileFinder.findFileOnClassPath(CONFIG_FILE_PATH).getFile());
        UIStringsFile stringConfiguration = new ApacheUIStringsFile(classPathFileFinder.findFileOnClassPath(UI_STRINGS_CONFIG_FILE_PATH).getFile());
        injector = createInjector(propertiesManager,stringConfiguration,primaryStage, classPathFileFinder);
        EventBus eventBus = injector.getInstance(EventBus.class);
        srtService= injector.getInstance(SrtService.class);
        eventHandlerService = injector.getInstance(EventHandlerService.class);
        //todo siehe in class die eventhandler in eigene module auslagern
        eventHandlerService.initEventHandlers();
        eventBus.register(srtService);

        SettingsStageController settingsStageController = injector.getInstance(SettingsStageController.class);
        settingsStageController.open();
        start();
    }

    private static Injector createInjector(PropertiesFile propertiesManager, UIStringsFile stringConfiguration, Stage primaryStage, ClassPathFileFinder classPathFileFinder){
        if(injector==null) {
            //use default modules
            List<Module> moduleList = Arrays.asList(
                    new ClassPathFileFinderModule(classPathFileFinder),
                    new ConfigFileModule(propertiesManager, stringConfiguration),
                    new FileChooserModule(stringConfiguration, propertiesManager) ,
                    new ParserModule(stringConfiguration, propertiesManager) ,
                    new GuiModule(stringConfiguration, propertiesManager, primaryStage),
                    new UserInputHandlerModule() ,
                    new SystemCommandModule() ,
                    new OsModule()
            );
           return Guice.createInjector(moduleList);
        }else {
            //use extern modules
            return injector;
        }
    }

    //only for testing Purposes
    public static void createInjector(Module... modules){
        injector = Guice.createInjector(Arrays.asList(modules));
    }

    public static Injector getInjector()  {
        return injector;
    }

    private void start() {
        srtService.startParser();
    }
}