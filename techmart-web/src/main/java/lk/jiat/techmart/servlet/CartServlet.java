package lk.jiat.techmart.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.jiat.techmart.dto.ProductDTO;
import lk.jiat.techmart.remote.CartRemote;
import lk.jiat.techmart.remote.ProductRemote;
import lk.jiat.techmart.web.util.FlashMessageUtil;
import lk.jiat.techmart.web.util.LookupUtil;
import lk.jiat.techmart.web.util.ProductImageUtil;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession();
            CartRemote cartRemote = getCartRemote(session);
            String action = req.getParameter("action");

            if ("add".equals(action)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                ProductRemote productRemote = LookupUtil.productRemote();
                ProductDTO product = productRemote.getProduct(productId);
                ProductImageUtil.applyLocalImage(product);
                cartRemote.addItem(product, quantity);
                FlashMessageUtil.success(session, product.getName() + " added to cart.");
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }

            if ("remove".equals(action)) {
                Long productId = Long.parseLong(req.getParameter("productId"));
                cartRemote.removeItem(productId);
                FlashMessageUtil.info(session, "Item removed from cart.");
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }

            req.setAttribute("items", cartRemote.getItems());
            req.setAttribute("total", cartRemote.getTotal());
            req.getRequestDispatcher("/cart.jsp").forward(req, resp);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    private CartRemote getCartRemote(HttpSession session) throws NamingException {
        CartRemote cartRemote;
        if (session.getAttribute("cartRemote") == null) {
            cartRemote = LookupUtil.cartRemote();
            session.setAttribute("cartRemote", cartRemote);
        } else {
            cartRemote = (CartRemote) session.getAttribute("cartRemote");
        }
        return cartRemote;
    }
}
