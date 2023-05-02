package com.example.demo.servlet;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.eclipse.microprofile.reactive.messaging.Channel;
//import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@WebServlet("/kafka/produce")
public class KafkaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    //@Inject
    //@Channel("prices")
    //Emitter<Integer> emitter;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain");
        String priceStr = request.getParameter("price");
        if (priceStr != null && priceStr.length() > 0) {
            Integer price = Integer.parseInt(priceStr);
            //emitter.send(Message.of(price));
            response.getWriter().println("Emitted new message " + price + " to Kafka @ " + java.time.Instant.now());
        } else {
            response.getWriter().println("ERROR: The query parameter 'price' is required");
        }
	}
}
