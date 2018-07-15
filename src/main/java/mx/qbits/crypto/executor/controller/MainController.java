package mx.qbits.crypto.executor.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class MainController extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(MainController.class);
    private int port = 6060;
    
    public void start(Future<Void> fut) {
        long start = System.currentTimeMillis();
        logger.info("Inicializando Vertical de Login en puerto: " + port);
        Router router = Router.router(vertx);
        
        // Este bloque de código es totalmente necesario para evitar el error de CORS ******
            Set<String> allowedHeaders = new HashSet<>();
            allowedHeaders.add("x-requested-with");
            allowedHeaders.add("Access-Control-Allow-Origin");
            allowedHeaders.add("origin");
            allowedHeaders.add("Content-Type");
            allowedHeaders.add("accept");
            allowedHeaders.add("X-PINGARUNER");
    
            Set<HttpMethod> allowedMethods = new HashSet<>();
            allowedMethods.add(HttpMethod.GET);
            allowedMethods.add(HttpMethod.POST);
            allowedMethods.add(HttpMethod.OPTIONS);
            allowedMethods.add(HttpMethod.DELETE);
            allowedMethods.add(HttpMethod.PATCH);
            allowedMethods.add(HttpMethod.PUT);
    
            router.route().handler(CorsHandler
                    .create("*")
                    .allowedHeaders(allowedHeaders)
                    .allowedMethods(allowedMethods));
        // Aqui concluye el código necesrio para evitar el error de CORS ******
        
        router.route().handler(BodyHandler.create());          
        
        router.post("/crypto-trader/bitcoin/coloca").handler(this::doit);
        router.post("/crypto-trader/bitcoin/elimina").handler(this::doit);
        
        vertx.createHttpServer().requestHandler(router::accept).listen( 
                config().getInteger("http.port", port), result -> {
                    logger.info("Hola ---> "+port);
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });        
        //pba = System.getenv("PBA");
        long end = System.currentTimeMillis() - start;
        logger.info("Vertical de Executor iniciada en " + end + " milisegundos.");
    }  
    
    private void doit(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        String decoded = routingContext.getBodyAsString();
        String jsonResponse = procesa(decoded);
        response.
        setStatusCode(200)
        .putHeader("content-type", "application/json; charset=utf-8")
        .putHeader("content-type", "application/json")
        .putHeader("Access-Control-Allow-Origin", "*")
        .putHeader("Access-Control-Allow-Methods","GET, POST, OPTIONS")
        .putHeader("Access-Control-Allow-Credentials", "true")
        .end(jsonResponse);
    }
    
    private String procesa(String decoded) {
        Operacion info = Json.decodeValue(decoded, Operacion.class);
        System.out.println(info.getAccion());
        System.out.println(info.getValor());
        System.out.println(info.getCantidad());
        System.out.println(info.getSender());
        return decoded;
    }

}
