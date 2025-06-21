# Script para cargar datos MongoDB
Write-Host "=== CARGANDO DATOS MONGODB ===" -ForegroundColor Green

$mongoCode = @'
db.opiniones_clientes.deleteMany({});
db.logs_pedidos.deleteMany({});
db.historial_repartidores.deleteMany({});
db.navegacion_usuarios.deleteMany({});

const fs = require('fs');
const data = JSON.parse(fs.readFileSync('datos_mongodb.json', 'utf8'));

db.opiniones_clientes.insertMany(data.opiniones_clientes);
db.logs_pedidos.insertMany(data.logs_pedidos);
db.historial_repartidores.insertMany(data.historial_repartidores);
db.navegacion_usuarios.insertMany(data.navegacion_usuarios);

print('=== DATOS CARGADOS ===');
print('Opiniones: ' + db.opiniones_clientes.countDocuments());
print('Logs: ' + db.logs_pedidos.countDocuments());
print('Repartidores: ' + db.historial_repartidores.countDocuments());
print('Navegacion: ' + db.navegacion_usuarios.countDocuments());
print('=====================');
'@

$mongoCode | Out-File -FilePath "load_data.js" -Encoding UTF8

Write-Host "Ejecutando script..." -ForegroundColor Yellow
mongosh deliveryapp_mongo load_data.js

Remove-Item "load_data.js" -ErrorAction SilentlyContinue

Write-Host "Datos cargados!" -ForegroundColor Green
