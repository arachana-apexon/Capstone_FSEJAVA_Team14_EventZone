# EventZone Database Connection Script
# Run this script to connect to Render PostgreSQL database

$DB_URL = "postgresql://eventzone_user:eSCzBsSj2uQsDF2tRUcrCdJSJSpZUHaM@dpg-d932bgtaeets73avf1rg-a.oregon-postgres.render.com/eventzone_jec1"

Write-Host "Connecting to EventZone Database on Render..." -ForegroundColor Green
psql $DB_URL
