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
    <title>Checkout | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="page-bg">
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand brand-image" href="products"><span class="brand-logo-shell"><img class="brand-wordmark"
                                                                                         src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-dark.png"
                                                                                         alt="TechMart Online"></span></a>
        <nav class="nav"><a href="products">Shop</a><a class="active" href="cart">Cart</a><a href="my-orders">Orders</a><a
                class="logout-link" href="logout">Logout</a></nav>
    </div>
</header>
<% if (flashMessage != null) { %>
<div class="toast-stack">
    <div class="toast <%= flashType == null ? "info" : flashType %>"><span><%= flashMessage %></span>
        <button type="button" onclick="this.parentElement.parentElement.remove()">×</button>
    </div>
</div>
<% } %>
<main class="customer-shell section">
    <section class="simple-hero">
        <div><span class="kicker">Checkout</span>
            <h1>Confirm your order.</h1>
            <p>Complete the checkout to receive your order number.</p></div>
    </section>
    <% if (request.getAttribute("error") != null) { %><p class="error"><%= request.getAttribute("error") %>
</p><% } %>
    <div class="checkout-grid">
        <section class="checkout-card"><span class="kicker">Summary</span>
            <h2>Almost done</h2>
            <p>Make sure your cart is correct before placing the order.</p>
            <div class="hero-actions"><a class="btn ghost" href="cart">Review cart</a></div>
        </section>
        <section class="checkout-card"><span class="kicker">Place order</span>
            <h2>Ready to checkout?</h2>
            <p>Your order will be added to your account history after confirmation.</p>
            <form method="post" action="checkout" class="hero-actions">
                <button class="btn" type="submit">Place order</button>
            </form>
        </section>
    </div>
</main>
<script>setTimeout(function () {
    var stack = document.querySelector('.toast-stack');
    if (stack) stack.remove();
}, 4000);</script>
</body>
</html>
