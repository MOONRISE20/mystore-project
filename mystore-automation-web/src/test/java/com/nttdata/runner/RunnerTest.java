package com.nttdata.runner;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.nttdata.core.DriverManager;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.nttdata.stepsdefinitions", "com.nttdata.steps"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber.html",
                "json:target/cucumber-reports/cucumber.json",
                "junit:target/cucumber-reports/cucumber.xml"
        },
        monochrome = true,
        tags = ""
)
public class RunnerTest {
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeClass
    public static void setup() {
        System.out.println("========================================");
        System.out.println("INICIANDO PRUEBAS DE AUTOMATIZACIÓN");
        System.out.println("========================================");

        DriverManager.initialize();

        // Configurar ExtentReports
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = "target/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("Reporte de Automatización - MyStore");
        sparkReporter.config().setReportName("Pruebas de Regresión");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Tester", "NTT Data - Valia Tataje");
        extent.setSystemInfo("Navegador", "Chrome 141");
        extent.setSystemInfo("URL", "https://qalab.bensg.com/store");
        extent.setSystemInfo("Ambiente", "QA");
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));

        test = extent.createTest("Ejecución de Pruebas Automatizadas");
        test.log(Status.INFO, "Inicio de la ejecución de pruebas");

        System.out.println("ExtentReport configurado: " + reportPath);
    }

    @AfterClass
    public static void tearDown() {
        System.out.println("========================================");
        System.out.println("FINALIZANDO PRUEBAS");
        System.out.println("========================================");

        if (test != null) {
            test.log(Status.INFO, "Fin de la ejecución de pruebas");
        }

        DriverManager.quit();

        if (extent != null) {
            extent.flush();
            System.out.println("Reporte generado exitosamente");
        }
    }
}