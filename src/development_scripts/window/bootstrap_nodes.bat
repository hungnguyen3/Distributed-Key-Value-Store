cd ..\..\..\

mvn clean compile assembly:single

for /l %%x in (1, 1, 9) do (
  start java -Xmx64m -jar target/CPEN431_2023_PROJECT_G6-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 1000%%x 310
)

for /l %%x in (10, 1, 24) do (
  start java -Xmx64m -jar target/CPEN431_2023_PROJECT_G6-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 100%%x 310
)
