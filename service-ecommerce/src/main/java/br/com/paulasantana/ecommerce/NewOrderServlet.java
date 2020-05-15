package br.com.paulasantana.ecommerce;

import com.paulasantana.kafka.producer.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var orderId = UUID.randomUUID().toString();
            var amount = new BigDecimal(req.getParameter("amount"));

            var email = new Email(req.getParameter("email"), "Welcome ! We are processing your order");
            var order = new Order(orderId, amount, email.getSubject());

            orderDispatcher.send("ORDER_NEW", email.getSubject(), order);

            emailDispatcher.send("ORDER_EMAIL", email.getSubject(), email);

            System.out.println("New Order sent sucessfully");

            resp.getWriter().println("New Order sent");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

