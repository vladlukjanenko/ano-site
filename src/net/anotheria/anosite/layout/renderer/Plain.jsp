<%@ page
	contentType="text/html;charset=iso-8859-15" session="true"
%><%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean"
%><%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html"
%><%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic"
%>

<bean:define id="box" toScope="page" name="__box" scope="request" type="net.anotheria.anosite.content.bean.BoxBean"/>
<logic:notEqual name="box" property="content" value="">
	<bean:write name="box" property="content" filter="false"/>
</logic:notEqual>

<bean:define id="__boxlist" toScope="request" name="box" property="subboxes"/>
<jsp:include page="../templates/ListIterator.jsp" flush="false"/>