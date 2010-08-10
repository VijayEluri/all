for f in ~/Library/Application\ Support/Firefox/Profiles/*/*.sqlite
do
  ls -l "$f"
  sqlite3 "$f" 'VACUUM;'
  ls -l "$f"
  echo ----
done
