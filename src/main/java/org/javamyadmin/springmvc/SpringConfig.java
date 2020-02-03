package org.javamyadmin.springmvc;

import static org.javamyadmin.php.Php.$_SESSION;

import javax.servlet.http.HttpServletRequest;

import org.javamyadmin.helpers.Console;
import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Footer;
import org.javamyadmin.helpers.Header;
import org.javamyadmin.helpers.Menu;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Table;
import org.javamyadmin.helpers.Template;
import org.javamyadmin.helpers.UserPreferences;
import org.javamyadmin.helpers.navigation.Navigation;
import org.javamyadmin.helpers.navigation.NavigationTree;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "org.javamyadmin.controllers" })
public class SpringConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon.ico");
		registry
				// .addResourceHandler("/js{pattern:/(?!whitelist|messages)([a-z0-9]+)\\.js.*}")
				.addResourceHandler("/js/**/*.js").addResourceLocations("/js/");
		registry.addResourceHandler("/js/**/*.css").addResourceLocations("/js/");
		registry.addResourceHandler("/themes/**").addResourceLocations("/themes/");
	}

	@Bean
	@RequestScope
	// @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode =
	// ScopedProxyMode.TARGET_CLASS)
	public Globals getGLOBALS() {
		return new Globals();
	}

	@Bean
	@RequestScope
	public DatabaseInterface getDbi() {
		return new DatabaseInterface();
	}

	@Bean
	@RequestScope
	public SessionMap getSessionMap(HttpServletRequest request) {
		return $_SESSION(request.getSession());
	}

	@Bean
	@RequestScope
	public Response getResponse() {
		return new Response();
	}

	@Bean
	@RequestScope
	public Header getHeader() {
		return new Header();
	}

	@Bean
	@RequestScope
	public Footer getFooter() {
		return new Footer();
	}

	@Bean
	@RequestScope
	public Menu getMenu() {
		return new Menu();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Table getTable(String $db, String $table) {
		return new Table($db, $table);
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Template getTemplate() {
		return new Template();
	}

	@Bean
	@RequestScope
	public Navigation getNavigation() {
		return new Navigation();
	}

	@Bean
	@RequestScope
	public NavigationTree getNavigationTree(HttpServletRequest req, Globals GLOBALS) {
		return new NavigationTree(req, GLOBALS);
	}

	@Bean
	@RequestScope
	public UserPreferences getUserPreferences() {
		return new UserPreferences();
	}

	@Bean
	@RequestScope
	public Console getConsole() {
		return new Console();
	}

	/* TODO @Bean
	@RequestScope
	public ThemeManager getThemeManager(HttpServletRequest req) {
		return new ThemeManager(req);
	}*/
}
