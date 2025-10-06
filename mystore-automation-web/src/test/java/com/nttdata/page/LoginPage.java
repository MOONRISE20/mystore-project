package com.nttdata.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Localizadores corregidos según el sitio real
    private By emailInput = By.id("field-email");
    private By passwordInput = By.id("field-password");
    private By submitButton = By.cssSelector("button#submit-login, button.btn[type='submit']");
    private By loginLink = By.cssSelector("a[href*='iniciar-sesion'], a[title*='Iniciar'], .user-info a");
    private By accountLink = By.cssSelector("a[href*='mi-cuenta'], a.account");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void navigateToStore(String url) {
        driver.get(url);
        System.out.println("Navegando a: " + url);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Error cargando página: " + e.getMessage());
        }
    }

    public void clickLoginLink() {
        try {
            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
            link.click();
            System.out.println("Click en 'Iniciar sesión'");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Espera interrumpida");
        } catch (Exception e) {
            System.out.println("Error al hacer click en login: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void enterEmail(String email) {
        WebElement emailField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(emailInput)
        );
        emailField.clear();
        emailField.sendKeys(email);
        System.out.println("Email ingresado: " + email);
    }

    public void enterPassword(String password) {
        WebElement passwordField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordInput)
        );
        passwordField.clear();
        passwordField.sendKeys(password);
        System.out.println("Password ingresado");
    }

    public void clickSubmitButton() {
        WebElement button = wait.until(
                ExpectedConditions.elementToBeClickable(submitButton)
        );
        button.click();
        System.out.println("Click en botón 'INICIAR SESIÓN'");
    }

    public void login(String email, String password) {
        clickLoginLink();
        enterEmail(email);
        enterPassword(password);
        clickSubmitButton();
    }

    public boolean isLoginSuccessful() {
        try {
            // Esperar que desaparezca el formulario de login o que aparezca el enlace de cuenta
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(accountLink),
                    ExpectedConditions.invisibilityOfElementLocated(By.id("login-form"))
            ));

            // Verificar si existe el link de cuenta
            boolean hasAccount = driver.findElements(accountLink).size() > 0;

            if (hasAccount) {
                System.out.println("Login exitoso - Usuario autenticado");
                return true;
            } else {
                System.out.println("Login fallido - Usuario no autenticado");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Login fallido - Timeout o elemento no encontrado");
            return false;
        }
    }
}
