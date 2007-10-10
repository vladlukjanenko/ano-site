package net.anotheria.anosite.content.variables;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ConstantsProcessor implements VariablesProcessor{
	
	private static Map<String,String> constants = new HashMap<String, String>();
	static{
		constants.put("spacer", "&nbsp;");
		constants.put("euro", "&euro;");
	}

	public String replace(String prefix, String variable, String defValue, HttpServletRequest req) {
		String val = constants.get(variable);
		return val == null ? 
				"Unknown constants: "+variable : val;
	}

}