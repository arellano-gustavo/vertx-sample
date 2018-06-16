package mx.unam.dgpe.sample.controller;

import java.util.HashMap;
import java.util.Map;

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

public class MyController extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(MyController.class);
    private static String pba;
    
    public void start(Future<Void> fut) {
        logger.info("Inicializando Vertical");
        Router router = Router.router(vertx);
        //router.route("/*").handler(StaticHandler.create("assets")); 
        //router.route("/*").handler(StaticHandler.create("assets")); // para invocar asi: http://localhost:8080/index.html
        // el directorio "upload-folder" será creado en la misma ubicación que el jar fue ejecutado
        router.route().handler(BodyHandler.create().setUploadsDirectory("upload-folder"));
        router.get("/api/primero").handler(this::primero);
        router.post("/api/segundo").handler(this::segundo);
        router.get("/api/tercero").handler(this::tercero);
        router.get("/api/cuenta").handler(this::cuenta);
        
        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer().requestHandler(router::accept).listen( 
                config().getInteger("http.port", 8080), result -> {
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
    private void primero(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();
        String mode = request.getParam("mode");
        String jsonResponse = procesa(mode, request);
        response.
        setStatusCode(200).
        putHeader("content-type", "application/json; charset=utf-8").
        end(jsonResponse);
    }
    private void cuenta(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();
        String mode = request.getParam("mode");
        String jsonResponse = ok(mode, request);
        response.
        setStatusCode(200).
        putHeader("content-type", "application/json; charset=utf-8").
        end(jsonResponse);
    }    
    private String ok(String mode, HttpServerRequest request) {
        long start = System.currentTimeMillis();
        boolean error = false;
        Integer num = 1;
        try {
            num = Integer.parseInt(mode);
        } catch (Exception e) {
            error=true;
        }
        double res = 1;
        for (int i=1; i<=num; i++)
            res = res + Math.log10(i);
        int resultado = (int)res;
        long time = System.currentTimeMillis()-start;
        
        Map<Object, Object> info = new HashMap<>();
        if(!error) {
            info.put("resultado", ""+resultado);
            info.put("tiempo que tardó el cálculo", time+" milisegundos");
        }
        else
            info.put("resultado", "Valor inválido");
        return Json.encodePrettily(info);
    }
    private void segundo(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();

        String decoded = routingContext.getBodyAsString();
        String jsonResponse = procesa(decoded, request);
        response.
        setStatusCode(200).
        putHeader("content-type", "application/json; charset=utf-8").
        end(jsonResponse);
    }
    
    private void tercero(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        HttpServerRequest request = routingContext.request();
        String mode = request.getParam("source");
        String rev = revisa(mode);
        response.
        setStatusCode(200).
        putHeader("content-type", "application/json; charset=utf-8").
        end(rev);
    }    
    private String revisa(String decoded) {
        Map<String, String> autos = new HashMap<>();
        boolean r = calcula(decoded);
        autos.put("resultado", r+"");
        return Json.encodePrettily( autos);
    }
    
    private boolean calcula(String d) {
        String tmp = d+d;
        return tmp.substring(1, tmp.length()-1).contains(d);
    }
    private String procesa(String decoded, HttpServerRequest request) {
        Map<String, String> srv = new HashMap<>();
        srv.put("Current Node IP", request.localAddress().host());
        srv.put("Caller IP", request.remoteAddress().host());
        
        srv.put("Absolute url", request.absoluteURI());
        srv.put("path", request.path());
        srv.put("query", request.query());
        
        srv.put("uri", request.uri());


        
        Map<String, String> autos = new HashMap<>();
        autos.put("primero", "Ferrari");
        autos.put("segundo", "Lamborgini");
        autos.put("tercero", "Bugatti");
        
        Map<Object, Object> info = new HashMap<>();
        info.put("decoded", decoded);
        info.put("nombre", "gustavo");
        info.put("edad", "21");
        info.put("autos", autos);
        info.put("variable", pba);
        info.put("server-info", srv);
        return Json.encodePrettily(info);
    }

}
