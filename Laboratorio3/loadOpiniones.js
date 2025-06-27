const fs = require("fs");
const data = JSON.parse(fs.readFileSync("datosOpiniones_mongodb.json", "utf8"));

db.opiniones_clientes.deleteMany({});
db.opiniones_clientes.insertMany(data.opiniones_clientes);

print("Opiniones cargadas: " + db.opiniones_clientes.countDocuments());
