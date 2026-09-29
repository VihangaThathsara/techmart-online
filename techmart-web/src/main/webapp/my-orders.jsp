<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.techmart.dto.OrderDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<OrderDTO> orders = (List<OrderDTO>) request.getAttribute("orders");
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
    <title>My Orders | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="page-bg">
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand brand-image" href="products"><span class="brand-logo-shell"><img class="brand-wordmark"
                                                                                         src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-dark.png"
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
        <div><span class="kicker">Orders</span>
            <h1>Your purchases.</h1>
            <p>Check the latest order totals and status from your account.</p></div>
        <div class="count-tile"><span>Total</span><strong><%= orders == null ? 0 : orders.size() %>
        </strong></div>
    </section>
    <% if (orders == null || orders.isEmpty()) { %>
    <div class="empty-state"><h2>No orders yet</h2>
        <p class="footer-note">Place an order and it will appear here.</p>
        <div class="hero-actions"><a class="btn" href="products">Shop now</a></div>
    </div>
    <% } else { %>
    <div class="table-card">
        <div class="table-scroll">
            <table>
                <tr>
                    <th>Order</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Created</th>
                </tr>
                <% for (OrderDTO order : orders) { %>
                <tr>
                    <td><b>#<%= order.getId() %>
                    </b></td>
                    <td>Rs. <%= String.format("%,.2f", order.getTotalAmount()) %>
                    </td>
                    <td><span class="status <%= order.getStatus() %>"><%= order.getStatus() %></span></td>
                    <td><%= order.getCreatedAt() %>
                    </td>
                </tr>
                <% } %></table>
        </div>
    </div>
    <% } %>
</main>
<script>setTimeout(function () {
    var stack = document.querySelector('.toast-stack');
    if (stack) stack.remove();
}, 4000);</script>
</body>
</html>
