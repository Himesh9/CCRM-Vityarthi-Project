package edu.ccrm;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private final Path dataDir;
    private final int maxCreditsPerSemester;

    private AppConfig() {
        this.dataDir = Paths.get(System.getProperty("user.home"), "ccrm-data");
        this.maxCreditsPerSemester = 24;
    }

    public static AppConfig get() {
        return INSTANCE;
    }

    public Path getDataDir() {
        return dataDir;
    }

    public int getMaxCreditsPerSemester() {
        return maxCreditsPerSemester;
    }
}
