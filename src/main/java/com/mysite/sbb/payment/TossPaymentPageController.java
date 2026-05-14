package com.mysite.sbb.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TossPaymentPageController {

    private static final String CLIENT_KEY = "test_ck_P9BRQmyarYlBePmM9qKaVJ07KzLN";

    @GetMapping(value = "/toss/payment", produces = "text/html;charset=UTF-8")
    public String paymentPage(
            @RequestParam String amount,
            @RequestParam String orderId,
            @RequestParam String orderName,
            @RequestParam String method,
            @RequestParam String successUrl,
            @RequestParam String failUrl) {

        String html = "<!DOCTYPE html>"
            + "<html><head><meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
            + "</head><body>"
            + "<script src='https://js.tosspayments.com/v1/payment'></script>"
            + "<script>"
            + "window.onload = function() {"
            + "  var tossPayments = TossPayments('" + CLIENT_KEY + "');"
            + "  tossPayments.requestPayment('" + method + "', {"
            + "    amount: " + amount + ","
            + "    orderId: '" + orderId + "',"
            + "    orderName: '" + orderName + "',"
            + "    successUrl: '" + successUrl + "',"
            + "    failUrl: '" + failUrl + "',"
            + "  }).catch(function(e) {"
            + "    window.location.href = '" + failUrl + "&code=' + e.code;"
            + "  });"
            + "};"
            + "</script></body></html>";

        return html;
    }
}
