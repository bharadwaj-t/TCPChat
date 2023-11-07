package dev.lifeofcode.chat;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TcpChatApplication implements CommandLineRunner {
	private final ChatServerVerticle chatServerVerticle;

	public TcpChatApplication(ChatServerVerticle chatServerVerticle) {
		this.chatServerVerticle = chatServerVerticle;
	}

	public static void main(String[] args) {
		SpringApplication.run(TcpChatApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var vertx = Vertx.vertx();
		vertx.deployVerticle(chatServerVerticle).onFailure(failure -> {
			log.error("Failed to deploy ChatServerVerticle verticle.", failure);
		});
	}
}
