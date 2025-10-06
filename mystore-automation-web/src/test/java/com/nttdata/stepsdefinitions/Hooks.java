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
import java.util.Base64;

public class Hooks {

    private static ExtentReports extent;
    private static ExtentTest test;
    private WebDriver driver;

    @Before
    public void setUp(Scenario scenario) {
        // Crear carpeta de reportes si no existe
        new File("reportes/screenshots").mkdirs();

        // Configurar el reporte Spark (reemplazo moderno del HTML)
        ExtentSparkReporter spark = new ExtentSparkReporter("reportes/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Crear test por escenario
        test = extent.createTest(scenario.getName());
        test.info("Inicio del escenario: " + scenario.getName());

        // Iniciar el driver desde DriverFactory
        driver = DriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            try {
                // Captura de pantalla
                TakesScreenshot ts = (TakesScreenshot) driver;
                byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);

                // Guardar imagen localmente
                String screenshotPath = "reportes/screenshots/" + scenario.getName().replaceAll(" ", "_") + ".png";
                FileOutputStream fos = new FileOutputStream(screenshotPath);
                fos.write(screenshot);
                fos.close();

                // Agregar imagen al reporte HTML
                String base64Image = Base64.getEncoder().encodeToString(screenshot);
                test.addScreenCaptureFromBase64String(base64Image, scenario.getName());

                // Registrar resultado del escenario
                if (scenario.isFailed()) {
                    test.fail("❌ El escenario falló.");
                } else {
                    test.pass("✅ El escenario pasó exitosamente.");
                }

                // Generar el PDF con la imagen y el resultado
                try {
                    String pdfPath = "reportes/Reporte_" + scenario.getName().replaceAll(" ", "_") + ".pdf";
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
                    document.open();
                    document.add(new Paragraph("📘 Reporte de Prueba - Proyecto Store NTTDATA"));
                    document.add(new Paragraph("Escenario: " + scenario.getName()));
                    document.add(new Paragraph("Resultado: " + (scenario.isFailed() ? "Fallido ❌" : "Exitoso ✅")));
                    document.add(Chunk.NEWLINE);

                    File screenshotFile = new File(screenshotPath);
                    if (screenshotFile.exists()) {
                        Image img = Image.getInstance(screenshotPath);
                        img.scaleToFit(500, 400);
                        img.setAlignment(Element.ALIGN_CENTER);
                        document.add(img);
                    } else {
                        document.add(new Paragraph("⚠️ No se encontró la captura de pantalla."));
                    }

                    document.close();
                    test.info("📄 Reporte PDF generado en: " + pdfPath);
                } catch (DocumentException | IOException e) {
                    e.printStackTrace();
                    test.warning("Error al generar el PDF: " + e.getMessage());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                driver.quit();
            }

            // Guardar el reporte HTML
            extent.flush();
        }
    }
}
