package xyz.troublor.crawljax.experiments;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.CrawlerContext;
import com.crawljax.core.ExitNotifier;
import com.crawljax.core.plugin.OnFireEventSucceededPlugin;
import com.crawljax.core.plugin.PostCrawlingPlugin;
import com.crawljax.core.plugin.PreResetPlugin;
import com.crawljax.core.state.Eventable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ClientSideCoverageCollectorPlugin implements PreResetPlugin, OnFireEventSucceededPlugin, PostCrawlingPlugin {
    Path covSaveDir;
    String lastCovJson = null;

    public ClientSideCoverageCollectorPlugin(Path covSaveDir) {
        this.covSaveDir = covSaveDir;
    }

    private String fetchCovJson(EmbeddedBrowser browser) {
        return (String) browser.executeJavaScript("return JSON.stringify(window.__coverage__)");
    }

    private void saveCovJson(String covJson) {
        if (!Files.exists(this.covSaveDir)) {
            try {
                Files.createDirectories(this.covSaveDir);
            } catch (IOException e) {
                System.err.println("Create coverage save dir failed: " + e.getMessage());
            }
        }
        try {
            Path file = Files.createTempFile(this.covSaveDir, "cov-", ".json");
            FileWriter myWriter = new FileWriter(file.toString());
            myWriter.write(covJson);
            myWriter.close();
        } catch (IOException e) {
            System.err.println("Save coverage failed: " + e.getMessage());
        }
    }

    @Override
    public void preReset(CrawlerContext context) {
        String covJson = fetchCovJson(context.getBrowser());
        saveCovJson(covJson);
    }


    @Override
    public void onFireEventSucceeded(CrawlerContext context, Eventable eventable, List<Eventable> pathToFailure) {
        lastCovJson = fetchCovJson(context.getBrowser());
    }

    @Override
    public void postCrawling(CrawlSession session, ExitNotifier.ExitStatus exitReason) {
        saveCovJson(lastCovJson);
    }
}
