<%@ page import="lk.jiat.techmart.dto.OrderDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    OrderDTO order = (OrderDTO) request.getAttribute("order");
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
    <title>Order Confirmed | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="page-bg">
<header class="topbar">
    <div class="topbar-inner"><a class="brand brand-image" href="products"><span class="brand-logo-shell"><img
            class="brand-wordmark" src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-dark.png"
            alt="TechMart Online"></span></a>
        <nav class="nav"><a href="products">Shop</a><a href="cart">Cart</a><a class="active" href="my-orders">Orders</a><a
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
        <div><span class="kicker">Thank you</span>
            <h1>Order confirmed.</h1>
            <p>Your order has been placed successfully.</p>
            <div class="hero-actions"><a class="btn" href="my-orders">View orders</a><a class="btn ghost"
                                                                                        href="products">Continue
                shopping</a></div>
        </div>
        <div class="count-tile"><span>Order ID</span><strong>#<%= order == null ? "--" : order.getId() %>
        </strong></div>
    </section>
    <% if (order != null) { %>
    <div class="order-card">
        <div class="panel"><span class="card-label">Customer</span>
            <div class="metric-value"><%= order.getUsername() %>
            </div>
        </div>
        <div class="panel"><span class="card-label">Total</span>
            <div class="metric-value">Rs. <%= String.format("%,.2f", order.getTotalAmount()) %>
            </div>
        </div>
        <div class="panel"><span class="card-label">Status</span>
            <div class="metric-value"><span class="status <%= order.getStatus() %>"><%= order.getStatus() %></span>
            </div>
        </div>
    </div>
    <% } else { %>
    <div class="empty-state"><h2>Order details unavailable</h2>
        <p class="footer-note">You can still view your order history from the Orders page.</p>
        <div class="hero-actions"><a class="btn" href="my-orders">Open orders</a></div>
    </div>
    <% } %>
</main>
<script>setTimeout(function () {
    var stack = document.querySelector('.toast-stack');
    if (stack) stack.remove();
}, 4000);</script>
</body>
</html>
