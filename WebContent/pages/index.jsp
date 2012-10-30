<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>BiNChe -- Enrichment analysis using ChEBI</title>
<script type="text/javascript">
	function display(value) {
		var val = document.getElementById(value).value;
		if (val == "plain") {
			document.getElementById('plainInfo').style.display = 'block';
			document.getElementById('weightInfo').style.display = 'none';
		} else if (val == "weighted") {
			document.getElementById('plainInfo').style.display = 'none';
			document.getElementById('weightInfo').style.display = 'block';
		}
	}
</script>
<style type="text/css">
#loading {
	position: fixed;
	left: 0;
	top: 0;
	bottom: 0;
	width: 100%;
	height: 100%;
	min-height: 100%;
	background-color: white;
	text-align: center;
	opacity: 0.85;
	filter: alpha(opacity =       85);
}

#loadingtext {
	width: 100px;
	height: 100px;
	background-color: #FFF;
	text-align: center;
	padding: 100px 0 0 0;
	margin: 250px auto 0 auto;
}
</style>
</head>

<body>
	<center>
		<div id="loading" style="display:none;">
			<div id="loadingtext">
				Please wait...<br /> <img
					src="${ pageContext.request.contextPath }/images/ajax-loader.gif"
					border="0" />
			</div>
		</div>
		<h1>ENRICHMENT ANALYSIS</h1>
		<br /> Please enter your list of CHEBI ids, separated by tab, newline or comma. <br> <br> <br>


		<form action="${ pageContext.request.contextPath }/ValidateInput"
			method="post">
			<b>Chebi Ids</b>
			<table style="vertical-align: middle">
				<tr>
					<td style="padding: 10px"><textarea rows="10" cols="50"
							name="input">
                        CHEBI:10101
                        CHEBI:10102
                        CHEBI:101161
                        CHEBI:101335
                        CHEBI:102265
                        CHEBI:10283
                        CHEBI:10319
                        CHEBI:10334
                        CHEBI:103784
                        CHEBI:10432
                        CHEBI:104381
                        CHEBI:10577
                        CHEBI:10615
                        CHEBI:10702
                        CHEBI:10782
                        CHEBI:108133
                        CHEBI:108141
                        CHEBI:10983
                        CHEBI:114833
                        CHEBI:13719
                        CHEBI:142589
                        CHEBI:144308
                        CHEBI:144365
                        CHEBI:14498
                        CHEBI:14597
                        CHEBI:147265
                        CHEBI:14885
                        CHEBI:15035
                        CHEBI:15036
                        CHEBI:15040
                        CHEBI:15347
                        CHEBI:15349
                        CHEBI:15355
                        CHEBI:15375
                        CHEBI:15382
                        CHEBI:15383
                        CHEBI:15388
                        CHEBI:15389
                        CHEBI:15450
                        CHEBI:15451
                        CHEBI:15475
                        CHEBI:15481
                        CHEBI:15483</textarea></td>
				</tr>
				<tr>
					<td style="clear: both; padding: 10px"><br> <b>Select
							type of analysis </b> <br /> <span> <input type="radio"
							id="weighted" name="analysisType" value="weighted"
							onclick="display('weighted');" /> <b>Weighted
								Enrichment Analysis </b>
					</span> <span> <input type="radio" id="plain" name="analysisType"
							value="plain" checked="checked" onclick="display('plain');" /> <b>Plain Enrichment
								Analysis </b> <br />
					</span> <br> <span id="weightInfo"
						style="display: none; padding: 2px"> This method accepts a
							list of CHEBI Ids and their weights. <br> Weight is a
							decimal value between 0 and 1.<br> This analysis runs on the
							ChEBI structure ontology.

					</span> <span id="plainInfo" style="display: none; padding: 2px">
							This method accepts a list of CHEBI Ids only.<br /> <br> <b>Select
								target of enrichment</b> <select name="targetType">
								<option value="structure" selected="selected">ChEBI
									structure classification</option>
								<option value="role">ChEBI role classification</option>
								<option value="both">ChEBI structures and roles</option>
						</select>
					</span><br> <input type="submit" value="Submit"
						onclick="document.getElementById('loading').style.display = 'block';" />
					</td>
				</tr>
			</table>
		</form>

		<br> <br> <i>Test data (For reference)</i> <br>

		<table>
			<tr>
				<td>
					<table>
						<!-- Weighted table -->
						<tr>
							<td>CHEBI:17079</td>
							<td>0.7665</td>
						</tr>
						<tr>
							<td>CHEBI:46816</td>
							<td>0.7465</td>
						</tr>
						<tr>
							<td>CHEBI:28658</td>
							<td>0.7465</td>
						</tr>
						<tr>
							<td>CHEBI:28611</td>
							<td>0.7465</td>
						</tr>
						<tr>
							<td>CHEBI:28594</td>
							<td>0.6915</td>
						</tr>
						<tr>
							<td>CHEBI:17048</td>
							<td>0.6915</td>
						</tr>
						<tr>
							<td>CHEBI:7852</td>
							<td>0.60575</td>
						</tr>
						<tr>
							<td>CHEBI:3540</td>
							<td>0.509</td>
						</tr>
					</table>
				</td>
				<td>
					<table>
						<!-- Plain table -->
						<tr>
							<td>CHEBI:17079</td>
						</tr>
						<tr>
							<td>CHEBI:46816</td>
						</tr>
						<tr>
							<td>CHEBI:28658</td>
						</tr>
						<tr>
							<td>CHEBI:28611</td>
						</tr>
						<tr>
							<td>CHEBI:28594</td>
						</tr>
						<tr>
							<td>CHEBI:17048</td>
						</tr>
						<tr>
							<td>CHEBI:7852</td>
						</tr>
						<tr>
							<td>CHEBI:3540</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>

		<script type="text/javascript">
			document.getElementById("loading").style.display = "none";
		</script>

	</center>
</body>
</html>
