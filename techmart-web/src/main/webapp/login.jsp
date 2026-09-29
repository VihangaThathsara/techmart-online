<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String flashMessage = (String) session.getAttribute("flashMessage");
    String flashType = (String) session.getAttribute("flashType");
    if (flashMessage != null) {
        session.removeAttribute("flashMessage");
        session.removeAttribute("flashType");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign in | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="login-screen">
<% if (flashMessage != null) { %>
<div class="toast-stack">
    <div class="toast <%= flashType == null ? "info" : flashType %>">
        <span><%= flashMessage %></span>
        <button type="button" onclick="this.parentElement.parentElement.remove()">×</button>
    </div>
</div>
<% } %>
<div class="login-shell login-shell-refined">
    <section class="login-showcase login-showcase-copy">
        <div class="login-brand-panel">
            <div class="brand-logo-shell brand-logo-shell-wide">
                <img class="brand-wordmark"
                     src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-light.png"
                     alt="TechMart Online">
            </div>
        </div>
        <div class="login-copy login-copy-wide">
            <span class="kicker">Member sign in</span>
            <h1>Shop smarter.<br>Manage faster.</h1>
            <p>Access the TechMart workspace with a cleaner customer experience, polished admin tools, and reliable
                order management.</p>
        </div>
    </section>

    <section class="login-form">
        <span class="kicker">Welcome back</span>
        <h2>Sign in to continue</h2>
        <p class="subtle">Use your TechMart account credentials to access the platform.</p>
        <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %>
        </p>
        <% } %>
        <form class="form" method="post" action="${pageContext.request.contextPath}/login">
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <button class="btn" type="submit">Sign in</button>
        </form>
    </section>
</div>
<script>
    setTimeout(function () {
        var stack = document.querySelector('.toast-stack');
        if (stack) stack.remove();
    }, 4000);
</script>
</body>
</html>
