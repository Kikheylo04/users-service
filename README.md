El proyecto se corre mediante este comando
# Opción A: con wrapper (recomendado)
./mvnw spring-boot:run

# Opción B: si tienes Maven instalado
mvn spring-boot:run
Lo puedes ver en la web gracias a swagger
- App: `http://localhost:8080`
- Swagger UI: **http://localhost:8080/swagger-ui.html**

### Ejemplos (cURL)

**1) Registrar**
curl -X POST "http://localhost:8080/api/users/register"   -H "Content-Type: application/json"   -d '{
        "firstName":"Sebas",
        "lastName":"Chavez",
        "username":"sebas",
        "password":"123456",
        "role":"ADMIN"
      }'
**2) Login**
curl -X POST "http://localhost:8080/api/users/login"   -H "Content-Type: application/json"   -d '{"username":"sebas","password":"123456"}'

Respuesta (200):
 json
{
  "responseCode": 200,
  "responseMessage": "Login Exitoso",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
}

**3) Perfil (token requerido)**
curl -X GET "http://localhost:8080/api/users/profile"   -H "Authorization: Bearer eyJhbGciOi..."

**4) Listado (ADMIN)**
curl -X GET "http://localhost:8080/api/users/"   -H "Authorization: Bearer eyJhbGciOi..."

## Probar en Swagger

1. Ir a **http://localhost:8080/swagger-ui.html**
2. Ejecutar **/register** para crear un usuario.
3. Ejecutar **/login** y copiar el token JWT.
4. Clic en **Authorize** (candado) y pegar:  
   `Bearer eyJhbGciOi...`
5. Probar endpoints protegidos.
