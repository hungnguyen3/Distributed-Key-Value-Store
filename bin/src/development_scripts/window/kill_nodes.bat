lsof -i :10001-10020 | awk 'NR!=1 {print $2}' | xargs kill