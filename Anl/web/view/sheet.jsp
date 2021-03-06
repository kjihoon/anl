<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<style>
#sheetsection{
	height: 700px;
}
 #spreadsheet{
	
	 display: block;   
	 border: none;   
	 height: 100%;  
	 width: 100%;

} 
</style>
<div class="page-wrapper">
	<div class="row page-titles">
		<div class="col-md-5 align-self-center">
			<h3 class="text-primary">Data Handling</h3>
		</div>
		<div class="col-md-7 align-self-center">
			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="javascript:void(0)">Home</a></li>
				<li class="breadcrumb-item active">Data Sheet</li>
			</ol>
		</div>
	</div>
	<!-- End Bread crumb -->
	<div class="container-fluid">
		<div class="card" id="sheetsection">
			 <iframe src="/Anl/view/spreadsheet.jsp" id="spreadsheet"></iframe>	
		</div>
	</div>
</div>
