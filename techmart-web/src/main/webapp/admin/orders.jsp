<%@ page import="java.util.List" %>
<%@ page import="lk.jiat.techmart.dto.OrderDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<OrderDTO> orders = (List<OrderDTO>) request.getAttribute("orders");
%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Orders | TechMart Admin</title>
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
        <nav class="admin-menu"><a href="dashboard">Dashboard</a><a href="inventory">Inventory</a><a class="active"
                                                                                                     href="orders">Orders</a><a
                href="metrics">Reports</a><a href="${pageContext.request.contextPath}/logout">Logout</a></nav>
    </aside>
    <main class="admin-main">
        <div class="admin-top">
            <div><span class="kicker">Orders</span>
                <h1>Order desk</h1>
                <p>Review customer orders and current status.</p></div>
        </div>
        <div class="admin-grid">
            <div class="admin-card"><span class="card-label">Orders</span>
                <div class="metric-value"><%= orders == null ? 0 : orders.size() %>
                </div>
            </div>
            <div class="admin-card"><span class="card-label">Status</span>
                <div class="metric-value">Active</div>
            </div>
        </div>
        <section class="admin-section"><% if (orders == null || orders.isEmpty()) { %>
            <div class="empty-state"><h2>No orders yet</h2>
                <p class="footer-note">New customer orders will appear here.</p></div>
            <% } else { %>
            <div class="table-card">
                <div class="table-scroll">
                    <table>
                        <tr>
                            <th>Order</th>
                            <th>User</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Created</th>
                        </tr>
                        <% for (OrderDTO o : orders) { %>
                        <tr>
                            <td><b>#<%= o.getId() %>
                            </b></td>
                            <td><%= o.getUsername() %>
                            </td>
                            <td>Rs. <%= String.format("%,.2f", o.getTotalAmount()) %>
                            </td>
                            <td><span class="status <%= o.getStatus() %>"><%= o.getStatus() %></span></td>
                            <td><%= o.getCreatedAt() %>
                            </td>
                        </tr>
                        <% } %></table>
                </div>
            </div>
            <% } %></section>
    </main>
</div>
</body>
</html>
