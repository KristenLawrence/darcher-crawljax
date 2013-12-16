package com.crawljax.plugins.clickable_detector;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;

import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.plugins.CrawljaxProxyPlugin;
import com.crawljax.plugins.JavaScriptInjectorFilterSource;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProxyTest {


	@Test
	public void testProxyPassThrough() throws MalformedURLException {
		String script = "<SCRIPT>console.log('Crawljax FTW')</SCRIPT>";
		CrawljaxProxyPlugin proxyPlugin = new CrawljaxProxyPlugin(55555);
		proxyPlugin.addFilter(new JavaScriptInjectorFilterSource(ImmutableList.of(script)));
		DomInterceptorPlugin interceptor = new DomInterceptorPlugin();
		CrawljaxConfiguration conf = CrawljaxConfiguration.builderFor("http://demo.crawljax.com")
														  .setProxyConfig(proxyPlugin.getConfiguration())
														  .setMaximumStates(5)
														  .addPlugin(proxyPlugin)
		  												  .addPlugin(interceptor)
														  .build();
		CrawljaxRunner runner = new CrawljaxRunner(conf);
		runner.call();
		assertThat(interceptor.getIntercepted(), everyItem(containsString(script)));
	}



}
