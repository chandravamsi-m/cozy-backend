package com.cozycreations.backend.services;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
public class CatalogueService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] generatePdfCatalogue(List<Map<String, Object>> products, String collectionTitle) {
        log.info("Starting Playwright PDF generation for catalogue...");

        // Pre-process products to add computed fields required by Thymeleaf
        for (Map<String, Object> p : products) {
            String name = (String) p.getOrDefault("name", "Product");
            int baseLength = 20;
            int fontSize = name.length() > baseLength ? Math.max(18, 29 - (name.length() - baseLength) / 2) : 29;
            p.put("fontSize", fontSize);

            String imageUrl = (String) p.get("imageUrl");
            if (imageUrl != null && imageUrl.contains("cloudinary.com") && !imageUrl.contains("/upload/w_")) {
                p.put("optimizedImageUrl", imageUrl.replace("/upload/", "/upload/w_300,h_300,c_fill,f_auto,q_auto:good/"));
            } else {
                p.put("optimizedImageUrl", imageUrl != null ? imageUrl : "");
            }

            Object packObj = p.get("quantityPack");
            int pack = packObj instanceof Number ? ((Number) packObj).intValue() : 1;
            Object priceObj = p.get("price");
            double price = priceObj instanceof Number ? ((Number) priceObj).doubleValue() : 0.0;
            p.put("badgeText", "Pack of " + pack + " - ₹" + price + "/ + courier");

            Object weightObj = p.get("weightGrams");
            String weight = weightObj != null ? String.valueOf(weightObj) : "?";
            Object burnTimeObj = p.get("burnTimeHours");
            String burnTime = burnTimeObj != null ? String.valueOf(burnTimeObj) : "?";
            String waxType = (String) p.get("waxType");
            waxType = (waxType != null && !waxType.isEmpty()) ? waxType.substring(0, 1).toUpperCase() + waxType.substring(1) : "Soy";
            String dimensions = (String) p.get("dimensions");
            String dimensionsPart = dimensions != null ? "(" + dimensions + ")" : "";
            p.put("descriptionHtml", "| Natural " + waxType + " Wax |<br>Aromatherapy Candle | Perfect for Home Decor & Gifting" + dimensionsPart + "(" + weight + "g - " + burnTime + "hrs)");
        }
        
        // 1. Process HTML using Thymeleaf
        Context welcomeCtx = new Context();
        String welcomeHtml = templateEngine.process("catalogue/welcome", welcomeCtx);

        Context template1Ctx = new Context();
        template1Ctx.setVariable("collectionTitle", collectionTitle);
        template1Ctx.setVariable("products", products);
        String template1Html = templateEngine.process("catalogue/template1", template1Ctx);

        List<String> htmlPages = List.of(welcomeHtml, template1Html);
        
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true)
                    .setArgs(List.of("--no-sandbox", "--disable-setuid-sandbox", "--disable-dev-shm-usage", "--disable-gpu", "--font-render-hinting=none")));
            Page page = browser.newPage();
            
            // Note: In reality, you'd iterate through htmlPages, render them, and use Apache PDFBox to stitch them.
            // For now, we will render the first page as a POC.
            if (!htmlPages.isEmpty()) {
                page.setContent(htmlPages.get(1)); // Use template1 for POC
                page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
                
                byte[] pdfBytes = page.pdf(new Page.PdfOptions().setFormat("A4").setPrintBackground(true));
                log.info("Successfully generated PDF snippet.");
                return pdfBytes;
            }
        } catch (Exception e) {
            log.error("Failed during PDF generation with Playwright", e);
        }
        return new byte[0];
    }
}
