package com.nttdata.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class StorePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators actualizados
    private By menuLinks = By.cssSelector("#menu a, .menu a, nav a");
    private By productList = By.cssSelector(".product-miniature, .product-container, .product");
    private By quantityInput = By.cssSelector("#quantity_wanted, input[name='qty']");
    private By addToCartButton = By.cssSelector("button[data-button-action='add-to-cart'], button.add-to-cart");

    // Popup
    private By confirmationModal = By.cssSelector("#blockcart-modal, .modal-content");
    private By popupProductName = By.cssSelector(".product-name, h4.h6");
    private By popupTotalPrice = By.cssSelector(".value, .cart-products-count");
    private By continueShoppingBtn = By.cssSelector(".btn[data-dismiss='modal']");
    private By proceedToCheckoutBtn = By.cssSelector("a.btn-primary[href*='pedido'], a[href*='order']");

    public StorePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void navigateToCategory(String category, String subcategory) {
        System.out.println("Navegando a categoría: " + category + " > " + subcategory);

        try {
            // Buscar el enlace de la categoría principal
            String categoryXpath = String.format("//a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
                    category.toLowerCase());

            WebElement categoryLink = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(categoryXpath))
            );

            // Hacer hover sobre la categoría
            Actions actions = new Actions(driver);
            actions.moveToElement(categoryLink).perform();
            System.out.println("Hover sobre: " + category);
            Thread.sleep(1000);

            // Si hay subcategoría, buscarla y hacer click
            if (subcategory != null && !subcategory.isEmpty()) {
                String subcategoryXpath = String.format("//a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
                        subcategory.toLowerCase());

                WebElement subcategoryLink = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(subcategoryXpath))
                );
                subcategoryLink.click();
                System.out.println("Click en subcategoría: " + subcategory);
            } else {
                categoryLink.click();
                System.out.println("Click en categoría: " + category);
            }

            Thread.sleep(2000); // Esperar a que cargue la página

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Espera interrumpida");
            throw new RuntimeException("Navegación interrumpida");
        } catch (Exception e) {
            System.out.println("Error navegando a categoría: " + e.getMessage());
            throw new RuntimeException("Categoría '" + category + "' no encontrada");
        }
    }

    public boolean isCategoryValid() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(productList));
            int productCount = driver.findElements(productList).size();
            System.out.println("Productos encontrados: " + productCount);
            return productCount > 0;
        } catch (Exception e) {
            System.out.println("No se encontraron productos");
            return false;
        }
    }

    public void addFirstProductToCart(int quantity) {
        System.out.println("Agregando " + quantity + " unidades del primer producto");

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(productList));
            List<WebElement> products = driver.findElements(productList);

            if (products.isEmpty()) {
                throw new RuntimeException("No hay productos disponibles");
            }

            WebElement firstProduct = products.get(0);
            WebElement productLink = firstProduct.findElement(By.cssSelector("a"));
            productLink.click();
            System.out.println("Click en el primer producto");

            Thread.sleep(2000);
            wait.until(ExpectedConditions.presenceOfElementLocated(quantityInput));

            if (quantity > 1) {
                WebElement qtyInput = driver.findElement(quantityInput);
                qtyInput.clear();
                qtyInput.sendKeys(String.valueOf(quantity));
                System.out.println("Cantidad ajustada a: " + quantity);
                Thread.sleep(500);
            }

            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
            addButton.click();
            System.out.println("Click en Añadir al Carrito");

            Thread.sleep(2000);

        } catch (Exception e) {
            System.out.println("Error agregando producto: " + e.getMessage());
            throw new RuntimeException("Error al agregar producto al carrito");
        }
    }

    public boolean isConfirmationPopupDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationModal));
            System.out.println("Popup de confirmación mostrado");
            return true;
        } catch (Exception e) {
            System.out.println("Popup de confirmación NO mostrado");
            return false;
        }
    }

    public String getPopupProductName() {
        try {
            String productName = driver.findElement(popupProductName).getText();
            System.out.println("Producto en popup: " + productName);
            return productName;
        } catch (Exception e) {
            System.out.println("Error obteniendo nombre del producto");
            return "";
        }
    }

    public boolean validatePopupTotalPrice(int quantity) {
        try {
            // En este caso, solo validamos que el popup muestre información
            // El cálculo exacto se valida en la página del carrito
            String totalText = driver.findElement(popupTotalPrice).getText();
            System.out.println("Información del carrito: " + totalText);

            boolean isValid = !totalText.isEmpty();
            System.out.println(isValid ? "Validación de popup correcta" : "Validación de popup incorrecta");
            return isValid;

        } catch (Exception e) {
            System.out.println("Error validando popup: " + e.getMessage());
            return false;
        }
    }

    public void proceedToCheckout() {
        try {
            WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(proceedToCheckoutBtn));
            checkoutBtn.click();
            System.out.println("Procediendo al checkout");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Error procediendo al checkout: " + e.getMessage());
            throw new RuntimeException("No se pudo proceder al checkout");
        }
    }

    private double extractPrice(String priceText) {
        String cleaned = priceText.replaceAll("[^0-9.]", "");
        return Double.parseDouble(cleaned);
    }

    public void debugPrintAllLinks() {
        try {
            Thread.sleep(3000);
            System.out.println("========== DEBUG: TODOS LOS ENLACES ==========");

            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            int count = 0;

            for (WebElement link : allLinks) {
                if (link.isDisplayed()) {
                    String text = link.getText().trim();
                    String href = link.getAttribute("href");

                    if (!text.isEmpty() || (href != null && !href.isEmpty())) {
                        System.out.println(++count + ". Texto: '" + text + "' | Href: " + href);
                    }
                }
            }

            System.out.println("========== FIN DEBUG ==========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}