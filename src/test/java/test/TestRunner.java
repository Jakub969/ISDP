package test;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=== SPÚŠŤANIE TESTOV ===\n");
        
        String[] testClasses = {
            "subory.DataLoadersTest",
            "mvp.ModelCalculationTest",
            "udaje.TurnusZmenaCalculationTest"
        };
        
        int totalTests = 0;
        int totalPassed = 0;
        int totalFailed = 0;
        
        for (String testClass : testClasses) {
            System.out.println("--- " + testClass + " ---");
            
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(testClass))
                .build();
            
            Launcher launcher = LauncherFactory.create();
            SummaryGeneratingListener listener = new SummaryGeneratingListener();
            
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);
            
            TestExecutionSummary summary = listener.getSummary();
            
            long testsFound = summary.getTestsFoundCount();
            long testsSucceeded = summary.getTestsSucceededCount();
            long testsFailed = summary.getTestsFailedCount();
            
            totalTests += testsFound;
            totalPassed += testsSucceeded;
            totalFailed += testsFailed;
            
            System.out.println("  Nájdené: " + testsFound);
            System.out.println("  Úspešné: " + testsSucceeded);
            System.out.println("  Neúspešné: " + testsFailed);
            
            if (testsFailed > 0) {
                System.out.println("  Chyby:");
                summary.getFailures().forEach(failure -> {
                    System.out.println("    ✗ " + failure.getTestIdentifier().getDisplayName());
                    System.out.println("      " + failure.getException().getMessage());
                });
            }
            
            System.out.println();
        }
        
        System.out.println("=== SÚHRN ===");
        System.out.println("Celkom testov: " + totalTests);
        System.out.println("Úspešné: " + totalPassed);
        System.out.println("Neúspešné: " + totalFailed);
        System.out.println("Úspešnosť: " + (totalTests > 0 ? (totalPassed * 100 / totalTests) : 0) + "%");
        System.out.println();
        
        if (totalFailed == 0) {
            System.out.println("✓ VŠETKY TESTY PREŠLI!");
            System.exit(0);
        } else {
            System.out.println("✗ NIEKTORÉ TESTY ZLYHALI");
            System.exit(1);
        }
    }
}
