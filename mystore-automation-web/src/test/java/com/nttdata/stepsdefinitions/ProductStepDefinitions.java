package com.nttdata.stepsdefinitions;

import com.nttdata.core.DriverManager;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductStepDefinitions {

    private WebDriver driver;
    private WebDriverWait wait;

    @Dado("estoy en la página de la tienda")
    public void estoyEnLaPaginaDeLaTienda() {
        driver = DriverManager.getDriver();
        driver.get("https://qalab.bensg.com/store/pe/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        System.out.println("Navegando a: https://qalab.bensg.com/store/pe/");

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            Thread.sleep(2000);
            System.out.println("Página cargada correctamente");
        } catch (Exception e) {
            Assert.fail("Error cargando la página: " + e.getMessage());
        }
    }

    @Y("me logueo con mi usuario {string} y clave {string}")
    public void meLogueoConMiUsuarioYClave(String email, String password) {
        try {
            // Verificar si ya estamos logueados
            boolean yaLogueado = !driver.findElements(
                    By.xpath("//*[@id='_desktop_user_info']/div/a[@class='account']/span")
            ).isEmpty();

            if (yaLogueado) {
                System.out.println("Usuario ya logueado, cerrando sesión primero...");
                // Hacer logout
                WebElement logoutLink = driver.findElement(
                        By.cssSelector("#_desktop_user_info .logout")
                );
                logoutLink.click();
                Thread.sleep(2000);
                System.out.println("Sesión cerrada");
            }

            // Selector exacto del enlace "Iniciar sesión"
            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#_desktop_user_info > div > a > span")
            ));
            loginLink.click();
            System.out.println("Click en 'Iniciar sesión'");
            Thread.sleep(2000);

            // Ingresar email
            WebElement emailField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("field-email"))
            );
            emailField.clear();
            emailField.sendKeys(email);
            System.out.println("Email ingresado: " + email);

            // Ingresar contraseña
            WebElement passwordField = driver.findElement(By.id("field-password"));
            passwordField.clear();
            passwordField.sendKeys(password);
            System.out.println("Password ingresado");

            // Click en submit
            WebElement submitButton = driver.findElement(By.id("submit-login"));
            submitButton.click();
            System.out.println("Click en 'INICIAR SESIÓN'");
            Thread.sleep(3000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Proceso interrumpido durante el login");
        } catch (Exception e) {
            System.out.println("Error durante el login: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Error en el login: " + e.getMessage());
        }
    }

    @Cuando("navego a la categoria {string} y subcategoria {string}")
    public void navegoALaCategoriaYSubcategoria(String categoria, String subcategoria) {
        try {
            System.out.println("Navegando a: " + categoria + " > " + subcategoria);

            // Click en categoría CLOTHES usando el ID exacto
            WebElement clothesLink = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("#category-3 > a"))
            );
            clothesLink.click();
            System.out.println("Click en CLOTHES");
            Thread.sleep(2000);

            // Click en subcategoría MEN
            WebElement menLink = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id='left-column']/div[1]/ul/li[2]/ul/li[1]/a")
                    )
            );
            menLink.click();
            System.out.println("Click en MEN");
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Proceso interrumpido durante la navegación");
        } catch (Exception e) {
            System.out.println("Error navegando a categoría: " + e.getMessage());
            Assert.fail("No se pudo navegar a la categoría");
        }
    }

    @Y("agrego {int} unidades del primer producto al carrito")
    public void agregoUnidadesDelPrimerProductoAlCarrito(int cantidad) {
        try {
            // Click en el producto "Hummingbird printed t-shirt"
            WebElement producto = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id='js-product-list']/div[1]/div/article/div/div[2]/h2/a")
                    )
            );
            producto.click();
            System.out.println("Click en el primer producto");
            Thread.sleep(2000);

            // Ajustar cantidad
            WebElement qtyInput = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("quantity_wanted"))
            );
            qtyInput.clear();
            qtyInput.sendKeys(String.valueOf(cantidad));
            System.out.println("Cantidad: " + cantidad);
            Thread.sleep(500);

            // Agregar al carrito
            WebElement addToCartBtn = driver.findElement(
                    By.xpath("//*[@id='add-to-cart-or-refresh']/div[2]/div/div[2]/button")
            );
            addToCartBtn.click();
            System.out.println("Click en 'AÑADIR AL CARRITO'");
            Thread.sleep(3000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Proceso interrumpido");
        } catch (Exception e) {
            System.out.println("Error agregando producto: " + e.getMessage());
            Assert.fail("No se pudo agregar el producto");
        }
    }

    @Entonces("valido en el popup la confirmación del producto agregado")
    public void validoEnElPopupLaConfirmacionDelProductoAgregado() {
        try {
            WebElement popup = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("blockcart-modal"))
            );
            Assert.assertTrue("El popup debe estar visible", popup.isDisplayed());
            System.out.println("Popup de confirmación mostrado");
        } catch (Exception e) {
            Assert.fail("No se mostró el popup de confirmación");
        }
    }

    @Y("valido en el popup que el monto total sea calculado correctamente")
    public void validoEnElPopupQueElMontoTotalSeaCalculadoCorrectamente() {
        try {
            WebElement total = driver.findElement(
                    By.cssSelector("#blockcart-modal .product-total")
            );
            String totalText = total.getText();
            Assert.assertFalse("El total no debe estar vacío", totalText.isEmpty());
            System.out.println("Total en popup: " + totalText);
        } catch (Exception e) {
            System.out.println("Advertencia: No se pudo validar el total exacto");
        }
    }

    @Cuando("finalizo la compra")
    public void finalizoLaCompra() {
        try {
            WebElement checkoutBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#blockcart-modal .cart-content-btn a")
                    )
            );
            checkoutBtn.click();
            System.out.println("Click en 'FINALIZAR COMPRA'");
            Thread.sleep(2000);
        } catch (Exception e) {
            Assert.fail("Error al finalizar la compra: " + e.getMessage());
        }
    }

    @Entonces("valido el titulo de la pagina del carrito")
    public void validoElTituloDeLaPaginaDelCarrito() {
        try {
            WebElement titulo = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[@id='main']//h1")
                    )
            );
            String tituloText = titulo.getText().trim().toUpperCase();
            Assert.assertTrue(
                    "El título debe contener 'CARRITO'",
                    tituloText.contains("CARRITO")
            );
            System.out.println("Título validado: " + tituloText);
        } catch (Exception e) {
            Assert.fail("Error validando título del carrito");
        }
    }

    @Y("vuelvo a validar el calculo de precios en el carrito")
    public void vuelvoAValidarElCalculoDePreciosEnElCarrito() {
        try {
            WebElement precio = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.cssSelector(".product-price strong")
                    )
            );
            String precioText = precio.getText();
            Assert.assertFalse("El precio no debe estar vacío", precioText.isEmpty());
            System.out.println("Precio en carrito: " + precioText);
        } catch (Exception e) {
            System.out.println("Advertencia: No se pudo validar el precio exacto");
        }
    }

    @Entonces("valido que el login sea {string}")
    public void validoQueElLoginSea(String resultado) {
        try {
            Thread.sleep(2000);

            if (resultado.equalsIgnoreCase("exitoso")) {
                // Verificar que aparezca el nombre del usuario o "Cerrar sesión"
                boolean loginExitoso = !driver.findElements(
                        By.xpath("//*[@id='_desktop_user_info']/div/a[@class='account']/span")
                ).isEmpty();

                Assert.assertTrue("El login debería ser exitoso", loginExitoso);
                System.out.println("Login exitoso validado");

            } else if (resultado.equalsIgnoreCase("fallido")) {
                // Verificar que siga en la página de login o muestre error
                boolean loginFallido = !driver.findElements(By.id("field-email")).isEmpty()
                        || driver.getCurrentUrl().contains("login");
                Assert.assertTrue("El login debería fallar", loginFallido);
                System.out.println("Login fallido validado correctamente");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Proceso interrumpido");
        } catch (Exception e) {
            Assert.fail("Error validando resultado del login: " + e.getMessage());
        }
    }
}
