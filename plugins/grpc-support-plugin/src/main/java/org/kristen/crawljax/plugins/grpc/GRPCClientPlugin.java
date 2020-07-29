package org.kristen.crawljax.plugins.grpc;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.*;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.plugin.*;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.Identification;
import com.crawljax.core.state.StateVertex;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class GRPCClientPlugin implements OnBrowserCreatedPlugin, OnFireEventSucceededPlugin, OnUrlFirstLoadPlugin{
    @Override
    public void onBrowserCreated(EmbeddedBrowser newBrowser) {

    }

    @Override
    public void onFireEventSucceeded(CrawlerContext context, Eventable eventable, List<Eventable> pathToFailure) {

    }

    @Override
    public void onUrlFirstLoad(CrawlerContext context) {

    }
}
