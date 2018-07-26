package mx.qbits.crypto.executor.controller;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.limitSell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.exception.BinanceApiException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import mx.qbits.crypto.executor.model.AuthData;
import mx.qbits.crypto.executor.model.Operacion;
import mx.qbits.crypto.store.Account;
import mx.qbits.crypto.store.AccountInfoResolver;

public class MainController extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(MainController.class);
    private int port = 6060;
    private BinanceApiRestClient client;
    
    public void start(Future<Void> fut) {
        long start = System.currentTimeMillis();
        logger.info("Inicializando Vertical de Login en puerto interno: " + port);
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
        
        router.post("/crypto-trader/bitcoin/coloca").handler(this::processRequest);
        router.post("/crypto-trader/bitcoin/elimina").handler(this::processRequest);
        
        vertx.createHttpServer().requestHandler(router::accept).listen( 
                config().getInteger("http.port", port), result -> {
                    logger.info("Hola ---> "+port);
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
        check();
        long end = System.currentTimeMillis() -start;
        logger.info("Vertical de Executor iniciada en " + end + " milisegundos.");
    }
    
    private void check() {
        String usr = System.getenv("USER_ID");
        if(usr==null) {
            logger.error("Error grave: no existe la variable de ambiente USER_ID !!!");
            System.exit(1);
        }
        
        AccountInfoResolver op = new AccountInfoResolver();
        String credFile = System.getenv("CRED_FILE");
        if(credFile==null) {
            logger.error("Error grave: no existe el archivo de credenciales !!!");
            System.exit(1);
        }
        
        Account[] accInfo = op.getAccountsInfo(credFile);
        for(Account account : accInfo) {
            if(usr.equals(account.getUser())) {
                AuthData.usr = account.getUser();
                AuthData.key = account.getKey();
                AuthData.secret = account.getSecret();
                logger.info("Usuario "+AuthData.usr+" logueado al sistema exitosamente !!!");
                BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(AuthData.key, AuthData.secret);
                this.client = factory.newRestClient();
                return;
            }
        }

        logger.error("Error grave: no fue posible encontrar los datos de " + usr);
        System.exit(1);
    }
    
    private void processRequest(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        String decoded = routingContext.getBodyAsString();
        String jsonResponse = invokeApi(decoded);
        response.
        setStatusCode(200)
        .putHeader("content-type", "application/json; charset=utf-8")
        .putHeader("content-type", "application/json")
        .putHeader("Access-Control-Allow-Origin", "*")
        .putHeader("Access-Control-Allow-Methods","GET, POST, OPTIONS")
        .putHeader("Access-Control-Allow-Credentials", "true")
        .end(jsonResponse);
    }
    
    private String invokeApi(String decoded) {
        Operacion info = null;
        info = Json.decodeValue(decoded, Operacion.class);
        logger.info(info.toString());
        double delta = 1.025;
        List<AggTrade> trades = client.getAggTrades(AuthData.symbol);
        if(trades.size()>0) {
            String price = trades.get(0).getPrice();
            Double dbl = Double.parseDouble(price);
            if("compra".equals(info.getAccion())) {
                if(dbl>info.getValor()*delta) {
                    logger.error("La operación no fue efectuada "
                            + "debido a que el humbral de compra ("+delta+") fue "
                            + "exedido. Valor de mercado: "+dbl+". Valor enviado: "+info.getValor()); 
                }
                return "{\"error\":true}";
            }
            if("venta".equals(info.getAccion())) {
                if(dbl<info.getValor()/delta) {
                    logger.error("La operación no fue efectuada "
                            + "debido a que el humbral de venta ("+delta+") fue "
                            + "exedido. Valor de mercado: "+dbl+". Valor enviado: "+info.getValor()); 
                }
                return "{\"error\":true}";
            }
        }
        
        if("compra".equals(info.getAccion())) {
            logger.info("comprando...");
            NewOrderResponse newOrderResponse = client.newOrder(
                    limitBuy(AuthData.symbol, TimeInForce.GTC, info.getCantidad()+"", info.getValor()+""));
            logger.info(newOrderResponse);
        } else if ("venta".equals(info.getAccion())) {
            logger.info("vendiendo...");
            NewOrderResponse newOrderResponse = client.newOrder(
                    limitSell(AuthData.symbol, TimeInForce.GTC, info.getCantidad()+"", info.getValor()+""));
            logger.info(newOrderResponse);
        } else if (info.getValor()==0 && info.getCantidad()==0 && info.getAccion().length()>0) {
            logger.info("cancelando...");
            try {
                this.client.cancelOrder(new CancelOrderRequest(AuthData.symbol, info.getAccion()));
            } catch (BinanceApiException e) {
                logger.error(e.getError().getMsg());
            }
        }
        return decoded;
    }

}
