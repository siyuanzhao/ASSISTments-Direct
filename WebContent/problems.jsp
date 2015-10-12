<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
  <%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Problems</title>
<style type="text/css">
hr {
	margin: 5px;
	border-top: 1px solid black !important;
}

</style>
</head>
<body>
	<c:if test="${empty requestScope.count }">
		<c:set var="count" value="${1 }" scope="request"></c:set>
	</c:if>
	<c:forEach items="${requestScope.sections }" var="section">
		<c:choose>
			<c:when test="${section.type.name eq 'ProblemSection'}">
				<c:forEach items="${section.problems }" var="problem"	varStatus="loop">
					<p style="margin-top: 15px;">
					<!-- <input type="checkbox" class="problem" value="${problem.id }">  --><b>Problem ${requestScope.count }</b></p>
					<c:set var="count" value="${requestScope.count + 1 }" scope="request"></c:set>
							${problem.body}
						<!-- display answer fields-->
					<c:choose>
						<c:when test="${problem.type.typeId == 1}">
							<!-- choose 1 -->
							<c:forEach items="${problem.answers }" var="answer">
								<p>
									<input type="radio"> ${answer.value }
								</p>
							</c:forEach>
						</c:when>
						<c:when test="${problem.type.typeId == 2}">
							<!-- choose N -->
							<c:forEach items="${problem.answers }" var="answer">
								<p>
									<input type="checkbox"> ${answer.value }
								</p>
							</c:forEach>
						</c:when>
						<c:when test="${problem.type.typeId == 3}">
							<!-- rank -->
							<c:forEach items="${problem.answers }" var="answer">
								<p>
									<input type="text" style="width: 2em;"> ${answer.value }
								</p>
							</c:forEach>
						</c:when>
						<c:when test="${problem.type.typeId == 8}">
							<!-- open response -->
							<textarea></textarea>
						</c:when>
						<c:otherwise>
							<input type="text">
						</c:otherwise>
					</c:choose>
					<hr style="margin: 30px 0 0 0;">
				</c:forEach>
			</c:when>
			<c:otherwise>
				<!-- call itself to display problems -->
					<c:set var="sections" value="${section.children }" scope="request" />
					<jsp:include page="problems.jsp" />
			</c:otherwise>
		</c:choose>
	</c:forEach>
</body>
</html>