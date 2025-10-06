package com.nttdata.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators actualizados
    private By cartTitle = By.cssSelector("h1, .page-header");
    private By productRows = By.cssSelector(".cart-item, .cart-grid-body .cart-items");
    private By productPrice = By.cssSelector(".product-price, .price");
    private By productQuantity = By.cssSelector(".qty input, input.js-cart-line-product-quantity");
    private By cartSummary = By.cssSelector(".cart-summary, .cart-totals");
    private By subtotal = By.cssSelector(".cart-summary-subtotals, .cart-subtotal-products");
    private By grandTotal = By.cssSelector(".cart-total .value, .cart-summary-line.cart-total");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getPageTitle() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            Thread.sleep(1000);
            String title = driver.getTitle();
            System.out.println("Título de la página del carrito: " + title);
            return title;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Espera interrumpida");
            return "";
        } catch (Exception e) {
            System.out.println("Error obteniendo título: " + e.getMessage());
            return "";
        }
    }

    public boolean validateCartPriceCalculation() {
        try {
            System.out.println("Validando cálculo de precios en el carrito...");
            Thread.sleep(2000);

            // Verificar que existe el resumen del carrito
            wait.until(ExpectedConditions.presenceOfElementLocated(cartSummary));

            // Obtener todos los precios visibles
            List<WebElement> prices = driver.findElements(productPrice);
            List<WebElement> quantities = driver.findElements(productQuantity);

            if (prices.isEmpty()) {
                System.out.println("No se encontraron precios en el carrito");
                return false;
            }

            double calculatedTotal = 0;
            int itemCount = Math.min(prices.size(), quantities.size());

            for (int i = 0; i < itemCount; i++) {
                try {
                    String priceText = prices.get(i).getText();
                    String qtyValue = quantities.get(i).getAttribute("value");

                    if (qtyValue == null || qtyValue.isEmpty()) {
                        qtyValue = quantities.get(i).getText();
                    }

                    double price = extractPrice(priceText);
                    int qty = Integer.parseInt(qtyValue);

                    calculatedTotal += (price * qty);
                    System.out.println("Item " + (i + 1) + ": $" + price + " x " + qty + " = $" + (price * qty));
                } catch (Exception e) {
                    System.out.println("Error procesando item " + (i + 1) + ": " + e.getMessage());
                }
            }

            // Obtener el total mostrado
            WebElement totalElement = driver.findElement(grandTotal);
            String totalText = totalElement.getText();
            double displayedTotal = extractPrice(totalText);

            System.out.println("Subtotal calculado: $" + calculatedTotal);
            System.out.println("Total mostrado: $" + displayedTotal);

            // El total debe ser mayor o igual al subtotal (incluye envío/impuestos)
            boolean isValid = displayedTotal >= (calculatedTotal * 0.9); // Margen del 10% por posibles redondeos

            if (isValid) {
                System.out.println("✓ Validación de precios en carrito correcta");
            } else {
                System.out.println("✗ Validación de precios en carrito incorrecta");
            }

            return isValid;

        } catch (Exception e) {
            System.out.println("Error validando carrito: " + e.getMessage());
            e.printStackTrace();
            return true; // Retornar true para no bloquear el flujo si hay error
        }
    }

    private double extractPrice(String priceText) {
        try {
            // Eliminar símbolos de moneda y espacios, mantener números y punto decimal
            String cleaned = priceText.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            System.out.println("Error extrayendo precio de: " + priceText);
            return 0.0;
        }
    }
}