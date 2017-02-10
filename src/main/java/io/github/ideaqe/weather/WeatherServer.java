package io.github.ideaqe.weather;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.github.ideaqe.weather.provider.CollisionMapper;
import io.github.ideaqe.weather.provider.JacksonConfig;
import io.github.ideaqe.weather.provider.StatusFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class WeatherServer extends Server {

	public static void main(String[] args) throws Exception {
		WeatherServer server = new WeatherServer(8080);
		server.start();
		server.join();
	}

	public WeatherServer(int port) {
		super(port);

		ResourceConfig resourceConfig = new ResourceConfig();
		resourceConfig.register(StatusFilter.class);
		resourceConfig.register(JacksonConfig.class);
		resourceConfig.register(CollisionMapper.class);
		resourceConfig.register(JacksonJsonProvider.class);
		resourceConfig.register(WeatherService.class);
		ServletContainer servletContainer = new ServletContainer(resourceConfig);

		ServletHolder servletHolder = new ServletHolder(servletContainer);
		servletHolder.setInitOrder(0);

		ServletContextHandler handler = new ServletContextHandler(NO_SESSIONS);
		handler.setContextPath("/");
		handler.addServlet(servletHolder, "/*");
		setHandler(handler);
	}
}
