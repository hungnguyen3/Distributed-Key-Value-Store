cd ..\..\..\

mvn clean compile assembly:single

for /l %%x in (1, 1, 25) do (
  start java -Xmx64m -jar target\CPEN431_2023_PROJECT_G6-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 1%%x 310
)

cd src\development_scripts\window
