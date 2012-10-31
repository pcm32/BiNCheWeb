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

	<%
		RenderedImage bfImage = bincheexec.generateImage(inputMap, request,
				response);
	%>
	<center>
		<table>
			<tr>
				<td>
					<div style="padding: 10px">
						<img alt="Chebi Graph" width="1024" height="768" border="1"
							title="Chebi Graph"
							src="${ pageContext.request.contextPath }/ImageViewer" />
					</div>
				</td>
			</tr>
		</table>
	</center>
</body>
<!-- Backup of export format divs -->
			<div class="selections">
				<div class="description">Select a file type.</div>
				<h3>Network Data</h3>
				<div class="data_formats">

					<!-- XGMML Format 
					<div class="selection" id="save_xgmml">
						<div class="description">
							<label>XGMML</label> <span>eXtensible Graph Markup and
								Modeling Language</span>
						</div>
						<div id="exporter_xgmml"></div>
					</div>

					 GRAPHML Format 
					<div class="selection" id="save_graphml">
						<div class="description">
							<label>GraphML</label> <span>Graph Markup Language</span>
						</div>
						<div id="exporter_graphml"></div>
					</div>

					 SIF Format 
					<div class="selection" id="save_sif">
						<div class="description">
							<label>SIF</label> <span>Simple Interaction Format</span>
						</div>
						<div id="exporter_sif"></div>
					</div>
-->
				</div>
				<h3>Image</h3>
				<div class="image_formats">

					<!-- SVG Format 
					<div class="selection" id="save_svg">
						<div class="description">
							<label>SVG</label> <span>Vector Image</span>
						</div>
						<div id="exporter_svg"></div>
					</div>

					 PDF Format 
					<div class="selection" id="save_pdf">
						<div class="description">
							<label>PDF</label> <span>Vector Image</span>
						</div>
						<div id="exporter_pdf"></div>
					</div>

					 PNG Format 
					<div class="selection" id="save_png">
						<div class="description">
							<label>PNG</label> <span>Bitmap Image</span>
						</div>
						<div id="exporter_png"></div>
					</div>
-->
				</div>
			</div>
</html>