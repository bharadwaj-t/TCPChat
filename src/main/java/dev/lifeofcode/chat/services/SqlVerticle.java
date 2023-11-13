package dev.lifeofcode.chat.services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        String user = config().getString("user");
        String password = config().getString("password");
        String host = config().getString("host");
        String database = config().getString("database");

        var connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost(host)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password)
                .setPipeliningLimit(16);

        var poolOptions = new PoolOptions()
                .setMaxSize(5);
        var client = PgPool.client(vertx, connectOptions, poolOptions);

        var options = new SqlAuthenticationOptions();

        // TODO: is the default query a bug? Verify.
        options.setAuthenticationQuery("SELECT password FROM users WHERE username = $1");
        var authenticationProvider = SqlAuthentication.create(client, options);

        signUp(client, vertx, authenticationProvider);
        authenticate(vertx, authenticationProvider);

        startPromise.complete();
    }

    private void signUp(SqlClient client, Vertx vertx, SqlAuthentication sqlAuth) {
        vertx.eventBus().<JsonObject>consumer("user-signup", dataMsg -> {
            var data = dataMsg.body();
            var user = data.getString("username");
            var password = data.getString("password");
            String hash = sqlAuth.hash(
                    "pbkdf2", // hashing algorithm (OWASP recommended)
                    VertxContextPRNG.current().nextString(32), // secure random salt
                    password // password
            );

            client.preparedQuery("INSERT INTO users (username, password) VALUES ($1, $2)")
                    .execute(Tuple.of(user, hash))
                    .onSuccess(rowset -> {
                        log.info("UPDATED");
                        dataMsg.reply("Successfully signed up!");
                    })
                    .onFailure(err -> {
                        log.error("Failed to sign up", err);
                        dataMsg.reply("Failed to sign-up");
                    });
        });
    }

    private void authenticate(Vertx vertx, AuthenticationProvider sqlAuth) {
        vertx.eventBus().<JsonObject>consumer("user-authentication", dataMsg -> {
            var authInfo = dataMsg.body();
            var creds = new UsernamePasswordCredentials(authInfo.getString("username"), authInfo.getString("password"));
            sqlAuth.authenticate(creds)
                    .onSuccess(user -> {
                        log.info("Authenticated: {}", user.principal());
                        dataMsg.reply("Successfully authenticated.\n");
                    })
                    .onFailure(err -> {
                        log.error("Auth failed", err);
                        dataMsg.reply("Failed to authenticate.\n");
                    });
        });
    }
}
