$ErrorActionPreference = "Continue"

Write-Host "=== SPÚŠŤANIE TESTOV ===" -ForegroundColor Cyan
Write-Host ""

$testJar = "$env:USERPROFILE\.m2\repository\org\junit\jupiter\junit-jupiter-api\5.10.1\junit-jupiter-api-5.10.1.jar"
$engineJar = "$env:USERPROFILE\.m2\repository\org\junit\jupiter\junit-jupiter-engine\5.10.1\junit-jupiter-engine-5.10.1.jar"
$paramsJar = "$env:USERPROFILE\.m2\repository\org\junit\jupiter\junit-jupiter-params\5.10.1\junit-jupiter-params-5.10.1.jar"
$platformCommons = "$env:USERPROFILE\.m2\repository\org\junit\platform\junit-platform-commons\1.10.1\junit-platform-commons-1.10.1.jar"
$platformEngine = "$env:USERPROFILE\.m2\repository\org\junit\platform\junit-platform-engine\1.10.1\junit-platform-engine-1.10.1.jar"
$opentest4j = "$env:USERPROFILE\.m2\repository\org\opentest4j\opentest4j\1.3.0\opentest4j-1.3.0.jar"
$gsonJar = "$env:USERPROFILE\.m2\repository\com\google\code\gson\gson\2.10.1\gson-2.10.1.jar"
$gurobiJar = "$env:USERPROFILE\.m2\repository\com\gurobi\gurobi\11.0.1\gurobi-11.0.1.jar"

$classpath = "target/classes;target/test-classes;$testJar;$engineJar;$paramsJar;$platformCommons;$platformEngine;$opentest4j;$gsonJar;$gurobiJar"

$java = 'C:\Users\z005383v\.vscode\extensions\redhat.java-1.51.0-win32-x64\jre\21.0.9-win32-x86_64\bin\java.exe'

$tests = @(
    @{Name="DataLoadersTest"; Class="subory.DataLoadersTest"},
    @{Name="ModelCalculationTest"; Class="mvp.ModelCalculationTest"},
    @{Name="TurnusZmenaCalculationTest"; Class="udaje.TurnusZmenaCalculationTest"}
)

$totalPassed = 0
$totalFailed = 0

foreach ($test in $tests) {
    Write-Host "--- $($test.Name) ---" -ForegroundColor Yellow
    
    $output = & $java -cp $classpath org.junit.platform.suite.engine.VintageTestEngine $test.Class 2>&1
    
    $reflection = [System.Reflection.Assembly]::LoadFrom($testJar)
    
    try {
        $testClass = [System.Reflection.Assembly]::LoadFrom("target/test-classes/$($test.Class.Replace('.','/')).class")
        $methods = $testClass.GetMethods() | Where-Object { $_.GetCustomAttributes($false) | Where-Object { $_.GetType().Name -like "*Test*" } }
        
        foreach ($method in $methods) {
            try {
                $instance = $testClass.GetConstructor(@()).Invoke(@())
                
                $setupMethod = $testClass.GetMethod("setUp")
                if ($setupMethod) {
                    $setupMethod.Invoke($instance, @())
                }
                
                $method.Invoke($instance, @())
                Write-Host "  ✓ $($method.Name)" -ForegroundColor Green
                $totalPassed++
            }
            catch {
                Write-Host "  ✗ $($method.Name): $($_.Exception.InnerException.Message)" -ForegroundColor Red
                $totalFailed++
            }
        }
    }
    catch {
        Write-Host "  Reflection prístup zlyhal, skúšam priamu Java reflexiu..." -ForegroundColor Gray
        
        $result = & $java -cp $classpath -Djunit.jupiter.execution.parallel.enabled=false `
            org.junit.platform.console.ConsoleLauncher `
            --select-class $test.Class `
            --disable-ansi-colors 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            $passed = ($result | Select-String "successful").Count
            $failed = ($result | Select-String "failed").Count
            $totalPassed += $passed
            $totalFailed += $failed
            Write-Host "  Passed: $passed, Failed: $failed"
        }
        else {
            Write-Host "  Spustenie zlyhalo"
        }
    }
    
    Write-Host ""
}

Write-Host "=== SÚHRN ===" -ForegroundColor Cyan
Write-Host "Úspešné: $totalPassed" -ForegroundColor Green
Write-Host "Neúspešné: $totalFailed" -ForegroundColor $(if ($totalFailed -eq 0) { "Green" } else { "Red" })
Write-Host ""

if ($totalFailed -eq 0) {
    Write-Host "✓ VŠETKY TESTY PREŠLI!" -ForegroundColor Green
    exit 0
}
else {
    Write-Host "✗ NIEKTORÉ TESTY ZLYHALI" -ForegroundColor Red
    exit 1
}
