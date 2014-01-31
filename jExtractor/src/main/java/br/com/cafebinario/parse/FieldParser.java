package br.com.cafebinario.parse;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import br.com.cafebinario.properties.AppProperties;
import br.com.cafebinario.transport.GenericFilter;

public class FieldParser {

	protected static Logger LOG = Logger.getLogger(FieldParser.class);

	public GenericFilter parse(HttpServletRequest request) {
		LOG.debug("Parse request to GenericFilter:");

		GenericFilter parameter = new GenericFilter();
		String interfaceDefinition = AppProperties.instanceOf().getProperty(
				"InterfaceWebDefinition");
		LOG.debug("interfaceDefinition=" + interfaceDefinition);

		String[] filds = interfaceDefinition.split("[,]");

		String intefaceType = request.getParameter("InterfaceType");
		LOG.debug("InterfaceType=" + intefaceType);
		parameter.setInterfaceType(intefaceType);

		for (String value : filds) {
			String parameterValue = request.getParameter(value);
			Integer key = new Integer(AppProperties.instanceOf().getProperty(
					value));
			parameter.put(key, parameterValue);

			LOG.debug("parameterKey=" + key.intValue());
			LOG.debug("parameterValue=" + parameterValue);
		}

		return parameter;
	}

	// WorkFlowFileGenerator
	public GenericFilter parse(String[] args) {
		LOG.debug("Parse args to GenericFilter:");
		GenericFilter parameter = new GenericFilter();
		int i = 1;
		while (i < args.length) {
			LOG.debug(i + "=" + args[i]);
			if (i != args.length - 1) {
				parameter.put(i, args[i]);
			} else {
				parameter.setInterfaceType(args[i]);
			}
			i++;
		}

		return parameter;
	}
}
