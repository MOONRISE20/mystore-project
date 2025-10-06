package com.nttdata.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }

    private static ExtentReports createInstance() {
        String reportPath = System.getProperty("user.dir") + "/reportes/ExtentReport.html";
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
        reporter.config().setDocumentTitle("Reporte de Automatización - Store");
        reporter.config().setReportName("Resultados de Pruebas de Regresión");

        extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Proyecto", "Store NTTDATA");
        extent.setSystemInfo("Ejecutado por", "Valia Tataje");
        extent.setSystemInfo("Entorno", "QA");
        return extent;
    }
}
