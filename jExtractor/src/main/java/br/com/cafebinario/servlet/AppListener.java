package br.com.cafebinario.servlet;

import java.util.Enumeration;

import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import br.com.cafebinario.properties.AppProperties;

@WebListener
public class AppListener {

	private static Logger LOG = Logger.getLogger(AppListener.class);

	public AppListener() {
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		LOG.debug("AppListener.contextDestroyed");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		Enumeration<Servlet> servletsEnumaration = arg0.getServletContext()
				.getServlets();

		LOG.debug("ServerInfo=" + arg0.getServletContext().getServerInfo());
		LOG.debug("ServletContextName="
				+ arg0.getServletContext().getServletContextName());

		LOG.debug("Servlets Loader:");
		while (servletsEnumaration.hasMoreElements()) {
			Servlet servlet = servletsEnumaration.nextElement();
			LOG.debug("\t ServletClass=" + servlet.getClass());
		}

		LOG.debug("AppListener.contextInitialized");
		LOG.debug("Load: "
				+ arg0.getServletContext().getRealPath("App.properties"));
		AppProperties.instanceOf().setPathFile(
				arg0.getServletContext().getRealPath("App.properties"));
		AppProperties.instanceOf().load();
	}
}
