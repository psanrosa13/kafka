package com.paulasantana;

import com.paulasantana.common.CorreleationId;
import com.paulasantana.producer.KafkaDispatcher;

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
      var email = req.getParameter("email");

      var order = new Order(orderId, amount, email);

      orderDispatcher.send("ORDER_NEW", email, order, new CorreleationId(NewOrderServlet.class.getSimpleName()));

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

