cd ..\..\..\

for /l %%x in (1, 1, 5) do (
  start java -Xmx64m -jar target\CPEN431_2023_PROJECT_G6-1.0-SNAPSHOT-jar-with-dependencies.jar 127.0.0.1 1000%%x 310
)
