package org.javamyadmin.jtwig;

import java.util.Collection;
import java.util.Collections;

import org.jtwig.functions.JtwigFunction;

/**
 * Abstract implementation
 * 
 * @author lucav
 *
 */
public abstract class AbstractJtwigFunction implements JtwigFunction {

	protected String name;

	public AbstractJtwigFunction(String name) {
		this.name = name;
	}

	@Override
	public Collection<String> aliases() {
		return Collections.emptyList();
	}

	@Override
	public String name() {
		return name;
	}

}