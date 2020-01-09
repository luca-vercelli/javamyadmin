package org.javamyadmin.springmvc;

import static org.javamyadmin.php.Php.$_SESSION;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javamyadmin.helpers.DatabaseInterface;
import org.javamyadmin.helpers.Footer;
import org.javamyadmin.helpers.Header;
import org.javamyadmin.helpers.Menu;
import org.javamyadmin.helpers.Response;
import org.javamyadmin.helpers.Scripts;
import org.javamyadmin.php.Globals;
import org.javamyadmin.php.Php.SessionMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "org.javamyadmin.controllers" })
public class SpringConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
	    	.addResourceHandler("/favicon.ico")
	    	.addResourceLocations("/favicon.ico");
        registry
    		//.addResourceHandler("/js{pattern:/(?!whitelist|messages)([a-z0-9]+)\\.js.*}")
        	.addResourceHandler("/js/**/*.js")
        	.addResourceLocations("/js/");
        registry
    		.addResourceHandler("/js/**/*.css")
			.addResourceLocations("/js/");
        registry
        	.addResourceHandler("/themes/**")
        	.addResourceLocations("/themes/"); 
    }
    
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Globals getGLOBALS() {
		return new Globals();
	}
    
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public DatabaseInterface getDbi(Globals GLOBALS) {
		return new DatabaseInterface(GLOBALS);
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public SessionMap getSessionMap(HttpServletRequest request) {
		return $_SESSION(request.getSession());
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Response getResponse() {
		return new Response();
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Header getHeader() {
		return new Header();
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Footer getFooter() {
		return new Footer();
	}

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public Menu getMenu() {
		return new Menu();
	}
}
