<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:webpage>
	<article>
		<h1>404</h1>
		<hr>
		<p>Sorry, we can't seem to find the page you are trying to access.</p>
		<p>Did you maybe try to access the page via an old bookmark?</p>
		<p><a href="${pageContext.request.contextPath}\">Back</a></p>
	</article>
</t:webpage>