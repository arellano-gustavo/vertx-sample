package mx.qbits.crypto.executor.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainController extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(MainController.class);
    private static String pba;
    private int port = 6060;
    
    public void start(Future<Void> fut) {
        logger.info("Inicializando Vertical en puerto: " + port);
        Router router = Router.router(vertx);
        router.route("/*").handler(StaticHandler.create("assets"));
        router.route().handler(BodyHandler.create());//.setUploadsDirectory("upload-folder"));
        router.post("/crypto-trader/bitcoin/coloca").handler(this::doit);
        router.post("/crypto-trader/bitcoin/elimina").handler(this::doit);
        
        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer().requestHandler(router::accept).listen( 
                config().getInteger("http.port", port), result -> {
                    logger.info("Hola");
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });        
        pba = System.getenv("PBA");
        logger.info("Vertical iniciada !!!" + pba);
    }  
    private void doit(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();

        String decoded = routingContext.getBodyAsString();
        String jsonResponse = procesa(decoded, request);
        response.
        setStatusCode(200).
        putHeader("content-type", "application/json; charset=utf-8").
        end(jsonResponse);
    }
    
    private String procesa(String decoded, HttpServerRequest request) {
        Operacion info = Json.decodeValue(decoded, Operacion.class);
        System.out.println(info.getAccion());
        System.out.println(info.getValor());
        System.out.println(info.getCantidad());
        return decoded;
    }

}
