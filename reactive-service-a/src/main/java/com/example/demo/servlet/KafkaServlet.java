package com.example.demo.servlet;

import java.io.IOException;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@WebServlet("/kafka/produce")
public class KafkaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Inject
    @Channel("prices2")
    Emitter<Integer> emitter;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain");
        String priceStr = request.getParameter("price");
        if (priceStr != null && priceStr.length() > 0) {
            Integer price = Integer.parseInt(priceStr);
            emitter.send(Message.of(price));
            response.getWriter().println("Emitted new message " + price + " to Kafka @ " + java.time.Instant.now());
        } else {
            response.getWriter().println("ERROR: The query parameter 'price' is required");
        }
	}
}
