package br.com.paulasantana.ecommerce;

import com.paulasantana.kafka.producer.KafkaDispatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class GenerateAllReportsServlet extends HttpServlet {

    private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        batchDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT" );

            System.out.println("Send Genarated report to all users");

            resp.getWriter().println("Reporter Requests generated");
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
