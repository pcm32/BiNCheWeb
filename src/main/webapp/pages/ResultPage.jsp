<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="net.sourceforge.metware.binche.execs.BiNCheExecWeb"%>
<%@ page import="java.awt.image.RenderedImage"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
<title>BiNChe -- Enrichment analysis using ChEBI</title>
</head>
<body>

	<%
		HashMap<String, String> inputMap = (HashMap<String, String>) request
			.getAttribute("inputMap");
	%>

	<jsp:useBean id="bincheexec"
		class="net.sourceforge.metware.binche.execs.BiNCheExecWeb"></jsp:useBean>

	<div align="center" style="padding: 5px">
		<h3>The graph from the enrichment analysis</h3>
	</div>
	<br>

	<center>
		<table>
			<tr>
				<td width="100%"><script type='text/javascript'>					
				<%RenderedImage bfImage = bincheexec.generateImage(inputMap, request,
						response);%>					
				</script>
					<div id="center-container" style="padding: 10px">
						<img alt="Chebi Graph" width="1024" height="768" border="1"
							title="Chebi Graph"
							src="${ pageContext.request.contextPath }/ImageViewer" />
					</div></td>
			</tr>
		</table>
	</center>
</body>
</html>