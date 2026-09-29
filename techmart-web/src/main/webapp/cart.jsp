<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.techmart.dto.CartItemDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<CartItemDTO> items = (List<CartItemDTO>) request.getAttribute("items");
    Double total = (Double) request.getAttribute("total");
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
    <title>Cart | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="page-bg">
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand brand-image" href="products">
            <span class="brand-logo-shell"><img class="brand-wordmark"
                                                src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-dark.png"
                                                alt="TechMart Online"></span>
        </a>
        <nav class="nav">
            <a href="products">Shop</a>
            <a class="active" href="cart">Cart</a>
            <a href="my-orders">Orders</a>
            <a class="logout-link" href="logout">Logout</a>
        </nav>
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
    <section class="cart-hero">
        <div>
            <span class="kicker">Cart</span>
            <h1>Your selected items.</h1>
            <p>Review quantities and totals before checkout.</p>
        </div>
        <div class="count-tile"><span>Items</span><strong><%= items == null ? 0 : items.size() %>
        </strong></div>
    </section>

    <% if (items == null || items.isEmpty()) { %>
    <div class="empty-state">
        <h2>Your cart is empty</h2>
        <p class="footer-note">Add products from the shop to start your order.</p>
        <div class="hero-actions"><a class="btn" href="products">Shop now</a></div>
    </div>
    <% } else { %>
    <div class="table-card">
        <div class="table-scroll">
            <table>
                <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Qty</th>
                    <th>Line Total</th>
                    <th></th>
                </tr>
                <% for (CartItemDTO item : items) { %>
                <tr>
                    <td><b><%= item.getProductName() %>
                    </b></td>
                    <td>Rs. <%= String.format("%,.2f", item.getUnitPrice()) %>
                    </td>
                    <td><span class="chip"><%= item.getQuantity() %></span></td>
                    <td><b>Rs. <%= String.format("%,.2f", item.getLineTotal()) %>
                    </b></td>
                    <td><a class="btn danger small" href="cart?action=remove&productId=<%= item.getProductId() %>">Remove</a>
                    </td>
                </tr>
                <% } %>
            </table>
        </div>
    </div>
    <div class="summary-panel">
        <div><span
                class="footer-note">Total</span><br><strong>Rs. <%= String.format("%,.2f", total == null ? 0 : total) %>
        </strong></div>
        <div class="hero-actions"><a class="btn ghost" href="products">Add more</a><a class="btn" href="checkout">Checkout</a>
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
