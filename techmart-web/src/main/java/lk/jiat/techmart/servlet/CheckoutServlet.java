package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.techmart.dto.OrderDTO;
import lk.jiat.techmart.remote.CartRemote;
import lk.jiat.techmart.remote.OrderRemote;
import lk.jiat.techmart.web.util.FlashMessageUtil;
import lk.jiat.techmart.web.util.LookupUtil;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            CartRemote cartRemote = (CartRemote) session.getAttribute("cartRemote");
            if (cartRemote == null || cartRemote.getItems().isEmpty()) {
                FlashMessageUtil.warning(session, "Your cart is empty.");
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            String username = (String) session.getAttribute("username");
            OrderRemote orderRemote = LookupUtil.orderRemote();
            OrderDTO order = orderRemote.placeOrder(username, cartRemote.getItems());
            cartRemote.clear();
            session.removeAttribute("cartRemote");
            session.setAttribute("lastOrder", order);
            FlashMessageUtil.success(session, "Order #" + order.getId() + " placed successfully.");
            resp.sendRedirect(req.getContextPath() + "/order-success");
        } catch (NamingException | RuntimeException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/checkout.jsp").forward(req, resp);
        }
    }
}
