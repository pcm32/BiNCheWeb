<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>BiNChe -- Enrichment analysis using ChEBI</title>
<script type="text/javascript">
	function display(value) {
		var val = document.getElementById(value).value;
		if (val == "show") {
			document.getElementById('target').style.display = 'block';
			document.getElementById('info').style.display = 'none';
		} else if (val == "hide") {
			document.getElementById('target').style.display = 'none';
			document.getElementById('info').style.display = 'block';
		}
	}
</script>
</head>

<body>
	<center>
		<h1>Enrichment Analysis</h1>
		<br /> Please enter your list of CHEBI ids, separated by space or
		newline. <br> <br> <br> <b> Chebi Ids </b> <br>

		<form action="${ pageContext.request.contextPath }/ValidateInput"
			method="post">
			<textarea rows="10" cols="50" name="listOfIds"> </textarea>

			<br> <br> <br> Select type of analysis <br /> <br />

			<input type="radio" id="enriched" name="type" value="hide"
				onclick="display('enriched');" /> Weighted Enrichment Analysis <br />
			<br /> <input type="radio" id="plain" name="type" value="show"
				onclick="display('plain');" /> Enrichment Analysis <br /> <br />

			<table>
				<tr>
					<td style="height: 25px"><span id="info" style="display: none">
							This method accepts a list of CHEBI Ids and their weights. <br>
							Weight is a decimal value between 0 and 1.<br> This analysis
							runs on the ChEBI structure ontology.
					</span></td>
				</tr>
			</table>
			<table>
				<tr>
					<td style="height: 25px"><span id="target"
						style="display: none"> This method accepts a list of CHEBI
							Ids only. <br> <b>Select target of enrichment</b>
							<select name="type">
								<option>ChEBI structure classification</option>
								<option selected="selected">ChEBI role classification</option>
								<option>ChEBI structures and roles</option>
						</select>
					</span></td>
				</tr>
			</table>
			<br> <input type="submit" value="Submit" />

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
	</center>
</body>
</html>
