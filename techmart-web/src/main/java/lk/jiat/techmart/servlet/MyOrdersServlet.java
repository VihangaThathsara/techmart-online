package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.techmart.remote.OrderRemote;
import lk.jiat.techmart.web.util.LookupUtil;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/my-orders")
public class MyOrdersServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = (String) req.getSession().getAttribute("username");
            OrderRemote orderRemote = LookupUtil.orderRemote();
            req.setAttribute("orders", orderRemote.getOrdersByUser(username));
            req.getRequestDispatcher("/my-orders.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
