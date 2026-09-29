<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.techmart.dto.ProductDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<ProductDTO> products = (List<ProductDTO>) request.getAttribute("products");
    String q = request.getParameter("q");
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
    <title>Shop | TechMart</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="page-bg">
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand brand-image" href="products">
            <span class="brand-logo-shell">
                <img class="brand-wordmark"
                     src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-dark.png"
                     alt="TechMart Online">
            </span>
        </a>
        <nav class="nav">
            <a class="active" href="products">Shop</a>
            <a href="cart">Cart</a>
            <a href="my-orders">Orders</a>
            <a class="logout-link" href="logout">Logout</a>
        </nav>
    </div>
</header>
<% if (flashMessage != null) { %>
<div class="toast-stack">
    <div class="toast <%= flashType == null ? "info" : flashType %>">
        <span><%= flashMessage %></span>
        <button type="button" onclick="this.parentElement.parentElement.remove()">×</button>
    </div>
</div>
<% } %>
<main class="customer-shell">
    <section class="store-hero store-hero-single">
        <div class="store-hero-card hero-wide">
            <div>
                <span class="kicker">Latest collection</span>
                <h1>Find the tech that fits your day.</h1>
                <p>Browse selected electronics, add your favorites to the cart, and place orders in a cleaner shopping
                    experience with a consistent TechMart interface.</p>
            </div>
            <div class="hero-bottom-row">
                <div class="hero-actions">
                    <a class="btn" href="#catalog">Explore products</a>
                    <a class="btn ghost" href="cart">View cart</a>
                </div>
            </div>
        </div>
    </section>

    <section class="section" id="catalog">
        <div class="catalog-head">
            <div>
                <span class="kicker">Shop</span>
                <h2>Product catalog</h2>
                <p>Search and select products for your order.</p>
            </div>
            <form class="search-box" method="get" action="products">
                <input type="text" name="q" value="<%= q == null ? "" : q %>" placeholder="Search products">
                <button class="btn" type="submit">Search</button>
            </form>
        </div>

        <% if (products == null || products.isEmpty()) { %>
        <div class="empty-state">
            <h2>No products found</h2>
            <p class="footer-note">Try another search term or view the full catalog.</p>
            <div class="hero-actions"><a class="btn" href="products">Reset search</a></div>
        </div>
        <% } else { %>
        <div class="product-grid">
            <% for (ProductDTO p : products) {
                String img = p.getImageUrl();
                String src = (img != null && (img.startsWith("http://") || img.startsWith("https://"))) ? img : request.getContextPath() + "/assets/products/" + img;
            %>
            <article class="product-card">
                <div class="product-media">
                    <img class="product-image" src="<%= src %>" onerror="this.onerror=null;this.src='<%= fallback %>';"
                         alt="<%= p.getName() %>">
                </div>
                <div class="product-body">
                    <div class="product-name"><%= p.getName() %>
                    </div>
                    <p class="product-description"><%= p.getDescription() %>
                    </p>
                    <div class="product-meta">
                        <div class="price">Rs. <%= String.format("%,.2f", p.getPrice()) %>
                        </div>
                        <span class="chip stock <%= p.getAvailableQuantity() <= 5 ? "low" : "ok" %>"><%= p.getAvailableQuantity() %> left</span>
                    </div>
                    <div class="product-actions">
                        <a class="btn" href="product-details?id=<%= p.getId() %>">View</a>
                        <a class="btn ghost" href="cart?action=add&productId=<%= p.getId() %>&quantity=1">Add</a>
                    </div>
                </div>
            </article>
            <% } %>
        </div>
        <% } %>
    </section>
</main>
<script>
    setTimeout(function () {
        var stack = document.querySelector('.toast-stack');
        if (stack) stack.remove();
    }, 4000);
</script>
</body>
</html>
