package com.nttdata.stepsdefinitions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.nttdata.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class Hooks {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private WebDriver driver;

    @Before
    public void setUp(Scenario scenario) {
        // Crear carpetas necesarias si no existen
        File reportDir = new File("reportes/screenshots");
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // Configurar SparkReporter solo una vez
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("reportes/ExtentReport.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }

        // Crear test individual por escenario
        ExtentTest scenarioTest = extent.createTest(scenario.getName());
        test.set(scenarioTest);
        scenarioTest.info("Inicio del escenario: " + scenario.getName());

        // Obtener driver
        driver = DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (driver != null) {
                // Captura de pantalla
                TakesScreenshot ts = (TakesScreenshot) driver;
                byte[] screenshotBytes = ts.getScreenshotAs(OutputType.BYTES);

                // Guardar imagen
                String sanitizedName = scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_");
                File screenshotFile = new File("reportes/screenshots/" + sanitizedName + ".png");
                Files.write(screenshotFile.toPath(), screenshotBytes);

                // Agregar screenshot al reporte HTML
                String base64Image = Base64.getEncoder().encodeToString(screenshotBytes);
                test.get().addScreenCaptureFromBase64String(base64Image, "Evidencia");

                // Log de resultado
                if (scenario.isFailed()) {
                    test.get().fail("Escenario fallido: " + scenario.getName());
                } else {
                    test.get().pass("Escenario exitoso: " + scenario.getName());
                }

                // Generar PDF con la captura
                generarPDF(scenario, screenshotFile, scenario.isFailed());
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (test.get() != null) {
                test.get().warning("Error durante tearDown: " + e.getMessage());
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
            if (extent != null) {
                extent.flush();
            }
        }
    }

    private void generarPDF(Scenario scenario, File screenshotFile, boolean failed) {
        try {
            String pdfPath = "reportes/Reporte_" + scenario.getName().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            document.add(new Paragraph("Reporte de Prueba - Proyecto Store NTTDATA"));
            document.add(new Paragraph("Escenario: " + scenario.getName()));
            document.add(new Paragraph("Resultado: " + (failed ? "Fallido" : "Exitoso")));
            document.add(Chunk.NEWLINE);

            if (screenshotFile.exists()) {
                Image img = Image.getInstance(screenshotFile.getAbsolutePath());
                img.scaleToFit(500, 400);
                img.setAlignment(Element.ALIGN_CENTER);
                document.add(img);
            } else {
                document.add(new Paragraph("No se encontró la captura."));
            }

            document.close();
            test.get().info("PDF generado: " + pdfPath);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            test.get().warning("Error al generar PDF: " + e.getMessage());
        }
    }
}