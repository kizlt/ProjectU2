<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Дорога домой</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quest.css">
</head>
<body>
<main class="page">
    <h1 class="title">Дорога домой</h1>

    <div class="scene-text"><c:out value="${sceneText}"/></div>

    <form class="choices" method="post" action="${pageContext.request.contextPath}/quest"
          onsubmit="if (this.dataset.sent) { return false; } this.dataset.sent = '1';">
        <input type="hidden" name="tab" value="<c:out value='${tabId}'/>">
        <input type="hidden" name="scene" value="<c:out value='${sceneId}'/>">
        <c:forEach items="${choices}" var="choice" varStatus="loop">
            <button class="button" type="submit" name="choice" value="${loop.index}"><c:out value="${choice.label}"/></button>
        </c:forEach>
    </form>
</main>
</body>
</html>
