package com.github.duskbyte;

import com.github.duskbyte.events.bus.EventBus;
import com.github.duskbyte.managers.AddonManager;
import com.github.duskbyte.managers.ConfigManager;
import com.github.duskbyte.managers.HealthManager;
import com.github.duskbyte.managers.ModuleManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;

public class DuskByte {

    public static final String MOD_ID = BuildConfig.MOD_ID;
    public static final String VERSION = BuildConfig.VERSION;

    public static final Logger LOGGER = LogManager.getLogger("DuskByte");

    public static int skipTicks;

    public static void init() {
        LOGGER.info("嘿 DuskByte 启动了 准备搞事");

        EventBus.INSTANCE.registerLambdaFactory(DuskByte.class.getPackageName(), (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));

        ModuleManager.INSTANCE.initModules();
        AddonManager.INSTANCE.setupAddons();
        ConfigManager.INSTANCE.initConfig();
        HealthManager.INSTANCE.getClass();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ConfigManager.INSTANCE.saveNow();
            DuskByte.LOGGER.info("配置存好了 下次见");
        }));

        DuskByte.LOGGER.info("DuskByte 加载完了 开始整活");
    }

}
