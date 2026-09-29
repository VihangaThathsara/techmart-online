<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.techmart.dto.ProductDTO" %>
<%@ page import="lk.jiat.techmart.dto.WarehouseStockDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<ProductDTO> products = (List<ProductDTO>) request.getAttribute("products");
    List<ProductDTO> lowStock = (List<ProductDTO>) request.getAttribute("lowStock");
    List<WarehouseStockDTO> warehouseStock = (List<WarehouseStockDTO>) request.getAttribute("warehouseStock");
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
    <title>Inventory | TechMart Admin</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/favicon-techmart.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body class="admin-bg">
<div class="admin-layout">
    <aside class="admin-sidebar">
        <div class="admin-brand-block"><span class="admin-brand-shell"><img class="admin-brand-logo"
                                                                            src="${pageContext.request.contextPath}/assets/logo-techmart-transparent-light.png"
                                                                            alt="TechMart Online"></span><span
                class="admin-panel-label">Admin Panel</span></div>
        <nav class="admin-menu"><a href="dashboard">Dashboard</a><a class="active" href="inventory">Inventory</a><a
                href="orders">Orders</a><a href="metrics">Reports</a><a
                href="${pageContext.request.contextPath}/logout">Logout</a></nav>
    </aside>
    <main class="admin-main">
        <% if (flashMessage != null) { %>
        <div class="toast-stack toast-stack-admin">
            <div class="toast <%= flashType == null ? "info" : flashType %>"><span><%= flashMessage %></span>
                <button type="button" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
        </div>
        <% } %>
        <div class="admin-top">
            <div><span class="kicker">Inventory</span>
                <h1>Stock control</h1>
                <p>Review product availability and warehouse quantities.</p></div>
            <a class="btn" href="inventory?action=sync">Refresh stock</a></div>
        <div class="admin-grid">
            <div class="admin-card"><span class="card-label">Products</span>
                <div class="metric-value"><%= products == null ? 0 : products.size() %>
                </div>
            </div>
            <div class="admin-card"><span class="card-label">Needs attention</span>
                <div class="metric-value"><%= lowStock == null ? 0 : lowStock.size() %>
                </div>
            </div>
            <div class="admin-card"><span class="card-label">Warehouses</span>
                <div class="metric-value">3</div>
            </div>
            <div class="admin-card"><span class="card-label">Status</span>
                <div class="metric-value">Live</div>
            </div>
        </div>
        <section class="admin-section">
            <div class="admin-section-head">
                <div><span class="kicker">Catalog</span>
                    <h2>Product availability</h2></div>
            </div>
            <div class="table-card">
                <div class="table-scroll">
                    <table>
                        <tr>
                            <th>ID</th>
                            <th>SKU</th>
                            <th>Name</th>
                            <th>Available</th>
                            <th>Price</th>
                            <th>Status</th>
                        </tr>
                        <% if (products != null) {
                            for (ProductDTO p : products) { %>
                        <tr>
                            <td><%= p.getId() %>
                            </td>
                            <td><b><%= p.getSku() %>
                            </b></td>
                            <td><%= p.getName() %>
                            </td>
                            <td><span
                                    class="chip stock <%= p.getAvailableQuantity() <= 5 ? "low" : "ok" %>"><%= p.getAvailableQuantity() %></span>
                            </td>
                            <td>Rs. <%= String.format("%,.2f", p.getPrice()) %>
                            </td>
                            <td><span
                                    class="status <%= p.getAvailableQuantity() <= 5 ? "pending" : "OK" %>"><%= p.getAvailableQuantity() <= 5 ? "LOW" : "OK" %></span>
                            </td>
                        </tr>
                        <% }
                        } %></table>
                </div>
            </div>
        </section>
        <section class="admin-section">
            <div class="admin-section-head">
                <div><span class="kicker">Locations</span>
                    <h2>Warehouse breakdown</h2></div>
            </div>
            <div class="table-card">
                <div class="table-scroll">
                    <table>
                        <tr>
                            <th>Product</th>
                            <th>Warehouse</th>
                            <th>Code</th>
                            <th>Available</th>
                            <th>Reserved</th>
                            <th>Total</th>
                        </tr>
                        <% if (warehouseStock != null) {
                            for (WarehouseStockDTO s : warehouseStock) { %>
                        <tr>
                            <td><b><%= s.getProductName() %>
                            </b></td>
                            <td><%= s.getWarehouseName() %>
                            </td>
                            <td><span class="chip soft"><%= s.getWarehouseCode() %></span></td>
                            <td><span
                                    class="chip stock <%= s.getAvailableQuantity() <= 2 ? "low" : "ok" %>"><%= s.getAvailableQuantity() %></span>
                            </td>
                            <td><%= s.getReservedQuantity() %>
                            </td>
                            <td><b><%= s.getTotalQuantity() %>
                            </b></td>
                        </tr>
                        <% }
                        } %></table>
                </div>
            </div>
        </section>
    </main>
</div>
<script>setTimeout(function () {
    var stack = document.querySelector('.toast-stack');
    if (stack) stack.remove();
}, 4000);</script>
</body>
</html>
