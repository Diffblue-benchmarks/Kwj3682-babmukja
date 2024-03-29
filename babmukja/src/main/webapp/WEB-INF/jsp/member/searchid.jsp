<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="X-UA-Compatible" content="ie=edge" />
<title>babmukja</title>
<link rel="stylesheet"
	href="<c:url value="/resources/css/member/style.css"/>" />
<link rel="stylesheet"
	href="<c:url value="https://use.fontawesome.com/releases/v5.8.1/css/all.css"/>"
	integrity="sha384-50oBUHEmvpQ+1lW4y57PTFmhCaXp0ML5d60M1M7uH2+nqUivzIebhndOJK28anvf"
	crossorigin="anonymous" />
<link
	href="<c:url value="https://fonts.googleapis.com/css?family=Jua"/>"
	rel="stylesheet" />
<script src="<c:url value="/resources/js/jquery-3.2.1.min.js"/>"></script>
</head>
<body>
	<main>
	<div class="search_id_container">
		<div class="search_id_title">
			아이디 찾기 <i class="fas fa-utensils"></i>
		</div>

		<form id="search-form" method="post">
			<div class="search_id_input" id="email-form">
				<div class="id_name">
					<input type="text" name="memName" id="memName" placeholder="이름" />
				</div>
				<div class="phone_email">
					<input type="text" name="memPhone" id="memPhone" placeholder="전화번호" />
				</div>
			</div>
		</form>

		<div class="search_id_button">
			<button type="button">아이디 찾기</button>
		</div>


		<div class="search_id">
			<span class="search_id_result"></span>
		</div>
	</div>
	</main>

	<script>
		$(".search_id_button").click(function() {
			const memName = $("#memName").val();
			const memPhone = $("#memPhone").val();
			
			console.log(memName);
			console.log(memPhone);
			
			const searchData = {memName,memPhone};
			
			$.ajax({
				type: 'POST',
				data: searchData,
				url: "findid.do"
			}).done(function(data){
				console.log(data);
				console.log(data == "");
				console.log(data === undefined);
				console.log(data === null);
				if(data == "") {
					$(".search_id .search_id_result").text("이메일과 비밀번호가 일치하지 않습니다.");
					$(".search_id .search_id_result").attr("style", "color:red;");
				} else {
					$(".search_id .search_id_result").text("회원님의 아이디는" + '"' + data + '"' + "입니다.");
					$(".search_id .search_id_result").attr("style", "color: blue;");
				}
			})
			.fail(e => console.log(e));

			if (memName.length == 0) {
				alert("이름을 입력해 주세요.");
				$("#memName").focus();
				return false;
			}
			if (memPhone.length == 0) {
				alert("전화번호를 입력해 주세요.");
				$("#memPhone").focus();
				return false;
			}
			if (cellPhone() == false) {
				alert("'-'을 사용해서 전화번호를 입력해주세요.");
				$("#memPhone").focus();
				return false;
			}

			function cellPhone() {
				const phonenum = $("#memPhone").val();
				const regPhone = /(01[0|1|6|9|7])[-](\d{3}|\d{4})[-](\d{4}$)/g;
				if (!regPhone.test(phonenum)) {
					$("#memPhone").focus();
					return false;
				}
				return true;
			}
		});
	</script>
</body>
</html>
