<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Insert title here</title>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
 
 	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
  	<style>
		body {
			margin: 0 auto;
		}
		main {
			width: 1200px;
			height: 600px;
			margin: 30px auto;
			display: flex;
			justify-content: space-evenly;
			align-items: center;
			flex-direction: column;
		}
  		#loginForm>ul>li, #link>ul>li{
  			list-style-type: none;
  		}
  		#logo{
			width: 300px;
  		}
		#userpwd, #userid{
			width: 400px;
			height: 40px;
			line-height: 40px;
			margin: 15px auto;
		}
		.submitbtn{
			background-color: #FFB300;
			border: none;
			color: white;
		}
		#login{
			margin: 20px auto;
			width: 400px;
			height: 40px;
			line-height: 40px;
		}
		#loginForm a{
			text-decoration: none;
			color: black;
			border-right: 2px solid gray;
			padding: 0 25px;
			text-align: center;
		}
		#loginForm a:last-child{
			border: none;
		}
		#loginForm li:last-child{
			margin: 20px auto;
		}
  	</style>
</head>
<body style="width: 1200px; margin: 0 auto;">
	<jsp:include page="${pageContext.servletContext.contextPath}/resources/header.jsp" />
	<main>
		<h1>로그인</h1>
		<form method="post" action="/loginOk" id="loginForm">
			<ul>
				<li><input type="text" name="userid" id="userid" placeholder="아이디"></li>
				<li><input type="password" name="userpwd" id="userpwd" placeholder="비밀번호"></li>
				<li><input type="submit" name="login" id="login" value="로그인" class="submitbtn"/></li>
				<li>
					<a href="/create_membership">회원가입</a>
					<a href="login/find_id">아이디찾기</a>
					<a href="login/find_pwd">비밀번호찾기</a>
				</li>
			</ul>
		</form>
	</main>
	<jsp:include page="${pageContext.servletContext.contextPath}/resources/footer.jsp" />
</body>
</html>