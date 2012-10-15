<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="net.sourceforge.metware.binche.execs.BiNCheExecWeb"%>
<%@ page import="org.json.simple.*"%>

<html>
<head>
<title>BiNChe -- Enrichment analysis using ChEBI</title>

<!-- Style sheets -->
<link rel="stylesheet" type="text/css"
	href="${ pageContext.request.contextPath }/css/result.css">

<link rel="stylesheet" type="text/css"
	href="${ pageContext.request.contextPath }/css/jquery-ui-1.9.0.custom.css">

<!-- JavaScript -->
<script type="text/javascript"
	src="${ pageContext.request.contextPath }/javascript/json2.min.js"></script>

<script type="text/javascript"
	src="${ pageContext.request.contextPath }/javascript/AC_OETags.min.js"></script>

<script type="text/javascript"
	src="${ pageContext.request.contextPath }/javascript/cytoscapeweb.js"></script>

<script type="text/javascript"
	src="${pageContext.request.contextPath }/javascript/jquery-1.8.2.js"></script>

<script type="text/javascript"
	src="${pageContext.request.contextPath }/javascript/cytoscapeweb-file.js"></script>

<%-- This loads the cytoscapeweb site! <script type="text/javascript"
	src="${pageContext.request.contextPath }/javascript/demo.js"></script> --%>

<script type="text/javascript"
	src="${pageContext.request.contextPath }/javascript/cytoscapeweb-styles-demo.js"></script>

<script type="text/javascript"
	src="${pageContext.request.contextPath }/javascript/flash_detect_min.js"></script>

<script type="text/javascript"
	src="${pageContext.request.contextPath }/javascript/jquery-ui-1.9.0.custom.js"></script>

<script type="text/javascript">
	$ (function() {
						$("#save_file").click(function() {
							$(".save_screen").show();
						});

						$(".back").click(function() {
							$(".save_screen").hide();
						});

						$(function() {
							$("#slider").slider();
						});

						function path(str) {
							function has_slash(str) {
								return str.substr(0, 1) == "/";
							}

							if (window.location.protocol == "file:"
									|| window.location.protocol != "http:") {
								if (has_slash(str)) {
									return str.substr(1);
								} else {
									return str;
								}
							} else {
								if (has_slash(str)) {
									return str;
								} else {
									return "/" + str;
								}
							}
						}

						function default_file_name(extension) {
							var d = new Date();

							function pad(num) {
								if (num < 10) {
									return "0" + num;
								}
								return num;
							}

							return "network_" + d.getFullYear() + "."
									+ pad(d.getMonth() + 1) + "."
									+ pad(d.getDay()) + "_" + pad(d.getHours())
									+ "." + pad(d.getMinutes()) + "."
									+ extension;
						}

						function make_selection(fn, title, description,
								isImage, binary) {
							var id = "exporter_" + fn;

							/* var containerClass = isImage ? ".image_formats"
									: ".data_formats";

							$(".save_screen")
									.find(containerClass)
									.append("\
											<div class=\"selection\" id=\"save_" + fn + "\">\
						                    <div class=\"icon\"></div>\
						                    <div class=\"description\"><label>" + title + "</label>\
						                        <span>" + description + "</span></div>\
						                    <div id=\""+ id + "\"></div>\
						                </div>\
						            "); */
						
						var options = {
			                    swfPath: path("swf/Exporter"),
			                    flashInstallerPath: path("swf/playerProductInstall"),
			                    base64: binary,
			                	data: function(){
			                		return vis[fn]();
			                    },
			                    fileName: function() {
			                    	return default_file_name(fn);
			                    },
					            ready: function() {
					            	$("#"+id).trigger("available");
			                    }
			                };
			                
			            new org.cytoscapeweb.demo.Exporter(id, options);
			        }
			        
			        make_selection(
			            "xgmml",
			            "XGMML",
			            "eXtensible Graph Markup and Modeling Language",
			            false
			        );
			        make_selection(
			            "graphml",
			            "GraphML",
			            "Graph Markup Language",
			            false
			        );
			        make_selection(
			    		"sif",
			    		"SIF",
			    		"Simple Interaction Format",
			    		false
			        );
			        make_selection(
			            "svg",
			            "SVG",
			            "Vector Image",
			            true
			        );
			        make_selection(
			    		"png",
			    		"PNG",
			    		"Bitmap Image",
			    		true,
			    		true
			        );
		
					});
</script>
</head>
<body>
	<jsp:useBean id="bincheexec"
		class="net.sourceforge.metware.binche.execs.BiNCheExecWeb"></jsp:useBean>

	<%
		HashMap<String, String> inputMap = (HashMap<String, String>) request.getAttribute("inputMap");

		bincheexec.generateJson(inputMap, request, response);

		Object nodeList = session.getAttribute("nodeList");
		Object edgeList = session.getAttribute("edgeList");
	%>

	<div align="center" style="padding: 5px">
		<h3>
			The graph from the enrichment analysis for the target '<%=request.getParameter("type")%>'
		</h3>
		<p>
			The number of compounds input :
			<%=request.getAttribute("inputSize")%></p>
		<p>
			The number of compounds in the enrichment graph :
			<%=request.getAttribute("outputSize")%></p>
	</div>
	<br>

	<div id="cytoweb_container">
		<div id="save_file">
			<label style="cursor: pointer;">Save File</label>
		</div>

		<script type="text/javascript">
			window.onload = function() {
				// id of Cytoscape Web container div
				var div_id = "cytoscapeweb";

				// spcify the data schema and data to use (grab the data from server)
				var network_json = {
					dataSchema : {
						nodes : [ {
							name : "label",
							type : "string"
						}, {
							name : "color",
							type : "string"
						} ],

						edges : [ {
							name : "directed",
							type : "boolean",
							defValue : true
						} ]
					},

					data : {
						nodes : <%=nodeList%>,

						edges : <%=edgeList%>
					}
				};

				// initialization options
				var options = {
					swfPath : "${ pageContext.request.contextPath }/swf/CytoscapeWeb",
					flashInstallerPath : "${ pageContext.request.contextPath }/swf/playerProductInstall"
				};

				// initialise
				var vis = new org.cytoscapeweb.Visualization(div_id, options);

				//specify the graph and layout to draw
				var draw_options = {
					network : network_json,
					layout : {
						name : "Tree",
						options : {
							orientation : "topToBottom",
							breadthSpace : 120,
							depthSpace : 50,
							subtreeSpace : 20
						}
					},
					visualStyle : {
						global : {
							backgroundColor : "#ffffff",
						},
						nodes : {
							shape : "roundrect",
							size : "auto",
							color : {
								passthroughMapper : {
									attrName : "color"
								}
							}
						},
						edges : {
							color : "#000000",
						}
					}
				};

				vis
						.ready(function() {
							$("#save_xgmml")
									.click(
											function() {
												vis
														.exportNetwork('xgmml',
																'${ pageContext.request.contextPath }/GraphExporter?type=xml');
											});

							$("#save_graphml")
									.click(
											function() {
												vis
														.exportNetwork(
																'graphml',
																'${ pageContext.request.contextPath }/GraphExporter?type=xml');
											});

							$("#save_sif")
									.click(
											function() {
												vis
														.exportNetwork('sif',
																'${ pageContext.request.contextPath }/GraphExporter?type=txt');
											});

							$("#save_png")
									.click(
											function() {
												vis
														.exportNetwork('png',
																'${ pageContext.request.contextPath }/GraphExporter?type=png');
											});

							$("#save_svg")
									.click(
											function() {
												vis
														.exportNetwork('svg',
																'${ pageContext.request.contextPath }/GraphExporter?type=svg');
											});

						});

				//draw
				vis.draw(draw_options);

			};
		</script>

		<div id="cytoscapeweb">The graph goes here.</div>

		<div class="save_screen">
			<div class="back">
				<span>Back to graph</span>
				<div class="icon"></div>
			</div>
			<div class="selections">
				<div class="description">Select a file type.</div>
				<h3>Network Data</h3>
				<div class="data_formats">

					<!-- XGMML Format -->
					<div class="selection" id="save_xgmml">
						<div class="description">
							<label>XGMML</label> <span>eXtensible Graph Markup and
								Modeling Language</span>
						</div>
						<div id="exporter_xgmml"></div>
					</div>

					 <!-- GRAPHML Format -->
					<div class="selection" id="save_graphml">
						<div class="description">
							<label>GraphML</label> <span>Graph Markup Language</span>
						</div>
						<div id="exporter_graphml"></div>
					</div>

					 <!-- SIF Format -->
					<div class="selection" id="save_sif">
						<div class="description">
							<label>SIF</label> <span>Simple Interaction Format</span>
						</div>
						<div id="exporter_sif"></div>
					</div>

				</div>
				<h3>Image</h3>
				<div class="image_formats">

					<!-- SVG Format -->
					<div class="selection" id="save_svg">
						<div class="description">
							<label>SVG</label> <span>Vector Image</span>
						</div>
						<div id="exporter_svg"></div>
					</div>

					<!--  PNG Format --> 
					<div class="selection" id="save_png">
						<div class="description">
							<label>PNG</label> <span>Bitmap Image</span>
						</div>
						<div id="exporter_png"></div>
					</div>

				</div>
			</div>

		</div>

		<div class="side">
			<div class="side_header">
				<span>Filter</span>
			</div>
			<div id="filter">
				<div class="slider_area">
					<span class="slider_min">30</span> <span class="slider_max">80</span>
					<div id="slider" class="slider">
						<div class="ui-slider-range ui-widget-header"
							style="left: 0%; width: 100%;"></div>
						<a href="#" class="ui-slider-handle" style="left: 0%;"></a> <a
							href="#" class="ui-slider-handle" style="left: 100%;"></a>
					</div>
				</div>
			</div>
		</div>

	</div>
</body>
</html>