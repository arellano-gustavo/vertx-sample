package mx.unam.dgpe.sample.controller;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import io.vertx.core.AbstractVerticle;
import static mx.unam.dgpe.sample.controller.RestUtil.*;

public class TestMyController extends AbstractVerticle {
    private static final Logger logger = Logger.getLogger(TestMyController.class);
    
    @Test
    public void ok() throws Exception {
        String result = sendGet("https://www.binance.com/api/v3/ticker/price?symbol=BTCUSDT");
        assertTrue("Este es un mensaje", result.length()>1);
        assertFalse("Este es un mensaje", result.length()>10000);
        logger.info(result);
    }

}
