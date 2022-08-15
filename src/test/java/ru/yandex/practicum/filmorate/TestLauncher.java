package ru.yandex.practicum.filmorate;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class TestLauncher {

    public static void main(String... args) {
        var launcher = LauncherFactory.create();
        var listener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(DiscoverySelectors.selectPackage(""))
                .build();
        launcher.execute(request, listener);
        try (var writer = new PrintWriter(System.out)) {
            listener.getSummary().printTo(writer);
        }
    }
}
