$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jdkTask = $env:JAVA_HOME
if (-not $jdkTask) {
    $javacTask = Get-Command javac -ErrorAction SilentlyContinue
    if ($javacTask) { $jdkTask = Split-Path -Parent (Split-Path -Parent $javacTask.Source) }
}
if (-not $jdkTask) { $jdkTask = Join-Path $env:ProgramFiles 'Apache NetBeans\jdk' }
if (-not (Test-Path -LiteralPath (Join-Path $jdkTask 'bin\javac.exe'))) {
    throw 'Configure JAVA_HOME com o JDK 26 utilizado no projeto.'
}
$classPathTask = (Join-Path $projectRoot 'build\classes') + ';' + (Join-Path $projectRoot 'lib\mysql-connector-j-8.4.0.jar')
if (-not (Test-Path -LiteralPath (Join-Path $projectRoot 'build\classes\br\com\assistencia\view\TelaLogin.class'))) {
    throw 'Compile o projeto no NetBeans antes de executar os testes.'
}
$testOutputTask = Join-Path $projectRoot 'build\test-fluxos'
New-Item -ItemType Directory -Path $testOutputTask -Force | Out-Null
$testSourcesTask = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'test') -Filter '*.java' -Recurse | ForEach-Object FullName)
& (Join-Path $jdkTask 'bin\javac.exe') -encoding UTF-8 -cp $classPathTask -d $testOutputTask $testSourcesTask
if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar os testes.' }
$previousTask = @{}
foreach ($keyTask in @('MYSQL_URL','MYSQL_USUARIO','MYSQL_SENHA','MYSQL_TESTE_URL_REAL')) {
    $previousTask[$keyTask] = [Environment]::GetEnvironmentVariable($keyTask, 'Process')
}
try {
    foreach ($keyTask in @('MYSQL_USUARIO','MYSQL_SENHA','MYSQL_URL')) {
        if ($null -eq [Environment]::GetEnvironmentVariable($keyTask, 'Process')) {
            [Environment]::SetEnvironmentVariable($keyTask, [Environment]::GetEnvironmentVariable($keyTask, 'User'), 'Process')
        }
    }
    $env:MYSQL_TESTE_URL_REAL = $env:MYSQL_URL
    if ([string]::IsNullOrWhiteSpace($env:MYSQL_TESTE_URL_REAL)) {
        $env:MYSQL_TESTE_URL_REAL = 'jdbc:mysql://127.0.0.1:3306/assistencia_tecnica?connectTimeout=5000&socketTimeout=10000'
    }
    $env:MYSQL_URL = 'jdbc:assistencia-teste'
    & (Join-Path $jdkTask 'bin\java.exe') '-Dfile.encoding=UTF-8' -cp ($testOutputTask + ';' + $classPathTask) br.com.assistencia.VerificarFluxos
    if ($LASTEXITCODE -ne 0) { throw 'Os testes encontraram uma falha.' }
} finally {
    foreach ($keyTask in $previousTask.Keys) {
        [Environment]::SetEnvironmentVariable($keyTask, $previousTask[$keyTask], 'Process')
    }
}
