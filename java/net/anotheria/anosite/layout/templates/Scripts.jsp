<%@ page contentType="text/html;charset=UTF-8" session="true"
%><%@ page isELIgnored ="false" 
%><%@ taglib uri="/tags/struts-bean" prefix="bean"
%><%@ taglib uri="/tags/struts-html" prefix="html"
%><%@ taglib uri="/tags/struts-logic" prefix="logic"
%><%@ taglib uri="/WEB-INF/tld/anosite.tld" prefix="as"
%><logic:iterate name="page" property="scripts" type="net.anotheria.anosite.content.bean.ScriptBean" id="script"
	><logic:equal name="anosite.verbose" value="true"><!-- AS Script: Script id: <bean:write name="script" property="id"/>, name: <bean:write name="script" property="name"/> --></logic:equal>
<logic:notEmpty name="script" property="link"><script type="text/javascript" src="${pageContext.request.contextPath}/<bean:write name="script" property="link" filter="false"/><logic:match name="script" property="link" value="?">&</logic:match><logic:notMatch name="script" property="link" value="?">?</logic:notMatch><as:random/>"></script></logic:notEmpty>
<logic:notEmpty name="script" property="content"><script type="text/javascript"><bean:write name="script" property="content" filter="false"/></script></logic:notEmpty>
</logic:iterate>
