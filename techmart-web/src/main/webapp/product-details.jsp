<%@ page import="lk.jiat.techmart.dto.ProductDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ProductDTO p = (ProductDTO) request.getAttribute("product");
    String fallback = request.getContextPath() + "/assets/products/product-default.png";
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
    <title><%= p == null ? "Product" : p.getName() %> | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="page-bg">
<header class="topbar">
    <div class="topbar-inner"><a class="brand brand-image" href="products"><span class="brand-logo-shell"><img
            class="brand-wordmark" src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-dark.png"
            alt="TechMart Online"></span></a>
        <nav class="nav"><a class="active" href="products">Shop</a><a href="cart">Cart</a><a href="my-orders">Orders</a><a
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
    <% if (p == null) { %>
    <div class="empty-state"><h2>Product not found</h2><a class="btn" href="products">Back to shop</a></div>
    <% } else {
        String img = p.getImageUrl();
        String src = (img != null && (img.startsWith("http://") || img.startsWith("https://"))) ? img : request.getContextPath() + "/assets/products/" + img;
    %>
    <div class="detail-layout">
        <div class="detail-photo"><img src="<%= src %>" onerror="this.onerror=null;this.src='<%= fallback %>';"
                                       alt="<%= p.getName() %>"></div>
        <section class="detail-panel"><span class="kicker"><%= p.getSku() %></span>
            <h1><%= p.getName() %>
            </h1>
            <p><%= p.getDescription() %>
            </p>
            <div class="product-meta"><span
                    class="chip stock <%= p.getAvailableQuantity() <= 5 ? "low" : "ok" %>"><%= p.getAvailableQuantity() %> available</span>
            </div>
            <div class="price">Rs. <%= String.format("%,.2f", p.getPrice()) %>
            </div>
            <form method="get" action="cart" class="buy-box"><input type="hidden" name="action" value="add"><input
                    type="hidden" name="productId" value="<%= p.getId() %>"><label
                    class="card-label">Quantity</label><input type="number" name="quantity" value="1" min="1"
                                                              max="<%= p.getAvailableQuantity() %>" required>
                <div class="hero-actions">
                    <button class="btn" type="submit">Add to cart</button>
                    <a class="btn ghost" href="products">Continue shopping</a></div>
            </form>
        </section>
    </div>
    <% } %>
</main>
<script>setTimeout(function () {
    var stack = document.querySelector('.toast-stack');
    if (stack) stack.remove();
}, 4000);</script>
</body>
</html>
