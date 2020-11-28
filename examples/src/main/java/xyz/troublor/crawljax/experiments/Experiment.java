package xyz.troublor.crawljax.experiments;

import com.crawljax.core.CrawlTaskConsumer;
import com.crawljax.core.CrawljaxRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Experiment {
    protected abstract CrawljaxRunner initialize(String chromeDebuggerAddress);

    public void start(String statusLogPath, String chromeDebuggerAddress) throws IOException {
        CrawljaxRunner crawljaxRunner = this.initialize(chromeDebuggerAddress);
        CrawlTaskConsumer consumer = crawljaxRunner.callRtnConsumer();
        consumer.crawler.close();
        try (FileWriter writer = new FileWriter(new File(statusLogPath))) {
            writer.write(crawljaxRunner.getReason().toString());
        }

        System.out.println("Crawl Complete: " + crawljaxRunner.getReason());
    }
}
