package org.javamyadmin.jtwig;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.model.expression.Expression;
import org.jtwig.model.position.Position;
import org.jtwig.model.tree.CompositeNode;
import org.jtwig.model.tree.Node;
import org.jtwig.parser.addon.AddonParserProvider;
import org.jtwig.parser.parboiled.ParserContext;
import org.jtwig.parser.parboiled.base.LimitsParser;
import org.jtwig.parser.parboiled.base.PositionTrackerParser;
import org.jtwig.parser.parboiled.base.SpacingParser;
import org.jtwig.parser.parboiled.expression.AnyExpressionParser;
import org.jtwig.parser.parboiled.node.AddonParser;
import org.jtwig.parser.parboiled.node.CompositeNodeParser;
import org.jtwig.render.RenderRequest;
import org.jtwig.render.expression.CalculateExpressionService;
import org.jtwig.render.expression.calculator.operation.binary.BinaryOperator;
import org.jtwig.render.expression.calculator.operation.binary.calculators.BinaryOperationCalculator;
import org.jtwig.render.node.RenderNodeService;
import org.jtwig.render.node.renderer.NodeRender;
import org.jtwig.renderable.Renderable;
import org.jtwig.renderable.impl.StringRenderable;
import org.jtwig.web.servlet.JtwigRenderer;
import org.parboiled.Rule;

/**
 * We add some PMA functions and some "original" Twig behaviours to JTwig
 * 
 * @author lucav
 *
 */
public class JtwigFactory {

	public static final String BASE_PATH = "WEB-INF/templates/";

	private static JtwigRenderer rendererInstance;
	private static EnvironmentConfiguration configuration;

	/**
	 * 'trans' node rendering stuff. This class represents the syntax tree node.
	 */
	public static class TransNode extends Node {
		Expression expression;
		CompositeNode content;

		protected TransNode(Position position, Expression expression) {
			super(position);
			this.expression = expression;
		}

		public TransNode(Position position, CompositeNode content) {
			super(position);
			this.content = content;
		}

		public Expression getExpression() {
			return expression;
		}

		public CompositeNode getContent() {
			return content;
		}
	}

	/**
	 * 'trans' node rendering stuff. This is the class that renders output.
	 */
	public static class TransNodeRender implements NodeRender<TransNode> {

		@Override
		public Renderable render(RenderRequest renderRequest, TransNode node) {
			Object jtwigValue = null;
			if (node.getExpression() != null) {
				CalculateExpressionService calculateExpressionService = renderRequest.getEnvironment()
						.getRenderEnvironment().getCalculateExpressionService();
				jtwigValue = calculateExpressionService.calculate(renderRequest, node.getExpression());
			} else if (node.getContent() != null) {
				RenderNodeService renderNodeService = renderRequest.getEnvironment().getRenderEnvironment()
						.getRenderNodeService();
				jtwigValue = renderNodeService.render(renderRequest, node.getContent());
			}

			// TODO bundles stuff is not the right choice

			return new StringRenderable(jtwigValue.toString());
		}
	}

	/**
	 * 'trans' node rendering stuff. This is the class that identifies input string.
	 */
	public static class TransParser extends AddonParser {

		public TransParser(ParserContext context) {
			super(TransParser.class, context);
		}

		@Override
		public Rule NodeRule() {
			PositionTrackerParser positionTrackerParser = parserContext().parser(PositionTrackerParser.class);
			LimitsParser limitsParser = parserContext().parser(LimitsParser.class);
			SpacingParser spacingParser = parserContext().parser(SpacingParser.class);
			AnyExpressionParser anyExpressionParser = parserContext().parser(AnyExpressionParser.class);
			CompositeNodeParser compositeNodeParser = parserContext().parser(CompositeNodeParser.class);
			return Sequence(positionTrackerParser.PushPosition(),

					limitsParser.startCode(), spacingParser.Spacing(), String("trans"), spacingParser.Spacing(),

					Mandatory(FirstOf(

							// single tag
							Sequence(anyExpressionParser.ExpressionRule(), spacingParser.Spacing(),
									limitsParser.endCode(), push(new TransNode(positionTrackerParser.pop(1),
											anyExpressionParser.pop()))),
							Sequence(
									// start tag
									limitsParser.endCode(),

									// body
									compositeNodeParser.NodeRule(),

									// end tag
									Sequence(limitsParser.startCode(), spacingParser.Spacing(), String("endtrans"),
											spacingParser.Spacing(), limitsParser.endCode(),
											push(new TransNode(positionTrackerParser.pop(1),
													compositeNodeParser.pop()))))),
							"Missing end tag for 'trans'"));
		}
	}

	/**
	 * 'is empty' construct
	 */
	public static class EmptyFunction extends JtwigFunction1Ary {

		public EmptyFunction() {
			super("empty",
					value -> value == null || "".equals(value)
							|| (value instanceof Number && ((Number) value).doubleValue() == 0.0)
							|| (value instanceof Collection && ((Collection<?>) value).isEmpty())
							|| (value instanceof Map && ((Map<?, ?>) value).isEmpty()));
		}
	};

	/**
	 * Twig '?' operator is different from JTwig's one
	 * 
	 * (x ? y) means (x ? y : null)
	 */
	public static class IfOperator implements BinaryOperator {

		@Override
		public String symbol() {
			return "?";
		}

		@Override
		public int precedence() {
			// | 30
			// and or 25
			// != == 20
			// > <= 15
			// ~ 12
			// + - 10
			// / * 5
			// . 1
			// is ???
			return 29;
		}
	};

	/**
	 * Twig '?' operator is different from JTwig's one
	 * 
	 * (x ? y) means (x ? y : null)
	 */
	public static class IfCalculator implements BinaryOperationCalculator {

		@Override
		public Object calculate(Request request) {
			CalculateExpressionService calculateExpressionService = request.getEnvironment().getRenderEnvironment()
					.getCalculateExpressionService();
			Object leftValue = calculateExpressionService.calculate(request, request.getLeftOperand());
			Object rightValue = calculateExpressionService.calculate(request, request.getRightOperand());

			if (leftValue != null && !leftValue.equals("") && !leftValue.equals(false) && !leftValue.equals(0)
					&& !leftValue.equals(Collections.emptyList())) {
				return rightValue; // to be rendered yet ?!?
			} else {
				return "";
			}
		}
	}

	private static EnvironmentConfiguration createConfiguration() {
		return EnvironmentConfigurationBuilder.configuration().functions().add(new EmptyFunction())
				.add(Functions.getFunctions()).and().parser().addonParserProviders().add(new AddonParserProvider() {

					@Override
					public Class<? extends AddonParser> parser() {
						return TransParser.class;
					}

					@Override
					public Collection<String> keywords() {
						return Collections.emptyList();
					}
				}).and().binaryOperators().add(new IfOperator()).and().withoutTemplateCache().and().render()
				.nodeRenders().add(TransNode.class, new TransNodeRender()).and().binaryExpressionCalculators()
				.add(IfOperator.class, new IfCalculator()).and().and().build();
	}

	/**
	 * Provider
	 */
	public static EnvironmentConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = createConfiguration();
		}
		return configuration;
	}

	private static JtwigRenderer createRenderer() {
		return new JtwigRenderer(getConfiguration());
	}

	/**
	 * Provider
	 */
	public static JtwigRenderer getRenderer() {
		if (rendererInstance == null) {
			rendererInstance = createRenderer();
		}
		return rendererInstance;
	}

	/**
	 * Load a template from a file in classpath.
	 * 
	 * @see https://github.com/phpmyadmin/phpmyadmin/blob/master/libraries/classes/Template.php
	 */
	public static JtwigTemplate getTemplate(String templateName) {
		return JtwigTemplate.fileTemplate(BASE_PATH + templateName + ".twig", getConfiguration());
	}

	/**
	 * Load a template from a file in classpath, then render it.
	 * 
	 * @see https://github.com/phpmyadmin/phpmyadmin/blob/master/libraries/classes/Template.php
	 * @param templateName
	 * @param model
	 * @return
	 */
	public static String render(String templateName, Map<String, Object> model) {
		JtwigModel tmodel = JtwigModel.newModel();
		for (Entry<String, Object> entry : model.entrySet()) {
			tmodel.with(entry.getKey(), entry.getValue());
		}
		return getTemplate(templateName).render(tmodel);
	}
}