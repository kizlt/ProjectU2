<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Дорога домой — ошибка</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/quest.css">
</head>
<body>
<main class="page">
    <h1 class="title">Дорога домой</h1>

    <c:set var="code" value="${requestScope['javax.servlet.error.status_code']}"/>
    <div class="scene-text"><c:choose>
        <c:when test="${code == 404}">Такой страницы нет. Похоже, в адресе опечатка.</c:when>
        <c:otherwise>Что-то сломалось по дороге.</c:otherwise>
    </c:choose>

    Код ошибки: <c:out value="${code}"/>

    Начать заново можно прямо отсюда.</div>

    <a class="button" href="${pageContext.request.contextPath}/quest?new">Начать заново</a>
</main>
</body>
</html>
