package com.infy.ekart.apigateway.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerHubController {

    @GetMapping(value = "/swagger", produces = MediaType.TEXT_HTML_VALUE)
    public String hub() {
        return """
            <!DOCTYPE html><html><head><title>eKart APIs</title>
            <style>
                body{font-family:Arial;background:linear-gradient(135deg,#667eea,#764ba2);padding:40px}
                h1{color:#fff;text-align:center}
                .grid{display:grid;grid-template-columns:repeat(3,1fr);gap:20px;max-width:900px;margin:auto}
                .card{background:#fff;padding:20px;border-radius:12px;text-align:center}
                .btn{background:#2874f0;color:#fff;padding:10px 20px;text-decoration:none;border-radius:6px;display:inline-block;margin-top:10px}
            </style></head><body>
            <h1>🚀 eKart API Hub</h1>
            <div class="grid">
                <div class="card"><h3>🔐 Auth</h3><p>:8081</p><a href="http://localhost:8081/swagger-ui/index.html" target="_blank" class="btn">Swagger</a></div>
                <div class="card"><h3>🛒 Cart</h3><p>:8082</p><a href="http://localhost:8082/swagger-ui/index.html" target="_blank" class="btn">Swagger</a></div>
                <div class="card"><h3>📦 Product</h3><p>:8083</p><a href="http://localhost:8083/swagger-ui/index.html" target="_blank" class="btn">Swagger</a></div>
                <div class="card"><h3>💳 Payment</h3><p>:8084</p><a href="http://localhost:8084/swagger-ui/index.html" target="_blank" class="btn">Swagger</a></div>
                <div class="card"><h3>📧 Notify</h3><p>:8085</p><a href="http://localhost:8085/swagger-ui/index.html" target="_blank" class="btn">Swagger</a></div>
                <div class="card"><h3>📋 Order</h3><p>:8086</p><a href="http://localhost:8086/swagger-ui/index.html" target="_blank" class="btn">Swagger</a></div>
            </div>
            </body></html>
            """;
    }
}